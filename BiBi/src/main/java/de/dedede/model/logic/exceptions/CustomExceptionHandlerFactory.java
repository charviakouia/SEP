package de.dedede.model.logic.exceptions;


import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {
	
	private ExceptionHandlerFactory factory;
	
	public CustomExceptionHandlerFactory() {}
	
	public CustomExceptionHandlerFactory(ExceptionHandlerFactory factory) {
		this.factory = factory;
	}
	
    @Override
    public ExceptionHandler getExceptionHandler() {
        return new CustomExceptionHandler(factory.getExceptionHandler());
    }
    
}
