package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.*;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.Application;
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
import java.util.ResourceBundle;

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
		user.setRole(UserRole.REGISTERED);
		user.setZipCode(99999);
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
		setPasswordHash();
		user.setToken(TokenGenerator.generateToken());
		UserDao.createUser(user);
		sendVerificationEmail();
		return switchUser();
	}
	
	private void sendVerificationEmail() {
		try {
			EmailUtility.sendEmail(user.getEmailAddress(), "Email-Link", 
					EmailUtility.getLink("/view/ffa/email-confirmation.xhtml", user.getToken().getContent()));
		} catch (MessagingException | UnsupportedEncodingException e) {
			Logger.severe("Couldn't send a verification email to user: " + user.getEmailAddress());
		}
	}

	private String switchUser(){
		if (isAdmin()){
			MessagingUtility.writePositiveMessageWithKey(context, "registration.success.admin");
		} else {
			MessagingUtility.writePositiveMessageWithKey(context, "registration.success.own");
			session.setUser(user);
		}
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "/view/account/profile.xhtml?faces-redirect=true&id=" + session.getUser().getId();
	}

	private void setPasswordHash(){
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword(password, salt);
		user.setPasswordHash(hash);
		user.setPasswordSalt(salt);
	}
	
	public boolean isAdmin() {
		UserDto currentUser = session.getUser();
		return currentUser != null && currentUser.getRole() != null && currentUser.getRole().equals(UserRole.ADMIN);
	}
	
}
