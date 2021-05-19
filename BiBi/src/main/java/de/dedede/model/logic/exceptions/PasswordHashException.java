package de.dedede.model.logic.exceptions;

/**
 * Thrown if the initial admin couldn't be added.
 */
public class PasswordHashException extends RuntimeException {

	/**
	 * Constructs a new InitialAdminException.
	 */
	public PasswordHashException() {

	}

	/**
	 * Constructs a new InitialAdminException with the specified detail message.
	 *
	 * @param message The message which is set to the InitialAdminException is
	 *                initialized by the RuntimeException class.
	 */
	public PasswordHashException(String message) {
		super(message);
	}
}
