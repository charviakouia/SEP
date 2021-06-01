package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
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

	@Inject
	private FacesContext facesContext;

	@Inject
	private UserSession userSession;

	private String email;

	private String password;

	@PostConstruct
	public void init() {


	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Log into the system.
	 * 
	 * @throws MaxConnectionsException If there are no more available database
	 *                                 connections.
	 */
	public String logIn() throws MaxConnectionsException {
		UserDto user = new UserDto();
		user.setEmailAddress(email);
		UserDto dbUser = null;

		try {
			dbUser = UserDao.readUserByEmail(user);

		} catch (EntityInstanceDoesNotExistException e){
			//TODO auf Fehlerseite weiterleiten
		}


		return null;
	}

	/**
	 * Send an email to the user with a reset link inside.
	 */
	public void resetPassword() {

	}
}
