package de.dedede.model.persistence.exceptions;

/**
 * This checked exception describes situations in which a medium, as represented
 * by a data entry, is absent from the data store. This generally means that
 * an attempt to reference an entity by an ID was carried out with an ID that
 * doesn't refer to anything. Sometimes, this may be the result of a race-condition.
 */
public class MediumDoesNotExistException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new MediumDoesNotExistException instance.
     */
    public MediumDoesNotExistException() {
        super();
    }

    /**
     * Creates a new MediumDoesNotExistException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public MediumDoesNotExistException(String msg) {
        super(msg);
    }

    /**
     * Creates a new MediumDoesNotExistException instance with a
     * display message identifying the specific cause.
     *
     * @param msg    The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *               (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public MediumDoesNotExistException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
