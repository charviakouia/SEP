package dedede.model.logic.managedbeans;

import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet with the same name. After the user clicked on the
 * 'Reset' password button on the {@link Login}, he is directed to this page where he can order
 * a verification link to set a new password.
 */
@Named
@RequestScoped
public class PasswordReset {
	private final Logger logger = null;

	private String password;

	private String token;

	private String confirmedPassword;
	
	/**
	 * Reset the password.
	 */
	public void resetPassword() {
		
	}
}
