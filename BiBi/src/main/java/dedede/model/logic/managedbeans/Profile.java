package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.UserDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
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
	
	private UserDto user;
	
	private String password;
	
	private String confirmedPassword;
	
	@Inject
	private UserSession userSession;
	
	/**
	 * Close one's own account if this is the profile of the current user or delete a user as an admin.
	 */
	public void delete() {
		
	}
	
    /**
     * Save the changes made to the profile.
     */
    public void save() {
    	
    }
}
