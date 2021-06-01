package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.UserDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the profile page. This page is either the profile page of
 * the current user or if it is an administrator possibly also of a user
 * different from the logged-in one. It allows a user to change their personal
 * information the system stores and it allows administrators to manage other
 * accounts.
 */
@Named
@ViewScoped
public class Profile implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private UserDto user;

	private String password;

	private String confirmedPassword;

	@Inject
	private UserSession userSession;

	@PostConstruct
	public void init() {

	}
	
	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}
	
	/**
	 * Close one's own account if this is the profile of the current user or delete
	 * a user as an admin.
	 */
	public void delete() {

	}

	/**
	 * Save the changes made to the profile.
	 */
	public void save() {

	}
}