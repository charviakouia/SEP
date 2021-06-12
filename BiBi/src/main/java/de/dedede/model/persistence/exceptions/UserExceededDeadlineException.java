package de.dedede.model.persistence.exceptions;

public class UserExceededDeadlineException extends Exception {
	
    private static final long serialVersionUID = 1L;

	public UserExceededDeadlineException() {
	}

	public UserExceededDeadlineException(String message) {
		super(message);
	}

	public UserExceededDeadlineException(Throwable cause) {
		super(cause);
	}

	public UserExceededDeadlineException(String message, Throwable cause) {
		super(message, cause);
	}

}
