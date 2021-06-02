package de.dedede.model.logic.exceptions;

import de.dedede.model.data.dtos.UserDto;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;

/**
 * CustomExceptionHandler is the central point for handling unchecked and
 * checked Exceptions that are thrown during the Faces lifecycle. Unchecked
 * exceptions are handled by redirecting to the error page. Checked exceptions
 * are handled by creating a FacesMessage that is displayed to the
 * {@link UserDto}.
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private final ExceptionHandler wrapped;

	public CustomExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = null;
	}

	public static void handleCheckedException() {

	}
	/**
	 * Logs the error and creates/displays a FacesMessage.
	 */
	@Override
	public void handle() {

	}

}
