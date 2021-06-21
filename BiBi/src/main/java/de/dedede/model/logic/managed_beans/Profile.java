package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ResourceBundle;

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
	public String delete() throws UserDoesNotExistException, IOException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		UserDao.deleteUser(user);
		if (userSession.getUser().getUserRole() == UserRole.ADMIN) {
			context.addMessage(null, new FacesMessage(messages.getString("delUser.success")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			String result = "administration?" + userSession.getUser().getId()
					+ "&faces-redirect=true";
			return result;
		} else {
			userSession.setUser(null);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.invalidateSession();
			context.addMessage(null, new FacesMessage(messages.getString("delUser.success")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			return "/view/public/login?faces-redirect=true";
		}
	}

	/**
	 * Save the changes made to the profile.
	 */
	public void save() throws IOException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		if (!password.equals(confirmedPassword)) {
			context.addMessage(null, new FacesMessage(messages.getString("saveProfile.noMatch")));
			return;
		}
		try {
			if (!password.isEmpty()) {
				String salt = PasswordHashingModule.generateSalt();
				String hash = PasswordHashingModule.hashPassword(password, salt);
				user.setPasswordHash(hash);
				user.setPasswordSalt(salt);
			}
			user.setToken(null);
			user.setTokenCreation(null);
			UserDao.updateUser(user);
			context.addMessage(null, new FacesMessage(messages.getString("saveProfile.success")));
		} catch (EntityInstanceDoesNotExistException e) {
			userSession.setUser(null);
			FacesContext.getCurrentInstance().getExternalContext();
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.invalidateSession();
			context.addMessage(null, new FacesMessage(messages.getString("saveProfile.notUser")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			externalContext.redirect("/BiBi/view/public/login.xhtml?faces-redirect=true");
		}
	}

	/**
	 * Loads user data from a database for viewAction.
	 * 
	 * @throws IOException 
	 */
	public void onload() throws IOException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			if (userSession.getUser() != null) {
				if (userSession.getUser().getUserRole().equals(UserRole.ADMIN)
						|| userSession.getUser().getId() == user.getId()) {
					user = UserDao.readUserForProfile(user);
				} else {
					context.addMessage(null, new FacesMessage(messages.getString("profile.notAccess")));
					context.getExternalContext().getFlash().setKeepMessages(true);
					FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/medium-search.xhtml");

				}
			} else {
				context.addMessage(null, new FacesMessage(messages.getString("profile.notLogin")));
				context.getExternalContext().getFlash().setKeepMessages(true);
				FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/login.xhtml");
			}
		} catch (UserDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("profile.invalidID")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/medium-search.xhtml");
		}
	}
	
	// Authored by Ivan to test global exception handler functionality
	public String throwsError() {
		throw new IllegalStateException("Doing a bit of testing here...");
	}
	
}
