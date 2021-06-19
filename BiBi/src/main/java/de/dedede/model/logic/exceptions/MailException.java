package de.dedede.model.logic.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import de.dedede.model.logic.managed_beans.EmailConfirmation;
import de.dedede.model.logic.managed_beans.PasswordReset;
import jakarta.faces.context.FacesContext;

/**
 * Thrown if the server could not send the {@link EmailConfirmation} or
 * {@link PasswordReset}.
 */
public class MailException extends Exception implements AnnotatedException {

    /**
     * Constructs a new MailException.
     */
    public MailException() {

    }

    /**
     * Constructs a new MailException with the specified detail message.
     *
     * @param message The message which is set to the MailException is initialized
     *                by the Exception class.
     */
    public MailException(String message) {
        super(message);
    }

    /**
     * Constructs a new MailException with the specified detail message.
     *
     * @param message The message which is set to the MailException is initialized
     *                by the Exception class.
     * @param cause   the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getPersonalizedMessage() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.mail");
    }
    
}
