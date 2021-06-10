package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

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

	@Inject
	private FacesContext facesContext;

	@PostConstruct
	public void init() {
		user = new UserDto();
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

	public UserRole[] getAllUserRoles() {
		return UserRole.values();
	}

	public UserVerificationStatus[] getAllUserStatus() {
		return UserVerificationStatus.values();
	}

	/**
	 * Close one's own account if this is the profile of the current user or delete
	 * a user as an admin.
	 */
	public String delete() throws UserDoesNotExistException {
		UserDao.deleteUser(user);
		if (userSession.getUser().getUserRole() == UserRole.ADMIN) {
			String result = "administration?" + userSession.getUser().getId()
					+ "&faces-redirect=true";
			return result;
		} else {
			return "/view/public/login?faces-redirect=true";
		}
	}

	/**
	 * Save the changes made to the profile.
	 */
	public void save() {

	}

	/**
	 * Loads user data from a database for viewAction.
	 */
	public void onload() throws UserDoesNotExistException {

		/*TODO: Mach besser, sobald logIn funktioniert.*/
//		if (userSession.getUser().getUserRole() == UserRole.ADMIN
//				|| userSession.getUser().getId() == user.getId()) {
//		}

		user = UserDao.readUserForProfile(user);

		//nur für Facelet-debug, weil eine LogIn immer noch nicht funktioniert
		userSession.setUser(user);
	}
}
