package dedede.model.logic.exceptions;
/**
 * Thrown if the server could not send the {@link dedede.model.logic.managedbeans.EmailConfirmation} or
 * {@link dedede.model.logic.managedbeans.PasswordReset}.
 */
public class MailException  extends Exception{

	/**
	 * Constructs a new MailException.
	 */
	public MailException() {

	}

	/**
	 * Constructs a new MailException with the specified detail message.
	 *
	 * @param message The message which is set to the MailException is initialized by the Exception
	 *    class.
	 */
	public MailException(String message) {
		super(message);
	}

}
