package de.dedede.model.persistence.exceptions;

/**
 * This checked exception describes situations in which a category, as represented
 * by a data entry, is absent from the data store. This generally means that
 * an attempt to reference an entity by an ID was carried out with an ID that
 * doesn't refer to anything. Sometimes, this may be the result of a race-condition.
 */
public class CategoryDoesNotExistException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new CategoryDoesNotExistException instance.
     */
    public CategoryDoesNotExistException() {
        super();
    }

    /**
     * Creates a new CategoryDoesNotExistException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public CategoryDoesNotExistException(String msg) {
        super(msg);
    }

    /**
     * Creates a new CategoryDoesNotExistException instance with a
     * display message identifying the specific cause.
     *
     * @param msg    The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *               (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CategoryDoesNotExistException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
