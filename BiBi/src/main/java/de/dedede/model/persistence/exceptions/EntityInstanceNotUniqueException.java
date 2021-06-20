package de.dedede.model.persistence.exceptions;

import java.util.ResourceBundle;

import de.dedede.model.data.exceptions.AnnotatedException;
import jakarta.faces.context.FacesContext;

/**
 * This checked exception describes situations in which the creation of an entity is
 * attempted with an ID, that is already associated with another entity in
 * the same data store. This generally means that the given ID already exists.
 * Sometimes, this may be the result of a race-condition.
 */
public class EntityInstanceNotUniqueException extends Exception implements AnnotatedException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new EntityInstanceNotUniqueException instance.
     */
    public EntityInstanceNotUniqueException() {
        super();
    }

    /**
     * Creates a new EntityInstanceNotUniqueException instance with a
     * display message identifying the specific cause.
     *
     * @param msg The message indicating the exception's cause.
     */
    public EntityInstanceNotUniqueException(String msg) {
        super(msg);
    }

    /**
     * Creates a new EntityInstanceNotUniqueException instance with a
     * display message identifying the specific cause.
     *
     * @param msg   The message indicating the exception's cause.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public EntityInstanceNotUniqueException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    @Override
	public String getPersonalizedMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
    	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
    	return bundle.getString("exception.entityInstanceNotUnique");
	}

}
