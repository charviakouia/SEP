package de.dedede.model.logic.managed_beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.EmailUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.TokenGenerator;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

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
	 * Redirects the user to the profile page after 5 seconds, if he has a 
	 * session already
	 * 
	 * @return true if the session already exists
	 
	public boolean redirectWhenLoggedIn() {
		if (userSession.getUser() == null) {
			return false;
		} else {
			ScheduledExecutorService scheduler = 
					Executors.newSingleThreadScheduledExecutor();
			scheduler.schedule(new CustomTask(userSession.getUser().getId()),
					5, TimeUnit.SECONDS);
			return true;
		}
	}*/
	
	/**
	 * Capsules a callable thread by the ThreadSchedular
	 
	private class CustomTask extends TimerTask  {
		
		private int id;
		
		@Inject
		private ExternalContext externalContext;

		public CustomTask(int id){
			this.id = id;
		}

		public void run() {
			try {
				externalContext.redirect("/BiBi/view/account/profile.xhtml?id="
										+ this.id);
		    } catch (Exception ex) {
		    	Logger.development("Error while redirecting thread ran");
		    }
		}
	}*/
	
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
		String inputHash = PasswordHashingModule.hashPassword(passwordInput, salt);
		if (inputHash.equals(passwordHash)) {
				ExternalContext externalContext = context.getExternalContext();
				HttpServletRequest request = 
						(HttpServletRequest) externalContext.getRequest();
				request.changeSessionId();
				userSession.setUser(completeUserData);
				try {
					externalContext.redirect(
							"/BiBi/view/public/medium-search.xhtml");
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

	// Ivan reformatted the method resetPassword()
	/**
	 * Send an email to the user with a reset link inside.						
	 */
	public void resetPassword() {
		//Fehler behandlung wenn Email+TokenGenerator fertig										
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		
		UserDto completeUserData = null;
		String subject = messages.getString("login.reset_email_subject");
		String content = messages.getString("login.reset_email_content");
		TokenDto newTokenContainer = TokenGenerator.generateToken();
		
		try {
			String token = newTokenContainer.getContent();
			String link = "http://localhost:8080" + 
					context.getApplication().getViewHandler().getResourceURL(
					context, "/view/public/password-reset.xhtml?token=" + URLEncoder.encode(token, "UTF-8"));
			
			completeUserData = UserDao.readUserByEmail(userData);
			completeUserData.setToken(newTokenContainer);	
			// TokenDto userToken = UserDao.setOrRetrieveUserToken(completeUserData, newTokenContainer);
			UserDao.updateUser(completeUserData);
			
			String firstname = completeUserData.getFirstName();
			String lastname = completeUserData.getLastName();
			String emailBody = insertParams(firstname, lastname, link, content);
			String emailAddress = completeUserData.getEmailAddress();
			EmailUtility.sendEmail(emailAddress, subject, emailBody);
			
			Logger.development("Aus der Loginseite wurde eine Passwortzurück"
					+ "setzung angefordert, eine Email wurde versendet an: " + emailAddress);
			Logger.development("Inhalt der Email: " + emailBody);													
			String shortMessage = messages.getString("login.email_was_sent_short");
			String longMessage = messages.getString("login.email_was_sent_long");
			context.addMessage("login_form:login_email_field", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, shortMessage, longMessage));
		
		} catch (MessagingException | UnsupportedEncodingException | EntityInstanceDoesNotExistException e) {
			String emailAddress = (completeUserData == null ? null : completeUserData.getEmailAddress());
			Logger.severe("Couldn't send a verification email to user: " + emailAddress);	
		} catch (UserDoesNotExistException e) {
			String shortMessage = messages.getString("login.unknown_user_short");
			String longMessage = messages.getString("login.unknown_user_long");
			context.addMessage("login_email_message", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, shortMessage, longMessage));
		}
		
	}
	
	private String insertParams(String param1, String param2, String param3, String content) {
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
