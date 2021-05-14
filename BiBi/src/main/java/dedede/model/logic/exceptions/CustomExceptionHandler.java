package dedede.model.logic.exceptions;

import jakarta.faces.context.ExceptionHandlerWrapper;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {
	
	private final ExceptionHandler wrapped;

	public CustomExceptionHandler(ExceptionHandler wrapped){
		this.wrapped = null;
	}

	public static void handleCheckedException() {
		
	}
	@Override
	public void handle() {
		
	}

}
