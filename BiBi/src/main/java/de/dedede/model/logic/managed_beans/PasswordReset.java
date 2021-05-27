package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.TokenDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet with the same name. After the user clicked on
 * the 'Reset' password button on the {@link Login}, he is directed to this page
 * where he can order a verification link to set a new password.
 */
@Named
@RequestScoped
public class PasswordReset {

	private String password;

	private TokenDto token;

	private String confirmedPassword;

	@PostConstruct
	public void init() {

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

	/**
	 * Reset the password when it is necessary to reset it.
	 */
	public void resetPassword() {

	}
}
