package de.dedede.model.logic.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import de.dedede.model.logic.managed_beans.EmailConfirmation;
import de.dedede.model.logic.managed_beans.PasswordReset;
import jakarta.faces.context.FacesContext;

/**
 * This checked exception describes a situation where an administrator or employee
 * tries to set an invalid (impossible) deadline for a medium, copy, or user.
 */
public class DeadlineException extends Exception implements AnnotatedException {

    /**
     * Constructs a new DeadlineException.
     */
    public DeadlineException() {

    }

    /**
     * Constructs a new DeadlineException with the specified detail message.
     *
     * @param message The message which is set to the MailException is initialized
     *                by the Exception class.
     */
    public DeadlineException(String message) {
        super(message);
    }

    /**
     * Constructs a new DeadlineException with the specified detail message.
     *
     * @param message The message which is set to the MailException is initialized
     *                by the Exception class.
     * @param cause   the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DeadlineException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getPersonalizedMessage() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.deadline");
    }
    
}
