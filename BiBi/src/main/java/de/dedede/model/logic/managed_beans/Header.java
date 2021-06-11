package de.dedede.model.logic.managed_beans;

import java.util.Base64;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.persistence.daos.ApplicationDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the header facelet of the application. Implements functions
 * necessary for the header facelet into the template.
 */

@Named("pageHeader") // "header" is reserved / an implicit object in EL
@RequestScoped
public class Header {

	@Inject
	private UserSession session;

	private ApplicationDto application;

	private MediumSearchDto mediumSearch = new MediumSearchDto();

	@PostConstruct
	public void init() {
		final var PROPER_APPLICATION_ID = 1;

		var argument = new ApplicationDto();
		argument.setId(PROPER_APPLICATION_ID);

		application = ApplicationDao.readCustomization(argument);

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

	public MediumSearchDto getMediumSearch() {
		return mediumSearch;
	}

	public void setMediumSearch(MediumSearchDto mediumSearch) {
		this.mediumSearch = mediumSearch;
	}

	public boolean showingAccountHelp() {
		return session.getUser() != null;
	}

	public boolean showingStaffHelp() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	public boolean showingAdminHelp() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole() == UserRole.ADMIN;
	}

	public boolean showingLogOut() {
		return session.getUser() != null;
	}

	public boolean showingLending() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	public boolean showingReturnForm() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	public boolean showingCopiesReadyForPickupAllUsers() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	public boolean showingMediumCreator() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	public boolean showingProfile() {
		return session.getUser() != null;
	}

	public boolean showingAdministration() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole() == UserRole.ADMIN;
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
	 * @return the String to the login page.
	 */
	public String logOut() {
		session.setUser(null);
		// FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

		return "/BiBi/view/public/login.xhtml?faces-redirect=true";
	}

	public String searchMedium() {
		// @Task put the term into a flash scope
		return "/BiBi/view/public/medium-search.xhtml?faces-redirect=true";
	}
}
