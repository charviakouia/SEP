package dedede.model.logic.managedbeans;

import dedede.model.logic.util.TokenGenerator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.internet.HeaderTokenizer;

import java.util.logging.Logger;

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

	private String confirmPassword;
	
	public void resetPassword() {
		
	}
}
