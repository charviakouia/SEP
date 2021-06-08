package de.dedede.model.persistence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

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
	private static final Queue<Connection> queue = new ConcurrentLinkedQueue<>();
	private static final Collection<Connection> backupList = new ConcurrentLinkedQueue<>();
	private static volatile boolean isShutDown = true;
	private static final Semaphore lifecycleSemaphore = new Semaphore(2, true);
	private static final long ACQUIRING_CONNECTION_PERIOD = 5000;

	private static final String DB_CAPACITY_KEY = "DB_CAPACITY";
	private static final int DEFAULT_DB_CAPACITY = 5;
	private static final String DB_DRIVER_KEY = "DB_DRIVER";
	private static final String DB_HOST_KEY = "DB_HOST";
	private static final String DB_NAME_KEY = "DB_NAME";
	private static final String DB_PORT_KEY = "DB_PORT";
	private static final String DB_URL_KEY = "DB_URL";
	private static final String DB_USER_KEY = "DB_USER";
	private static final String DB_PASSWORD_KEY = "DB_PASSWORD";
	private static final String DB_SSL_FACTORY_KEY = "DB_SSL_FACTORY";
	private static final String DB_SSL_KEY = "DB_SSL";

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
		if (!isShutDown){
			lifecycleSemaphore.release(2);
			throw new IllegalStateException();
		}
		int maxConnections = readDBCapacity();
		try {
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
		for (Connection conn : backupList){
			attemptToCloseConnection(conn);
		}
		queue.clear();
		backupList.clear();
		lifecycleSemaphore.release(2);
	}

	private static int readDBCapacity(){
		Properties props = ConfigReader.getInstance().getSystemConfigurations();
		int maxConnections = -1;
		try { maxConnections = Integer.parseInt(props.getProperty(DB_CAPACITY_KEY)); }
		catch (NumberFormatException ignored){}
		if (maxConnections < 0){
			Logger.severe("No suitable configuration for the maximum DB capacity was found... " +
					"Using default value of: " + DEFAULT_DB_CAPACITY);
			return DEFAULT_DB_CAPACITY;
		} else {
			return maxConnections;
		}
	}

	private static void populateQueue(int numConnections) throws ClassNotFoundException, SQLException {
		Properties props = ConfigReader.getInstance().getSystemConfigurations();
		Properties connProps = new Properties();
		connProps.setProperty("user", props.getProperty(DB_USER_KEY));
		connProps.setProperty("password", props.getProperty(DB_PASSWORD_KEY));
		String sslEnabled = props.getProperty(DB_SSL_KEY);
		if (sslEnabled.toLowerCase(Locale.ROOT).equals("true")){
			connProps.setProperty("ssl", "true");
			connProps.setProperty("sslfactory", props.getProperty(DB_SSL_FACTORY_KEY));
		} else {
			connProps.setProperty("ssl", "false");
		}
		Class.forName(props.getProperty(DB_DRIVER_KEY));
		String url = props.getProperty(DB_URL_KEY) + props.getProperty(DB_HOST_KEY) + ":" +
				props.getProperty(DB_PORT_KEY) + "/" + props.getProperty(DB_NAME_KEY);
		for (int i = 0; i < numConnections; i++){
			Connection conn = DriverManager.getConnection(url, connProps);
			queue.offer(conn);
			backupList.add(conn);
		}
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
		if (isShutDown){
			lifecycleSemaphore.release(1);
			throw new IllegalStateException("Pool is shut down");
		}
		Connection result = waitForConnection(timeout);
		lifecycleSemaphore.release(1);
		if (result == null){
			throw new MaxConnectionsException("No connections available");
		} else {
			return result;
		}
	}

	private Connection waitForConnection(long timeout){
		Connection result = queue.poll();
		long waitedTime = 0;
		while (result == null && waitedTime < timeout){
			try {
				wait(getAcquiringConnectionPeriod());
			} catch (InterruptedException ignored){}
			waitedTime += getAcquiringConnectionPeriod();
		}
		return result;
	}

	/**
	 * Returns a connection to the pool for use by other callers.
	 * Any uncommitted operation is not guaranteed to be executed.
	 * 
	 * @param conn The connection to be returned.
	 */
	public void releaseConnection(Connection conn) {
		lifecycleSemaphore.acquireUninterruptibly(1);
		if (isShutDown){ return; }
		try {
			if (!conn.getAutoCommit()){ conn.rollback(); }
			queue.offer(conn);
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

	public static long getAcquiringConnectionPeriod() {
		return ACQUIRING_CONNECTION_PERIOD;
	}

}
