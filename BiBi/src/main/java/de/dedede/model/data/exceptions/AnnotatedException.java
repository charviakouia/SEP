package de.dedede.model.data.exceptions;

/**
 * Used by all exceptions in the system to describe their function and the circumstances in which a user might
 * encounter them. This message is shown in the dynamic error page.
 *
 * @author Ivan Charviakou
 */
public interface AnnotatedException {
	
	String getPersonalizedMessage();
	
}
