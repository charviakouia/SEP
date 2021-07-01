package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.*;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
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
 *
 * @author Ivan Charviakou
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
		user.setZipCode(12345);
	}

	/**
	 * Saves the inputted user data into the datastore, thereby creating a newly registered user. In addition,
	 * a token is generated and sent to the given email-address, if the address is valid. In the case of a
	 * non-administrative account, the user session is also changed to reflect the new registration.
	 *
	 * @return A navigation reference to the profile-page for the newly confirmed user
	 */
	public String register() {
		setPasswordHash();
		setToken();
		createUser();
		sendVerificationEmail();
		switchUserAndSetMessages();
		return getNavigation();
	}

	private void setToken(){
		TokenDto token = TokenGenerator.generateToken();
		user.setToken(token);
		if (context.getExternalContext().getInitParameter("jakarta.faces.PROJECT_STAGE").equals("Development")){
			try {
				String link = EmailUtility.getLink("/view/ffa/email-confirmation.xhtml", user.getToken().getContent());
				MessagingUtility.writeNeutralMessage(context, true, link);
			} catch (UnsupportedEncodingException ignored) {}
		}
	}

	private void setPasswordHash() {
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword(password, salt);
		user.setPasswordHash(hash);
		user.setPasswordSalt(salt);
	}

	private void createUser() throws BusinessException {
		try {
			UserDao.createUser(user);
		} catch (LostConnectionException e){
			String msg = "Couldn't create user due to a connection error";
			Logger.severe(msg);
			throw new BusinessException(msg, e);
		}
	}
	
	private void sendVerificationEmail() {
		try {
			String link = EmailUtility.getLink("/view/ffa/email-confirmation.xhtml", user.getToken().getContent());
			String content = MessagingUtility.getMessage(context, "registration.email.content", link);
			String subject = MessagingUtility.getMessage(context, "registration.email.subject");
			EmailUtility.sendEmail(user.getEmailAddress(), subject, content);
		} catch (MessagingException | UnsupportedEncodingException e) {
			String msg = MessagingUtility.getMessage(context, "registration.error.emailNotSent", user.getEmailAddress());
			MessagingUtility.writeNegativeMessage(context, true, msg);
			Logger.severe(msg);
		}
	}

	private void switchUserAndSetMessages(){
		if (session.isAdmin()){
			MessagingUtility.writePositiveMessageWithKey(context, true, "registration.success.admin");
		} else {
			MessagingUtility.writePositiveMessageWithKey(context, true, "registration.success.own");
			session.setUser(user);
		}
	}

	private String getNavigation(){
		if (session.isAdmin()){
			return "/view/account/profile.xhtml?faces-redirect=true&id=" + user.getId();
		} else {
			return "/view/account/profile.xhtml?faces-redirect=true&id=" + session.getUser().getId();
		}
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
	
}
