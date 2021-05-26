package de.dedede.model.logic.managedbeans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * Backing bean for the header facelet of the application. Implements functions
 * necessary for the header facelet into the template.
 */

@Named
@Dependent
public class Header {

	@PostConstruct
	public void init() {

	}
	
	/**
	 * It will be called when the user clicks on the logout button. The current
	 * session of the user is invalidated and the user is lead back to the
	 * {@link Login}.
	 * 
	 * @return the String to the login and Register Page.
	 */
	public String logOut() {
		return "";
	}


	/**
	 *Displays the Help text when user click on it.
	 */
	public void displayHelpText(){

	}
}
