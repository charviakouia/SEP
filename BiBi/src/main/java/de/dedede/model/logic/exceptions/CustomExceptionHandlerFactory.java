package de.dedede.model.logic.exceptions;


import jakarta.faces.context.ExceptionHandler;

public class CustomExceptionHandlerFactory extends jakarta.faces.context.ExceptionHandlerFactory {
    @Override
    public ExceptionHandler getExceptionHandler() {
        return null;
    }
}
