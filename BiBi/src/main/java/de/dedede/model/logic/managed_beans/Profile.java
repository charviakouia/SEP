package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyIsNotAvailableException;
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
import java.util.List;
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
public class Profile extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private List<MediumCopyUserDto> copies;

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
		if (!MediumDao.readLentCopiesByUser(getPaginatedList(), user).isEmpty()
				|| !MediumDao.readMarkedCopiesByUser(getPaginatedList(), user).isEmpty()) {
			MessagingUtility.writeNegativeMessageWithKey(context, false, "delUser.hasLentCopies");
			return "";
		}
		UserDao.deleteUser(user);
		if (userSession.getUser().getUserRole() == UserRole.ADMIN) {
			MessagingUtility.writePositiveMessageWithKey(context, true, "delUser.success");
			return "/view/admin/administration?faces-redirect=true";
		} else {
			userSession.setUser(null);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.invalidateSession();
			MessagingUtility.writePositiveMessageWithKey(context, true, "delUser.success");
			return "/view/ffa/login?faces-redirect=true";
		}
	}

	/**
	 * Save the changes made to the profile.
	 */
	public void save() throws IOException {
		try {
			user.setToken(null);
			user.setTokenCreation(null);
			UserDao.updateUser(user);
			MessagingUtility.writePositiveMessageWithKey(context, false, "saveProfile.success");
		} catch (EntityInstanceDoesNotExistException e) {
			userSession.setUser(null);
			FacesContext.getCurrentInstance().getExternalContext();
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.invalidateSession();
			MessagingUtility.writeNegativeMessageWithKey(context, true, "saveProfile.notUser");
		}
	}

	/**
	 * Save the changes made to the password.
	 */
	public void updatePassword() {
		if (!password.equals(confirmedPassword)) {
			MessagingUtility.writeNegativeMessageWithKey(context, false, "saveProfile.noMatch");
			return;
		}
		if (!password.isEmpty()) {
			String salt = PasswordHashingModule.generateSalt();
			String hash = PasswordHashingModule.hashPassword(password, salt);
			user.setPasswordHash(hash);
			user.setPasswordSalt(salt);
			try {
				UserDao.updateUser(user);
				MessagingUtility.writePositiveMessageWithKey(context, false, "saveProfile.success");
			} catch (EntityInstanceDoesNotExistException e) {
				userSession.setUser(null);
				FacesContext.getCurrentInstance().getExternalContext();
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.invalidateSession();
				MessagingUtility.writeNegativeMessageWithKey(context, true, "saveProfile.notUser");
			}
		}
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
					MessagingUtility.writeNegativeMessageWithKey(context, true, "profile.notAccess");
					FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/opac/medium-search.xhtml");
				}
			} else {
				MessagingUtility.writeNegativeMessageWithKey(context, true, "profile.notLogin");
				FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/ffa/login.xhtml");
			}
		} catch (UserDoesNotExistException e) {
			MessagingUtility.writeNegativeMessageWithKey(context, true, "profile.invalidID");
			FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/opac/medium-search.xhtml");
		}
	}
	
	// Authored by Ivan to test global exception handler functionality
	public String throwsError() {
		throw new IllegalStateException("Doing a bit of testing here...");
	}

	@Override
	public List<?> getItems() {
		return copies;
	}

	@Override
	public void refresh() {

	}
}
