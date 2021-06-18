package de.dedede.model.logic.exceptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dedede.model.data.dtos.ErrorDto;
import de.dedede.model.data.dtos.UserDto;
import jakarta.faces.FacesException;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.component.UIViewParameter;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import jakarta.faces.view.ViewMetadata;

/**
 * CustomExceptionHandler is the central point for handling unchecked and
 * checked Exceptions that are thrown during the Faces lifecycle. Unchecked
 * exceptions are handled by redirecting to the error page. Checked exceptions
 * are handled by creating a FacesMessage that is displayed to the
 * {@link UserDto}.
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapped;

	public CustomExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}
	
	@Override
	public void handle() throws FacesException {
		
		Iterator<ExceptionQueuedEvent> queue = getUnhandledExceptionQueuedEvents().iterator();
		List<ErrorDto> dtos = iterateEvents(queue);
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = context.getViewRoot();
		
		if (dtos != null) {
            
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            requestMap.put("errorDtos", dtos.toArray(new ErrorDto[0]));
            // requestMap.put("previousUrl", context.getExternalContext().getRequestContextPath() + viewRoot.getViewId());
            requestMap.put("previousUrl", viewRoot.getViewId());
            requestMap.put("parameterMap", getParameterMap());
            
            NavigationHandler nav = context.getApplication().getNavigationHandler();
    		nav.handleNavigation(context, null, "/view/public/error");
    		
    		context.renderResponse();
    		
		}
		
		getWrapped().handle();
		
	}
	
	private Map<String, String> getParameterMap(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> result = new HashMap<>();
		
		for (UIViewParameter p : ViewMetadata.getViewParameters(FacesContext.getCurrentInstance().getViewRoot())){
			result.put(p.getName(), p.getStringValue(context));
        }
		
		return result;
		
	}
	
	private List<ErrorDto> iterateEvents(Iterator<ExceptionQueuedEvent> queue) {
		
		if (!queue.hasNext()) {
			return null;
		}
		
		List<ErrorDto> dtos = new LinkedList<>();
		
		do {
			
			ExceptionQueuedEvent event = queue.next();
			ExceptionQueuedEventContext source = (ExceptionQueuedEventContext) event.getSource();
			Throwable exception = source.getException();
			
			ErrorDto dto = new ErrorDto();
			dto.setMessage(exception.getMessage());
			dto.setStackTraceElements(exception.getStackTrace());
			dtos.add(dto);
			
			queue.remove();
			
		} while (queue.hasNext());
		
		return dtos;
		
	}

}
	
	
	
	
	
	
	
	
	
	
	
