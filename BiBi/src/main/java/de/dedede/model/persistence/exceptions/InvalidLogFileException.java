package de.dedede.model.persistence.exceptions;

/**
 * This checked exception describes situations in which in which the logger can not create,
 * find or write an entry in the log file.
 */
public class InvalidLogFileException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidLogFileException instance.
     */
    public InvalidLogFileException() {
        super();
    }

    /**
     * Creates a new InvalidLogFileException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public InvalidLogFileException(String msg) {
        super(msg);
    }

    /**
     * Creates a new InvalidLogFileException instance with a
     * display message identifying the specific cause.
     *
     * @param msg    The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *               (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public InvalidLogFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
