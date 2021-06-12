package de.dedede.model.persistence.exceptions;

public class InvalidUserForCopyException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidUserForCopyException() {super();}
	
	public InvalidUserForCopyException(String msg) {super(msg);}
	
	public InvalidUserForCopyException(String msg, Exception e) {super(msg, e);}
}
