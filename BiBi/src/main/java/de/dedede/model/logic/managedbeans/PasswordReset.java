package de.dedede.model.logic.managedbeans;

import java.util.logging.Logger;

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

	private String token;

	private String confirmedPassword;

	/**
	 * Reset the password when it is necessary to reset it.
	 */
	public void resetPassword() {

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}
}
