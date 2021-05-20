package de.dedede.model.persistence.util;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

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
	private Queue<Connection> connections = null;
	private final AtomicInteger maxConnections = new AtomicInteger();

	private ConnectionPool() {}

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
		return null;
	}

	/**
	 * Returns a connection to the pool for use by other callers.
	 * Any uncommitted operation is not guaranteed to be executed.
	 * 
	 * @param conn The connection to be returned.
	 */
	public void releseConnection(Connection conn) {

	}

	/**
	 * Initializes the connection pool and its connection objects.
	 * If this fails, an exception is thrown. The pool must be
	 * initialized in order to be used.
	 * 
	 * @throws LostConnectionException
	 */
	public void setUpConnectionPool() throws LostConnectionException {

	}

	/**
	 * Closes the connection pool as well as any active connection objects. 
	 * It is not guaranteed that uncommitted operations on active connections
	 * will be executed.
	 * 
	 */
	public void destroyConnectionPool() {

	}

}
