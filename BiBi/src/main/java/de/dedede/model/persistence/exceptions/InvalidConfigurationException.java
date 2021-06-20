package de.dedede.model.persistence.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import jakarta.faces.context.FacesContext;

/**
 * This checked exception describes situations in which in which in which the config reader
 * cannot read the configuration file because the configuration file is invalid.
 */
public class InvalidConfigurationException extends RuntimeException implements AnnotatedException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidConfigurationException instance.
     */
    public InvalidConfigurationException() {
        super();
    }

    /**
     * Creates a new InvalidConfigurationException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Creates a new InvalidConfigurationException instance with a
     * display message identifying the specific cause.
     *
     * @param msg    The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *               (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public InvalidConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    @Override
	public String getPersonalizedMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.invalidConfiguration");
	}

}
