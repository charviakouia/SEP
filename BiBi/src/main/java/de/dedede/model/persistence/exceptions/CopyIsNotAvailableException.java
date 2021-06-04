package de.dedede.model.persistence.exceptions;

/**
 * This checked exception describes situations in which in which a user was available to lend a copy,
 * but at the same time another user lent the copy. Sometimes, this may be the result of a race-condition.
 */
public class CopyIsNotAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new CopyIsNotAvailableException instance.
     */
    public CopyIsNotAvailableException() {
        super();
    }

    /**
     * Creates a new CopyIsNotAvailableException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public CopyIsNotAvailableException(String msg) {
        super(msg);
    }

    /**
     * Creates a new CopyIsNotAvailableException instance with a
     * display message identifying the specific cause.
     *
     * @param msg   The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CopyIsNotAvailableException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
