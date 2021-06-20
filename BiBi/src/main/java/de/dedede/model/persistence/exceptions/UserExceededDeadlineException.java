package de.dedede.model.persistence.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import jakarta.faces.context.FacesContext;

public class UserExceededDeadlineException extends Exception implements AnnotatedException {
	
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
	
	@Override
	public String getPersonalizedMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.userExceededDeadline");
	}

}
