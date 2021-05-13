package dedede.model.logic.managedbeans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class PasswordReset {
	
	private String password;
	private String confirmPassword;
	public void resetPassword() {
		
	}
}
