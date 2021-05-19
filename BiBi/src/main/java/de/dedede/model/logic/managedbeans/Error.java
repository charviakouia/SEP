package de.dedede.model.logic.managedbeans;

import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Backing bean for the error page. The sink for all kinds of fatal errors that
 * happened in the system. They are either caused client-side (HTTP status codes
 * 4XX) or server-side (HTTP status codes 5XX). If it’s a client-side error, the
 * user gets redirected to either the login page or their profile depending on
 * if they are logged in or not. The redirection happens after some fixed amount
 * of time.
 *
 */
@Named
@ApplicationScoped
public class Error implements Serializable {

	private EntityInstanceDoesNotExistException entityInstanceDoesNotExistException;

}
