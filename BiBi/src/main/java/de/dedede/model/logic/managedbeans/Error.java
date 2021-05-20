package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.ErrorDto;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

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

    @Serial
    private static  final long serialVersionUID = 1L;

	private ErrorDto errorDto;

    public ErrorDto getErrorDto() {
        return errorDto;
    }

    public void setErrorDto(ErrorDto errorDto) {
        this.errorDto = errorDto;
    }
}
