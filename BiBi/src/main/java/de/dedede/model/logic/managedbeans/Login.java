package de.dedede.model.logic.managedbeans;

import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the login page. This page is the one users first face when
 * they are not already logged in. It allows them to log into the system to gain
 * the privilege to borrow copies. If a logged-in user accesses this page by
 * manually entering its URL, a message is shown instead of the login form and
 * they get automatically redirected to their profile page.
 */
@Named
@RequestScoped
public class Login {
	private static final Logger logger = null;

	@Inject
	private FacesContext facesContext;

	@Inject
	private UserSession userSession;

	private String email;

	private String password;

	@PostConstruct
	public void init() {

	}

	/**
	 * Log into the system.
	 * 
	 * @throws MaxConnectionsException If there are no more available database
	 *                                 connections.
	 */
	public String logIn() throws MaxConnectionsException {
		return null;
	}

	/**
	 * Send an email to the user with a reset link inside.
	 */
	public void resetPassword() {

	}

}
