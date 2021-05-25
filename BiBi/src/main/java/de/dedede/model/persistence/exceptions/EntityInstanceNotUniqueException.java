package de.dedede.model.persistence.exceptions;

/**
 * This checked exception describes situations in which the creation of an entity is
 * attempted with an ID, that is already associated with another entity in
 * the same data store. This generally means that the given ID already exists.
 * Sometimes, this may be the result of a race-condition.
 *
 */
public class EntityInstanceNotUniqueException extends Exception {

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
	
}
