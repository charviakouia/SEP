package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
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
	private FacesContext context;

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
	 * 
	 * @throws IOException 
	 */
	public void onload() throws IOException {
		try {
			if (userSession.getUser() != null) {
				if (userSession.getUser().getUserRole().equals(UserRole.ADMIN)
						|| userSession.getUser().getId() == user.getId()) {
					user = UserDao.readUserForProfile(user);
				} else {
					context.addMessage(null, new FacesMessage("You do not have access to the user profile."));
				}
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/login.xhtml");
				context.addMessage(null, new FacesMessage("Please sign in."));
			}
		} catch (UserDoesNotExistException e) {
			context.addMessage(null, new FacesMessage("There is no user with this ID."));
		}
	}
}
