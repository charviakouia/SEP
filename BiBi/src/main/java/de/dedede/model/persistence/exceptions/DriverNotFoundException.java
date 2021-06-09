package de.dedede.model.persistence.exceptions;

/**
 * This checked exception describes situations in which in which a driver
 * for accessing the database was not found at the specified path.
 * Sometimes, this may be the result of a race-condition.
 */
public class DriverNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new DriverNotFoundException instance.
     */
    public DriverNotFoundException() {
        super();
    }

    /**
     * Creates a new DriverNotFoundException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public DriverNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Creates a new DriverNotFoundException instance with a
     * display message identifying the specific cause.
     *
     * @param msg   The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DriverNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
