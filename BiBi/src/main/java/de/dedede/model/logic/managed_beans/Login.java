package de.dedede.model.logic.managed_beans;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.util.EmailUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.TokenGenerator;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Backing bean for the login page. This page is the one users first face when
 * they are not already logged in. It allows them to log into the system to gain
 * the privilege to borrow copies. If a logged-in user accesses this page by
 * manually entering its URL, a message is shown instead of the login form					
 * 
 * @author Jonas Picker
 */
@Named
@RequestScoped
public class Login {
	
	/**
	 * Holds the form input.
	 */
	private UserDto userData = new UserDto();
	
	/**
	 * The @SessionScoped Bean that hold userdata.
	 */
	@Inject
	private UserSession userSession;
	
	/**
	 * No tasks here.
	 */
	@PostConstruct
	public void init() {}
	
	/**
	 * Checks for existing user session and decides if facelet renders login 
	 * form.
	 * 
	 * @return true if the login form should be rendered
	 */
	public boolean renderLogin() {
		if (userSession.getUser() != null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Log into the system and redirect to profile page if successful.
	 *
	 * @throws MaxConnectionsException If there are no more available database
	 *                                 connections.
	 * @throws LostConnectionException if an error during db communication 
	 * 									occured
	 */
	public void logIn() throws MaxConnectionsException,
			LostConnectionException {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context,
				"#{msg}", ResourceBundle.class);
		try {
		String passwordInput = userData.getPasswordHash();
		UserDto completeUserData = UserDao.readUserByEmail(userData);
		String salt = completeUserData.getPasswordSalt();
		String passwordHash = completeUserData.getPasswordHash();
		String inputHash = PasswordHashingModule.hashPassword(passwordInput,
				salt);
		if (inputHash.equals(passwordHash)) {
				ExternalContext externalContext = context.getExternalContext();
				HttpServletRequest request = 
						(HttpServletRequest) externalContext.getRequest();
				request.changeSessionId();
				userSession.setUser(completeUserData);
				NavigationHandler navigationHandler = 
						application.getNavigationHandler();
				navigationHandler.handleNavigation(context, null,
						"medium-search.xhtml");
				return;
			} else {
				String shortMessage = messages.getString("login.wrong"
						+ "_password_short");
				String longMessage = messages.getString("login.wrong"
						+ "_password_long");
				context.addMessage("login_form:login_password_field", 
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								shortMessage, longMessage));
			}
		} catch (UserDoesNotExistException e) {
			String shortMessage = messages.getString("login.unknown"
					+ "_user_short");
			String longMessage = messages.getString("login.unknown_user_long");
			context.addMessage("login_form:login_email_field", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							shortMessage, longMessage));
		}		
		
	}

	/**
	 * Send an email to the user with a reset link inside.						
	 */
	public void resetPassword() { 																				
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		try {
			UserDto completeUserData = UserDao.readUserByEmail(userData);
			String subject = messages.getString("login.reset_email_subject");
			String content = messages.getString("login.reset_email_content");
			TokenDto newTokenContainer = TokenGenerator.generateToken();  						
			TokenDto userToken = UserDao.setOrRetrieveUserToken(
					completeUserData, newTokenContainer);
			String token = userToken.getContent();		
			String userLink = "www.testlink.de/?token=" + token;				//@see format when PasswordReset Package is finished by Ivan
			String firstname = completeUserData.getFirstName();
			String lastname = completeUserData.getLastName();
			String emailBody = insertParams(firstname, lastname, userLink, 
					content);
			String emailAddress = completeUserData.getEmailAddress();
			try {
				EmailUtility.sendEmail(emailAddress, subject, emailBody);
			} catch (MessagingException e) {
				Logger.severe(e.getMessage());
			}
			Logger.development("A password reset was requested from the "
					+ "login page, an email was sent to: " 
					+ emailAddress);
			Logger.development("Inhalt der Email: " + emailBody);													
			String shortMessage = messages.getString("login.email_was"
					+ "_sent_short");
			String longMessage = messages.getString("login.email_was"
					+ "_sent_long");
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_INFO, 
							shortMessage, longMessage));
		} catch (UserDoesNotExistException e) {
			String shortMessage = messages.getString("login.unknown_user"
					+ "_short");
			String longMessage = messages.getString("login.unknown_user"
					+ "_long");
			context.addMessage("login_email_message", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							shortMessage, longMessage));
		}
		
	}
	
	private String insertParams(String param1, String param2, 
			String param3, String content) {
		MessageFormat messageFormat = new MessageFormat(content);
		Object[] args = {param1, param2, param3};
		String contentWithParam = messageFormat.format(args);
		
		return contentWithParam;
	}
	
	//getters and setters
	public UserDto getUserData() {
		return userData;
	}

	public void setUserData(UserDto user) {
		this.userData = user;
	}

}
