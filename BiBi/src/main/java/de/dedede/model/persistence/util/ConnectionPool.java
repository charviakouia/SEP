package de.dedede.model.persistence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.faces.config.ConfigurationException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
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
	private static Queue<Connection> queue = null;
	private static List<Connection> backupList = null;

	private static final String DB_CAPACITY_KEY = "DB_CAPACITY";
	private static final int DEFAULT_DB_CAPACITY = 10;
	private static final String DB_DRIVER_KEY = "DB_DRIVER";
	private static final String DB_HOST_KEY = "DB_HOST";
	private static final String DB_NAME_KEY = "DB_NAME";
	private static final String DB_PORT_KEY = "DB_PORT";
	private static final String DB_URL_KEY = "DB_URL";
	private static final String DB_USER_KEY = "DB_USER";
	private static final String DB_PASSWORD_KEY = "DB_PASSWORD";
	private static final String DB_SSL_FACTORY_KEY = "DB_SSL_FACTORY";

	private ConnectionPool() {}

	/**
	 * Initializes the connection pool and its connection objects.
	 * If this fails, an exception is thrown. The pool must be
	 * initialized in order to be used.
	 *
	 * @throws LostConnectionException	is thrown when an error occurs when creating
	 * 									the connections
	 * @throws ConfigurationException	is thrown when a necessary configuration, like the driver,
	 * 									could not be located
	 */
	public static void setUpConnectionPool() throws LostConnectionException, ConfigurationException {
		queue = new ConcurrentLinkedQueue<>();
		backupList = new LinkedList<>();
		int maxConnections = readDBCapacity();
		try {
			populateQueue(maxConnections);
		} catch (ClassNotFoundException e){
			Logger.severe("Couldn't locate the driver");
			throw new ConfigurationException("Couldn't locate the driver");
		} catch (SQLException e){
			Logger.severe("Connection pool couldn't be initialized");
			destroyConnectionPool();
			throw new LostConnectionException("Connection pool couldn't be initialized");
		}
		Logger.detailed("Connection pool successfully initialized");
	}

	/**
	 * Closes the connection pool as well as any active connection objects.
	 * It is not guaranteed that uncommitted operations on active connections
	 * will be executed.
	 *
	 */
	public static void destroyConnectionPool() {
		Logger.detailed("Closing connection pool...");
		for (Connection conn : backupList){
			try { conn.close(); } catch (SQLException ignore) { }
		}
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
		connProps.setProperty("ssl", "true");
		connProps.setProperty("sslfactory", props.getProperty(DB_SSL_FACTORY_KEY));
		Class.forName(props.getProperty(DB_DRIVER_KEY));
		String url = String.format(props.getProperty(DB_URL_KEY), props.getProperty(DB_HOST_KEY),
				props.getProperty(DB_PORT_KEY), props.getProperty(DB_NAME_KEY));
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
	 * due to the limitation on their total quantity, an exception is thrown.
	 * 
	 * @return A connection for executing queries on the app's data store.
	 * @throws MaxConnectionsException Is thrown if no connection is available.
	 */
	public Connection fetchConnection() throws MaxConnectionsException {
		Connection result = queue.poll();
		if (result == null){
			throw new MaxConnectionsException("No connections available");
		} else {
			return result;
		}
	}

	/**
	 * Returns a connection to the pool for use by other callers.
	 * Any uncommitted operation is not guaranteed to be executed.
	 * 
	 * @param conn The connection to be returned.
	 */
	public void releaseConnection(Connection conn) {
		try {
			if (!conn.getAutoCommit()){
				conn.rollback();
			}
			queue.offer(conn);
		} catch (SQLException e){
			Logger.severe("A connection appears to be damaged...");
			backupList.remove(conn);
			try { conn.close(); } catch (SQLException ignored){}
		}
	}

}
