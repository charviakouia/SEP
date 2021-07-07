package de.dedede.model.logic.managed_beans;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.util.EmailUtility;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.TokenGenerator;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
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
	 * The @SessionScoped Bean that holds the users data.
	 */
	@Inject
	private UserSession userSession;
		
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
	 * Log into the system and redirect to profile page if successful while
	 * switching out the HttpSession Id and initializing the users data in the
	 * session scope.
	 *
	 * @throws MaxConnectionsException If there are no more available database
	 *                                 connections.
	 * @throws LostConnectionException if an error during db communication 
	 * 								   occured
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
						"/view/opac/medium-search.xhtml?faces-redirect=true");
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
	 * Send an email to the user with a reset link inside, shows confirmation
	 * or failure messages depending on the outcome. 						
	 */
	public void resetPassword() {									
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		String subject = messages.getString("login.reset_email_subject");
		String content = messages.getString("login.reset_email_content");
		String shortMessageFail = messages.getString("login.email_failure"
				+ "_short");
		String longMessageFail = messages.getString("login.email_failure"
				+ "_long");
		TokenDto newTokenContainer = TokenGenerator.generateToken();
		try {
			UserDto completeUserData = UserDao.readUserByEmail(userData);
			TokenDto userToken = UserDao.setOrRetrieveUserToken(
					completeUserData, newTokenContainer);			
			String link = EmailUtility.getLink(
				   "/view/ffa/password-reset.xhtml", userToken.getContent());
			ExternalContext extCtx = context.getExternalContext();
			String SystemMode = 
					extCtx.getInitParameter("jakarta.faces.PROJECT_STAGE");
			if (SystemMode.equals("Development")){ //workaround for Ivans BBtest
				MessagingUtility.writeNeutralMessage(context, true, link);
			}
			String firstname = completeUserData.getFirstName();
			String lastname = completeUserData.getLastName();
			String emailBody = insertParams(firstname, lastname, link, content);
			String emailAddress = completeUserData.getEmailAddress();
			try {
				EmailUtility.sendEmail(emailAddress, subject, emailBody);
			} catch (MessagingException e) {
				Logger.severe("An error occured while trying to send reset"
						+ " email: " + e.getMessage());
				context.addMessage("login_email_message", 
						new FacesMessage(FacesMessage.SEVERITY_ERROR, 
								shortMessageFail, longMessageFail));
			}
			Logger.development("A password reset was requested from the "
					+ "login page, an email was sent to: " 
					+ emailAddress);												
			String shortMessage = messages.getString("login.email_was"
					+ "_sent_short");
			String longMessage = messages.getString("login.email_was"
					+ "_sent_long");
			context.addMessage("login_form:login_email_field", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							shortMessage, longMessage));
		} catch (UserDoesNotExistException e) {
			String shortMessage = messages.getString("login.unknown_user"
					+ "_short");
			String longMessage = messages.getString("login.unknown_user"
					+ "_long");
			context.addMessage("login_email_message", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, shortMessage,
							longMessage));
		} catch (UnsupportedEncodingException e1) {
			Logger.development("An unexpected error occured during "
					+ "token encoding on password reset.");
			context.addMessage("login.email_message", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, shortMessageFail, 
					longMessageFail));
		}		
	}
	
	private String insertParams(String param1, String param2, String param3, 
			String content) {
		MessageFormat messageFormat = new MessageFormat(content);
		Object[] args = {param1, param2, param3};
		String contentWithParam = messageFormat.format(args);
		return contentWithParam;
	}
	
	/**
	 * Grants the facelet access to the UserDto.
	 * 
	 * @return the inputContainer
	 */
	public UserDto getUserData() {
		return userData;
	}
	
	/**
	 * Grants the facelet access to the UserDto.
	 * 
	 * @param user the new user input
	 */
	public void setUserData(UserDto user) {
		this.userData = user;
	}
	
}
