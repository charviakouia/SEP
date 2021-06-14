package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.EmailUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.TokenGenerator;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The backing bean for the registration page. On this page an anonymous user
 * can register themself. Additionally, it can be used by administrators to
 * register new users.
 */
@Named
@ViewScoped
public class Registration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject private FacesContext context;
	@Inject private UserSession session;
	private UserDto user;

	private String password;
	private String repeatedPassword;

	@PostConstruct
	public void init() {
		user = new UserDto();
		user.setUserVerificationStatus(UserVerificationStatus.UNVERIFIED);
		user.setUserLendStatus(UserLendStatus.ENABLED);
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public List<UserRole> getRoles(){
		if (isAdmin()) {
			return List.of(UserRole.values());
		} else {
			return List.of(UserRole.REGISTERED);
		}
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
			setPasswordHash();
			user.setToken(TokenGenerator.generateToken());
			UserDao.createUser(user);
			sendVerificationEmail();
			return switchUser();
		} catch (EntityInstanceNotUniqueException e){
			context.addMessage(null, new FacesMessage("The entered email is already taken"));
			return null;
		}
	}
	
	private void sendVerificationEmail() {
		try {
			String link = context.getApplication().getViewHandler().getResourceURL(
					context, "/view/public/email-confirmation.xhtml?token=" 
							+ URLEncoder.encode(user.getToken().getContent(), "UTF-8"));
			EmailUtility.sendEmail(user.getEmailAddress(), "Email-Link", "http://localhost:8080" + link);
		} catch (MessagingException | UnsupportedEncodingException e) {
			Logger.severe("Couldn't send a verification email to user: " + user.getEmailAddress());
		}
	}

	private String switchUser(){
		if (isAdmin()){
			context.addMessage(null, new FacesMessage("User created successfully"));
			context.getExternalContext().getFlash().setKeepMessages(true);
			return "/view/account/profile.xhtml?faces-redirect=true&id=" + session.getUser().getId();
		} else {
			context.addMessage(null, new FacesMessage("Registration successful"));
			context.getExternalContext().getFlash().setKeepMessages(true);
			session.setUser(user);
			return "/view/account/profile.xhtml?faces-redirect=true&id=" + session.getUser().getId();
		}
	}

	private void setPasswordHash(){
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword(password, salt);
		user.setPasswordHash(hash);
		user.setPasswordSalt(salt);
	}
	
	private boolean isAdmin() {
		UserDto currentUser = session.getUser();
		return currentUser != null && currentUser.getRole() != null && currentUser.getRole().equals(UserRole.ADMIN);
	}
	
}
