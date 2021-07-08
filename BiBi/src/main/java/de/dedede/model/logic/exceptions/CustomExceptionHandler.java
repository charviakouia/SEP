package de.dedede.model.logic.exceptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dedede.model.data.dtos.ErrorDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.exceptions.AnnotatedException;
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
 * checked exceptions that are thrown during the Faces lifecycle. Unchecked
 * exceptions are handled by redirecting to the error page. For checked
 * exceptions, a FacesMessage is generated and displayed on the given page.
 *
 * @author Ivan Charviakou
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

	/**
	 * Adds any generated {@link ErrorDto}s, the URL of the previous page, and the GET-parameter map to the
	 * request map. Then, the user is navigated to the dynamic error page, where these parameters are used.
	 * Before this, the wrapped handler is invoked.
	 *
	 * @throws FacesException Is thrown when
	 */
	@Override
	public void handle() throws FacesException {
		
		Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
		List<ErrorDto> dtos = iterateEvents(iterator);
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot viewRoot = context.getViewRoot();
		
		if (dtos != null) {
            
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            requestMap.put("errorDtos", dtos.toArray(new ErrorDto[0]));
            requestMap.put("previousUrl", viewRoot.getViewId());
            requestMap.put("parameterMap", getParameterMap());
            
            NavigationHandler nav = context.getApplication().getNavigationHandler();
    		nav.handleNavigation(context, null, "/view/error/error");
    		
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
			dto.setExceptionInformation(getExceptionInformation(exception));
			
			dtos.add(dto);
			
			queue.remove();
			
		} while (queue.hasNext());
		
		return dtos;
		
	}
	
	private String[][] getExceptionInformation(Throwable exception){
		
		List<String[]> result = new LinkedList<>();
		
		for (Throwable e = exception; e != null; e = e.getCause()) {
			
			String[] arr = new String[3];
			arr[0] = e.getClass().getName();
			if (e instanceof AnnotatedException) {
				arr[1] = ((AnnotatedException) e).getPersonalizedMessage();
			} else {
				arr[1] = null;
			}
			arr[2] = e.getMessage();
			
			result.add(arr);
			
		}
		
		return result.toArray(new String[0][0]);
		
	}

}
	
	
	
	
	
	
	
	
	
	
	
