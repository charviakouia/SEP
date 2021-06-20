package de.dedede.model.persistence.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import jakarta.faces.context.FacesContext;

/**
 * This checked exception describes situations in which in which in which the config reader
 * cannot read the configuration file because the configuration file does not exist.
 */
public class ConfigurationDoesNotExistException extends Exception implements AnnotatedException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConfigurationDoesNotExistException instance.
     */
    public ConfigurationDoesNotExistException() {
        super();
    }

    /**
     * Creates a new ConfigurationDoesNotExistException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public ConfigurationDoesNotExistException(String msg) {
        super(msg);
    }

    /**
     * Creates a new ConfigurationDoesNotExistException instance with a
     * display message identifying the specific cause.
     *
     * @param msg    The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *               (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ConfigurationDoesNotExistException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    @Override
	public String getPersonalizedMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.configurationDoesNotExist");
	}

}
