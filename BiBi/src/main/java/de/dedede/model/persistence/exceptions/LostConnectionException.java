package de.dedede.model.persistence.exceptions;

import de.dedede.model.persistence.util.ConnectionPool;

/**
 * This checked exception is used for situations in which a connection
 * object from the connection pool can no longer be used to execute
 * data queries.
 * <p>
 * See the {@link ConnectionPool} class for the connection pool implementation.
 */
public class LostConnectionException extends RuntimeException {

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
     * @param msg The message indicating the exception's cause.
     */
    public LostConnectionException(String msg) {
        super(msg);
    }

    /**
     * Creates a new LostConnectionException instance with a
     * display message identifying the specific cause.
     *
     * @param msg   The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public LostConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
