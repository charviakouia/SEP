package de.dedede.model.persistence.exceptions;

import de.dedede.model.persistence.util.ConnectionPool;

/**
 * This checked exception is used for situations in which a connection
 * object from the connection pool can no longer be used to execute
 * data queries.
 * 
 * See the {@link ConnectionPool} class for the connection pool implementation.
 *
 */
public class LostConnectionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new LostConnectionException instance.
	 */
	public LostConnectionException() {
		super();
	}
	
	/**
	 * Creates a new LostConnectionException instance with a
	 * display message identifying the specific cause.
	 * 
	 * @param msg
	 */
	public LostConnectionException(String msg) {
		super(msg);
	}
	
}
