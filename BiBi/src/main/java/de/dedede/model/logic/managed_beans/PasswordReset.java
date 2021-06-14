package de.dedede.model.logic.managed_beans;

import java.io.Serializable;

import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
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
	
	private TokenDto token;
	private String password;
	private String confirmedPassword;
	private UserDto userDto;

	@PostConstruct
	public void init() {}

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

	public void findUser() throws UserDoesNotExistException {
		userDto = UserDao.readUserByToken(userDto);
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
		UserDao.updateUser(userDto);
		return null;
	}
}
