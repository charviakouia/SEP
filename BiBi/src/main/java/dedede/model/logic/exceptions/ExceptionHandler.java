package dedede.model.logic.exceptions;
/**
 * CustomExceptionHandler is the central point for handling unchecked and
 * checked Exceptions that are thrown during the Faces lifecycle. Unchecked
 * exceptions are handled by redirecting to the error page. Checked exceptions
 * are handled by creating a FacesMessage that is displayed to the
 * {@link dedede.model.data.dtos.UserDto}.
 */
public class ExceptionHandler {

	/**
	 * Logs the error and creates/displays a FacesMessage.
	 *
	 * @param exception The exception to be handled.
	 */
	public void handle(Throwable exception) {
		
	}

}
