package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the email confirmation page. Accessing this page verifies the email address of a specific
 * user, which is necessary to enable certain app functionalities. For this, the facelet takes a token as a query
 * parameter, which is sent to the user per email after registration.
 *
 * @author Ivan Charviakou
 */
@Named
@RequestScoped
public class EmailConfirmation implements Serializable {

	@Serial private static final long serialVersionUID = 1L;
	
	@Inject private FacesContext context;

	private TokenDto token;
	private UserDto user;

	@PostConstruct
	public void init() {
		token = new TokenDto();
		user = new UserDto();
		user.setToken(token);
	}

	/**
	 * Confirms the email-address of a newly registered user. This is only successful if the given token is
	 * associated with the user and the token itself has been generated specifically for the purpose of verifying
	 * the user's email.
	 *
	 * @return A navigation reference to the profile-page for the newly confirmed user
	 * @throws BusinessException Is thrown if the passed token is not associated with any user, if the token
	 * 		isn't a email-verification token, or if the user couldn't be found as the result of a race-condition.
	 */
	public String attemptToConfirm() throws BusinessException {
		findUser();
		user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
		user.setToken(null);
		user.setTokenCreation(null);
		try {
			UserDao.updateUser(user);
			MessagingUtility.writePositiveMessageWithKey(context, true, "emailConfirmation.success");
			return "/view/account/profile.xhtml?faces-redirect=true&id=" + user.getId();
		} catch (EntityInstanceDoesNotExistException e){
			String msg = "User could not be updated - user stopped existing before method's conclusion";
			Logger.severe(msg);
			throw new BusinessException(msg, e);
		}
	}

	private void findUser() throws BusinessException {
		try {
			UserDao.readUserByToken(user);
			if (!userEligible()) {
				String msg = MessagingUtility.getMessage(context, "emailVerification.userIneligible");
				Logger.severe(msg);
				throw new BusinessException(msg);
			}
		} catch (UserDoesNotExistException e) {
			String msg = MessagingUtility.getMessage(context, "emailVerification.noMatch");
			Logger.severe(msg);
			throw new BusinessException(msg, e);
		}
	}

	private boolean userEligible() {
		boolean tokenValid = user.getToken() != null && !user.getToken().getContent().isBlank();
		boolean tokenForEmail = UserVerificationStatus.UNVERIFIED == user.getUserVerificationStatus();
		return tokenValid && tokenForEmail;
	}

	public TokenDto getToken() {
		return token;
	}

	public void setToken(TokenDto token) {
		this.token = token;
	}

}
