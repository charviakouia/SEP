package de.dedede.model.persistence.exceptions;

import de.dedede.model.persistence.util.ConnectionPool;

/**
 * This checked exception is used for situations in which a connection
 * object is requested from the connection pool while all connections 
 * are already in use. 
 * 
 * See the {@link ConnectionPool} class for the connection pool implementation.
 *
 */
public class MaxConnectionsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new MaxConnectionsException instance.
	 */
	public MaxConnectionsException() {
		super();
	}
	
	/**
	 * Creates a new MaxConnectionsException instance with a
	 * display message identifying the specific cause.
	 * 
	 * @param msg The message indicating the exception's cause.
	 */
	public MaxConnectionsException(String msg) {
		super(msg);
	}

}
