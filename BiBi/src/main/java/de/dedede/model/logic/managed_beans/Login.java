package de.dedede.model.logic.managed_beans;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.EmailUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Backing bean for the login page. This page is the one users first face when
 * they are not already logged in. It allows them to log into the system to gain
 * the privilege to borrow copies. If a logged-in user accesses this page by
 * manually entering its URL, a message is shown instead of the login form and
 * they get automatically redirected to their profile page.						//<--Das macht der PhaseListener?!
 * 
 * @author Jonas Picker
 */
@Named
@RequestScoped
public class Login {

	private UserDto userData = new UserDto();
	
	/**
	 * The SessionScoped Bean that capsules user Data.
	 */
	@Inject
	UserSession userSession;
	
	@PostConstruct
	public void init() {}

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
				userSession = new UserSession();
				userSession.setUser(completeUserData);
				try {
					externalContext.redirect(
							"/BiBi/view/account/profile.xhtml?id=" 
							+ completeUserData.getId());
					return;
				} catch (IOException io) {
					Logger.severe("IOException occured during redirection"
							+ " attempt from correct login.");
					throw new BusinessException("An unexpected error "
							+ "occured during Login", io);						
				}
			} else {
				String shortMessage = messages.getString("login.wrong"
						+ "_password_short");
				String longMessage = messages.getString("login.wrong"
						+ "_password_long");
				context.addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								shortMessage, longMessage));
			}
		} catch (UserDoesNotExistException e) {
			String shortMessage = messages.getString("login.unknown"
					+ "_user_short");
			String longMessage = messages.getString("login.unknown_user_long");
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							shortMessage, longMessage));
		}		
		
	}

	/**
	 * Send an email to the user with a reset link inside.						
	 */
	public void resetPassword() { 												//Fehler behandlung wenn Email+TokenGenerator fertig										
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		try {
			UserDto completeUserData = UserDao.readUserByEmail(userData);
			String subject = messages.getString("login.reset_email_subject");
			String content = messages.getString("login.reset_email_content");
			TokenDto newTokenContainer = new TokenDto();       					//TokenGenerator.generateToken(); wenn fertig
			newTokenContainer.setContent("12345678901234567890");				//dummy
			TokenDto userToken = UserDao.setOrRetrieveUserToken(
					completeUserData, newTokenContainer);
			String token = userToken.getContent();								//UNFINISHED
						
			String userLink = "www.testlink.de/?token=" + token;				//@see format when PasswordReset Package is finished by Ivan
			String firstname = completeUserData.getFirstName();
			String lastname = completeUserData.getLastName();
			String emailBody = insertParams(firstname, lastname, userLink, 
					content);
			String emailAddress = completeUserData.getEmailAddress();
			EmailUtility.sendEmail(emailAddress, subject, emailBody);
			Logger.development("Aus der Loginseite wurde eine Passwortzurück"
					+ "setzung angefordert, eine Email wurde versendet an: " 
					+ emailAddress);
			Logger.development("Inhalt der Email: " + emailBody);													
			String shortMessage = messages.getString("login.email_was"
					+ "_sent_short");
			String longMessage = messages.getString("login.email_was"
					+ "_sent_long");
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							shortMessage, longMessage));
		} catch (UserDoesNotExistException e) {
			String shortMessage = messages.getString("login.unknown_user"
					+ "_short");
			String longMessage = messages.getString("login.unknown_user"
					+ "_long");
			context.addMessage(null, 
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
