package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

/**
 * The backing bean for the registration page. On this page an anonymous user
 * can register themself. Additionally, it can be used by administrators to
 * register new users.
 */
@Named
@ViewScoped
public class Registration {

	@Inject private FacesContext context;
	@Inject private UserSession session;
	private UserDto user;

	private String password;
	private String repeatedPassword;

	@PostConstruct
	public void init() {
		user = new UserDto();
		user.setEmailVerified(false);
		user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public List<UserRole> getRoles(){
		return List.of(UserRole.values());
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	/**
	 * As an anonymous user register to the system.
	 */
	public String register() {
		try {
			createPasswordHash();
			UserDao.createUser(user);
			return switchUser();
		} catch (EntityInstanceNotUniqueException e){
			context.addMessage(null, new FacesMessage("The entered email is already taken"));
			return null;
		}
	}

	private String switchUser(){
		UserDto currentUser = session.getUser();
		if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().equals(UserRole.ADMIN)){
			context.addMessage(null, new FacesMessage("User created successfully"));
			return null;
		} else {
			session.setUser(user);
			return "/view/profile?faces-redirect=true";
		}
	}

	private void createPasswordHash(){
		// TODO: Implement
	}
}
