package de.dedede.model.logic.managed_beans;

import java.util.Base64;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserRole;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the header facelet of the application. Implements functions
 * necessary for the header facelet into the template.
 */

@Named("header_") // "header" is reserved / an implicit object in EL
@Dependent
public class Header {

	@Inject
	private UserSession session;

	private ApplicationDto application;

	private MediumSearch mediumSearch;

	@PostConstruct
	public void init() {
	}
	
	public String getApplicationBase64Logo() {
		return Base64.getEncoder().encodeToString(application.getLogo());
	}

	public ApplicationDto getApplication() {
		return application;
	}

	public void setApplication(ApplicationDto application) {
		this.application = application;
	}

	public MediumSearch getMediumSearch() {
		return mediumSearch;
	}

	public void setMediumSearch(MediumSearch mediumSearch) {
		this.mediumSearch = mediumSearch;
	}
	
	public boolean showingAccountHelp() {
		return session.getUser() != null;
	}
	
	public boolean showingStaffHelp() {
	    // return session.getUser().getRole().isStaffOrHigher();
	    return true;
	}
	
	public boolean showingAdminHelp() {
		// return session.getUser().getRole() == UserRole.ADMIN;
	    return true;
	}

	public boolean showingLogOut() {
		return session.getUser() != null;
	}

	public boolean showingLending() {
	    // return session.getUser().getRole().isStaffOrHigher();
        return true;
	}

	public boolean showingReturnForm() {
		// return session.getUser().getRole().isStaffOrHigher();
	    return true;
	}

	public boolean showingCopiesReadyForPickupAllUsers() {
		// return session.getUser().getRole().isStaffOrHigher();
        return true;
	}

	public boolean showingMediumCreator() {
		// return session.getUser().getRole().isStaffOrHigher();
	    return true;
	}

	public boolean showingProfile() {
		return session.getUser() != null;
	}

	public boolean showingAdministration() {
		// return session.getUser().getRole() == UserRole.ADMIN;
	    return true;
	}

	public boolean showingLogin() {
		return session.getUser() == null;
	}

	public boolean showingRegistration() {
		return session.getUser() == null;
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
	 * Displays the Help text when user click on it.
	 */
	public void displayHelpText() {
	}
}
