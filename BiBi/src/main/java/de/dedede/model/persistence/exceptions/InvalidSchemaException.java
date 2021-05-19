package de.dedede.model.persistence.exceptions;

/**
 * This unchecked exception is used when access to an entity is attempted,
 * whose structure isn't defined in the data store. This generally means that 
 * the schema that defines the data store is incorrectly configured.
 *
 */
public class InvalidSchemaException extends RuntimeException {

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
	 * @param msg
	 */
	public InvalidSchemaException(String msg) {
		super(msg);
	}
	
}
