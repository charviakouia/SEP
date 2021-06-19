package de.dedede.model.persistence.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import jakarta.faces.context.FacesContext;

/**
 * This unchecked exception is used when access to an entity is attempted,
 * whose structure isn't defined in the data store. This generally means that
 * the schema that defines the data store is incorrectly configured.
 */
public class InvalidSchemaException extends RuntimeException implements AnnotatedException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidSchemaException instance.
     */
    public InvalidSchemaException() {
        super();
    }

    /**
     * Creates a new InvalidSchemaException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public InvalidSchemaException(String msg) {
        super(msg);
    }

    /**
     * Creates a new InvalidSchemaException instance with a
     * display message identifying the specific cause.
     *
     * @param msg   The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public InvalidSchemaException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    @Override
	public String getPersonalizedMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.invalidSchema");
	}

}
