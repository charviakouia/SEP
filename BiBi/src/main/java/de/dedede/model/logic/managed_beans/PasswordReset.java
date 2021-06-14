package de.dedede.model.logic.managed_beans;

import java.io.Serializable;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet with the same name. After the user clicked on
 * the 'Reset' password button on the {@link Login}, he is directed to this page
 * where he can order a verification link to set a new password.
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

	public void findUser() {
		try {
			userDto.setToken(token);
			UserDao.readUserByToken(userDto);
			if (!userEligible()) {
				throw new BusinessException("The given user is ineligible to change their password");
			}
		} catch (UserDoesNotExistException e) {
			throw new BusinessException("The given token doesn't match any user", e);
		}
	}
	
	private boolean userEligible() {
		boolean tokenValid = !userDto.getToken().getContent().isBlank();
		boolean tokenForPassword = UserVerificationStatus.VERIFIED == userDto.getUserVerificationStatus();
		return tokenValid && tokenForPassword;
	}

	/**
	 * Reset the password when it is necessary to reset it.
	 * @throws EntityInstanceDoesNotExistException 
	 */
	public String resetPassword() throws EntityInstanceDoesNotExistException {
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword(password, salt);
		userDto.setPasswordHash(hash);
		userDto.setPasswordSalt(salt);
		userDto.setToken(null);
		userDto.setTokenCreation(null);
		UserDao.updateUser(userDto);
		context.addMessage(null, new FacesMessage("Password changed successfully"));
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "/view/public/login.xhtml?faces-redirect=true";
	}
}
