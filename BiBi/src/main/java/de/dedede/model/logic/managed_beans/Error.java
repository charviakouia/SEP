package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import de.dedede.model.data.dtos.ErrorDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIViewParameter;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the error page. This facelet displays information relating to the context of unhandled,
 * thrown exceptions and offers the user suitable navigation options.
 *
 * @author Ivan Charviakou
 */
@Named
@RequestScoped
public class Error implements Serializable {

	@Serial private static final long serialVersionUID = 1L;
	@Inject FacesContext context;
	private ErrorDto[] errorDtos;
	private String previousUrl;
	private Map<String, String> parameterMap;
	
	@PostConstruct
    public void init() {
		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		errorDtos = (ErrorDto[]) requestMap.get("errorDtos");
		parameterMap = (Map<String, String>) requestMap.get("parameterMap");
		previousUrl = (String) requestMap.get("previousUrl");
	}

	/**
	 * Constructs a return link to the previous page, taking into account any previous GET-parameters.
	 *
	 * @return The URL to the previous page with the previous GET-parameters.
	 */
	public String getUrlWithParameters() {
    	if (parameterMap.isEmpty()) {
    		return previousUrl;
    	} else {
    		StringJoiner sj = new StringJoiner("&", "?", "");
        	for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
        		sj.add(entry.getKey() + "=" + entry.getValue());
        	}
        	return previousUrl + sj.toString();
    	}
    }

	/**
	 * Returns a link to the previous page, without taking any previous GET-parameters into account.
	 *
	 * @return The URL to the previous page without the previous GET-parameters.
	 */
    public String getUrlWithoutParameters() {
    	return previousUrl;
    }

	/**
	 * Returns a link the the app's home page.
	 *
	 * @return A link to the app's home page.
	 */
    public String getHomeUrl() {
    	return "/view/opac/medium-search.xhtml";
    }

	/**
	 * Returns information regarding the thrown errors that lead to this facelet, in the form of
	 * error DTOs.
	 *
	 * @return An array of error DTOs that encapsulate the original exception-error information.
	 * @see ErrorDto
	 */
	public ErrorDto[] getErrorDtos() {
		return errorDtos;
	}

	public void setErrorDtos(ErrorDto[] errorDtos) {
		this.errorDtos = errorDtos;
	}
    
}
