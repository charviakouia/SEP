package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.ApplicationDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Backing bean for the administration facelet.
 * Allows an administrator to update the system settings.
 * Further, they can search for any user inside of the system from here.
 *
 */
@Named
@SessionScoped
public class Admininstration implements Serializable {
	
	private ApplicationDto applicationDto;
	
	/**
	 * Save the changes made to the system settings.
	 */
	public void save() {
		
	}
	
	/**
	 * Search for a user inside of the system.
	 */
	public void searchUser() {
		
	}

}
