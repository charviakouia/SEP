package de.dedede.model.logic.exceptions;

/**
 * The MyBusinessException wraps all checked standard Java exception and enriches them with a custom error code.
 */
public class BusinessException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BusinessException.
     */
    public BusinessException() {

    }

    /**
     * Constructs a new BusinessException with the specified detail message.
     *
     * @param message The message which is set to the MailException is initialized
     *                by the Exception class.
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessException with the specified detail message.
     *
     * @param message The message which is set to the MailException is initialized
     *                by the Exception class.
     * @param cause   the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
