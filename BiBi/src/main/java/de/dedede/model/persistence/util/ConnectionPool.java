package de.dedede.model.persistence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;

/**
 * This pool manages the creation and distribution of connection-objects.
 * These are then used to perform SQL-queries on the application's data store.
 * Once the connection-operations have been committed, the connection must be 
 * returned to the pool to be reused. The maximum number of connections is specified 
 * by the application's global configurations.
 *
 */
public class ConnectionPool {

	private static ConnectionPool INSTANCE = null;
	private static Queue<Connection> queue = new ConcurrentLinkedQueue<>();

	private static Collection<Connection> backupList = new ConcurrentLinkedQueue<>();
	private static volatile boolean isShutDown = true;
	private static Semaphore lifecycleSemaphore = new Semaphore(2, true);
	private static Semaphore connectionSemaphore = new Semaphore(0, true);

	private ConnectionPool() {}

	/**
	 * Initializes the connection pool and its connection objects.
	 * If this fails, an exception is thrown. The pool must be
	 * initialized in order to be used.
	 *
	 * @throws SQLException				Is thrown when an error occurs when creating
	 * 									the connections
	 * @throws ClassNotFoundException	Is thrown when a necessary configuration, like the driver,
	 * 									could not be located
	 */
	public static void setUpConnectionPool() throws ClassNotFoundException, SQLException {
		lifecycleSemaphore.acquireUninterruptibly(2);
		try {
			if (!isShutDown) { throw new IllegalStateException(); }
			int maxConnections = readDBCapacity();
			populateQueue(maxConnections);
			isShutDown = false;
			Logger.detailed("Connection pool initialized successfully");
		} catch (ClassNotFoundException e){
			Logger.severe("Couldn't locate the driver");
			throw e;
		} catch (SQLException e){
			Logger.severe("Connection pool couldn't be initialized");
			destroyConnectionPool();
			throw e;
		} finally {
			lifecycleSemaphore.release(2);
		}
	}

	/**
	 * Closes the connection pool as well as any active connection objects.
	 * It is not guaranteed that uncommitted operations on active connections
	 * will be executed.
	 *
	 */
	public static void destroyConnectionPool() {
		lifecycleSemaphore.acquireUninterruptibly(2);
		isShutDown = true;
		Logger.detailed("Closing connection pool...");
		for (Connection conn : backupList){ attemptToCloseConnection(conn); }
		queue.clear();
		backupList.clear();
		lifecycleSemaphore.release(2);
	}

	private static int readDBCapacity(){
		int maxConnections = -1;
		try { maxConnections = Integer.parseInt(ConfigReader.getInstance().getKey("DB_CAPACITY")); }
		catch (NumberFormatException ignored){}
		if (maxConnections < 0){
			String msg = "No suitable configuration for the maximum DB capacity was found... ";
			Logger.severe(msg);
			throw new InvalidConfigurationException(msg);
		} else {
			return maxConnections;
		}
	}

	private static void populateQueue(int numConnections) throws ClassNotFoundException, SQLException {
		Properties connProps = new Properties();
		connProps.setProperty("user", ConfigReader.getInstance().getKey("DB_USER"));
		connProps.setProperty("password", ConfigReader.getInstance().getKey("DB_PASSWORD"));
		String sslEnabled = ConfigReader.getInstance().getKey("DB_SSL");
		if (sslEnabled.toLowerCase(Locale.ROOT).equals("true")){
			connProps.setProperty("ssl", "true");
			connProps.setProperty("sslfactory", ConfigReader.getInstance().getKey("DB_SSL_FACTORY"));
		} else {
			connProps.setProperty("ssl", "false");
		}
		Class.forName(ConfigReader.getInstance().getKey("DB_DRIVER"));
		String url = ConfigReader.getInstance().getKey("DB_URL") + 
				ConfigReader.getInstance().getKey("DB_HOST") + ":" +
				ConfigReader.getInstance().getKey("DB_PORT") + "/" + 
				ConfigReader.getInstance().getKey("DB_NAME");
		for (int i = 0; i < numConnections; i++){
			Connection conn = DriverManager.getConnection(url, connProps);
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			queue.offer(conn);
			backupList.add(conn);
		}
		connectionSemaphore.release(numConnections);
	}

	/**
	 * Returns the single instance of the ConnectionPool.
	 * 
	 * @return The singleton ConnectionPool instance
	 */
	public static ConnectionPool getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ConnectionPool();
		}
		return INSTANCE;
	}

	/**
	 * Issues a connection to the caller. If a no connection is available
	 * because of the limitation on their total quantity, an exception is thrown.
	 *
	 * @param timeout The number of milliseconds to wait for a connection to
	 *                become available
	 * @return A connection for executing queries on the app's data store.
	 * @throws MaxConnectionsException Is thrown if no connection is available.
	 */
	public Connection fetchConnection(long timeout) throws MaxConnectionsException {
		lifecycleSemaphore.acquireUninterruptibly(1);
		try {
			if (isShutDown) { throw new IllegalStateException("Pool is shut down"); }
			Connection conn = waitForConnection(timeout);
			if (conn == null) { throw new MaxConnectionsException("No connections available"); }
			return conn;
		} finally {
			lifecycleSemaphore.release(1);
		}
	}

	private Connection waitForConnection(long timeout){
		try {
			connectionSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
			return queue.poll();
		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * Returns a connection to the pool for use by other callers.
	 * Any uncommitted operation is not guaranteed to be executed.
	 * 
	 * @param conn The connection to be returned.
	 */
	public void releaseConnection(Connection conn) {
		lifecycleSemaphore.acquireUninterruptibly(1);
		try {
			if (isShutDown){ return; }
			if (!conn.getAutoCommit()){ conn.rollback(); }
			queue.offer(conn);
			connectionSemaphore.release();
		} catch (SQLException e){
			Logger.severe("A connection appears to be damaged...");
			backupList.remove(conn);
			attemptToCloseConnection(conn);
			try {
				populateQueue(1);
			} catch (ClassNotFoundException | SQLException e2){
				Logger.detailed("A broken connection couldn't be replaced");
			}
		} finally {
			lifecycleSemaphore.release(1);
		}
	}

	private static void attemptToCloseConnection(Connection conn){
		try {
			conn.close();
		} catch (SQLException e){
			Logger.detailed("A broken connection couldn't be closed...");
		}
	}

}
