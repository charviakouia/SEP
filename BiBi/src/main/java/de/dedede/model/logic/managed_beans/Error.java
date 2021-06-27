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
 * Backing bean for the error page. If it’s a client-side error, the
 * user gets redirected to either the login page or their profile depending on
 * if they are logged in or not. The redirection happens after some fixed amount
 * of time.
 *
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

    public ErrorDto[] getErrorDtos() {
        return errorDtos;
    }

    public void setErrorDtos(ErrorDto[] errorDtos) {
        this.errorDtos = errorDtos;
    }
    
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
    
    public String getUrlWithoutParameters() {
    	return previousUrl;
    }
    
    public String getHomeUrl() {
    	return "/view/opac/medium-search.xhtml";
    }
    
}
