package de.dedede.model.logic.managed_beans;

import java.io.Serializable;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the password-reset facelet. After the user clicked on the 'Reset' password button
 * on the {@link Login} page, he is sent an email containing a link to this page where a new password can be set.
 * Specifically, this facelet uses a password-reset token, which is passed as a GET-parameter.
 *
 * @author Ivan Charviakou
 */
@Named
@ViewScoped
public class PasswordReset implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject FacesContext context;
	@Inject UserSession session;
	
	private TokenDto token;
	private UserDto userDto;
	private String password;
	private String confirmedPassword;

	@PostConstruct
	public void init() {
		token = new TokenDto();
		userDto = new UserDto();
		userDto.setToken(token);
	}

	/**
	 * Attempts to find a given user by the passed token. This is only successful if the given token is associated
	 * with the user and the token itself has been generated specifically for the purpose of resetting the user's
	 * password.
	 *
	 * @throws BusinessException Is thrown if the passed token is not associated with any user or if the token
	 * 		isn't a password-reset token.
	 */
	public void findUser() throws BusinessException {
		try {
			UserDao.readUserByToken(userDto);
			if (!userEligible()) {
				String msg = MessagingUtility.getMessage(context, "passwordReset.userIneligible");
				Logger.severe(msg);
				throw new BusinessException(msg);
			}
		} catch (UserDoesNotExistException e) {
			String msg = MessagingUtility.getMessage(context, "passwordReset.noMatch");
			Logger.severe(msg);
			throw new BusinessException(msg, e);
		}
	}

	/**
	 * Saves the user's newly entered password into the datastore as a hash-value. For this, a random salt value
	 * is generated.
	 *
	 * @throws BusinessException Is thrown when the given user couldn't be found to be updated.
	 * 		As the user must have existed to access this facelet, this is a result of a race-condition.
	 * @see PasswordHashingModule#hashPassword(String, String)
	 */
	public String resetPassword() throws BusinessException {
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword(password, salt);
		userDto.setPasswordSalt(salt);
		userDto.setPasswordHash(hash);
		userDto.setToken(null);
		userDto.setTokenCreation(null);
		try {
			UserDao.updateUser(userDto);
			session.setUser(userDto);
			MessagingUtility.writePositiveMessageWithKey(context, true, "passwordReset.success");
			return "/view/account/profile.xhtml?faces-redirect=true&id=" + userDto.getId();
		} catch (EntityInstanceDoesNotExistException e){
			throw new BusinessException("Couldn't find a suitable user to update", e);
		}
	}

	private boolean userEligible() {
		boolean tokenValid = userDto.getToken() != null && !userDto.getToken().getContent().isBlank();
		boolean tokenForPassword = UserVerificationStatus.VERIFIED == userDto.getUserVerificationStatus();
		return tokenValid && tokenForPassword;
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

	public TokenDto getToken() {
		return token;
	}

	public void setToken(TokenDto token) {
		this.token = token;
	}

}
