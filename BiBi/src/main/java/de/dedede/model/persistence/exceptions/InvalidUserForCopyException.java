package de.dedede.model.persistence.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import jakarta.faces.context.FacesContext;

public class InvalidUserForCopyException extends Exception implements AnnotatedException {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidUserForCopyException() {super();}
	
	public InvalidUserForCopyException(String msg) {super(msg);}
	
	public InvalidUserForCopyException(String msg, Exception e) {super(msg, e);}
	
	@Override
	public String getPersonalizedMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.invalidUserForCopy");
	}
	
}
