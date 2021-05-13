package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.UserDto;
import dedede.model.logic.validators.EmailValidator;
import dedede.model.logic.validators.PasswordValidator;
import dedede.model.persistence.daos.UserDao;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the profile page.
 * This page is either the profile page of the current user or if it is an administrator possibly
 * also of a user different from the logged-in one. It allows a user to change their personal information
 * the system stores and it allows administrators to manage other accounts.
 */
@Named
@ViewScoped
public class Profile {
	
	private UserDao user;
	
	private UserDto userDto;
	
	private String password;
	
	private String reapeatPasword;
	
	private String email;
	
	private String userId;
	
	private EmailValidator emailValidator;
	
	private PasswordValidator passwordValidator;
	
	private UserSession userSession;

	private Profile profile1;

	private PasswordReset passwordReset1;
	
	/**
	 * As admin delete the user.
	 */
	public void delete() {
		
	}
	
    /**
     * Save the changes made to the profile.
     */
    public void save() {
    	
    }
}
