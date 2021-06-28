package de.dedede.model.logic.managed_beans;

import java.io.IOException;
import java.util.Base64;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.persistence.daos.ApplicationDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
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
	private ExternalContext ectx;

	@Inject
	private UserSession session;

	private ApplicationDto application;

	// @Note we could also make it flat here!
	private MediumSearchDto mediumSearch = new MediumSearchDto();

	/**
	 * Initialize this backing bean.
	 * 
	 * This method is not meant to be called directly but only by JSF and that only
	 * once!
	 */
	@PostConstruct
	public void init() {
		// there only ever exists one application and it has this specified ID
		final var PROPER_APPLICATION_ID = 1;

		var argument = new ApplicationDto();
		argument.setId(PROPER_APPLICATION_ID);

		application = ApplicationDao.readCustomization(argument);

	}

	/**
	 * Get the application logo in the Base64 format.
	 * 
	 * @return The application logo in Base64.
	 */
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

	/**
	 * Indicates whether context-sensitive help for registered users should be
	 * contained in the help page.
	 * 
	 * @return Flag if context-sensitive help for registered users should be shown.
	 */
	public boolean isShowingAccountHelp() {
		return session.getUser() != null;
	}

	/**
	 * Indicates whether context-sensitive help for library staff should be
	 * contained in the help page.
	 * 
	 * @return Flag if context-sensitive help for library staff should be shown.
	 */
	public boolean isShowingStaffHelp() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	/**
	 * Indicates whether context-sensitive help for administrators should be
	 * contained in the help page.
	 * 
	 * @return Flag if context-sensitive help for administrators should be shown.
	 */
	public boolean isShowingAdminHelp() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole() == UserRole.ADMIN;
	}

	/**
	 * Indicates whether the dropdown menu containing account-related links gets
	 * displayed.
	 * 
	 * @return If the account menu gets shown.
	 */
	public boolean isShowingAccountMenu() {
		return session.getUser() != null;
	}

	/**
	 * Indicates whether the dropdown menu containg staff-specific links gets
	 * displayed.
	 * 
	 * @return If the staff menu gets shown.
	 */
	public boolean isShowingStaffMenu() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole().isStaffOrHigher();
	}

	/**
	 * Indicates whether the dropdown menu containing administrator-specific links
	 * gets displayed.
	 * 
	 * @return If the administrator menu gets shown.
	 */
	public boolean isShowingAdministratorMenu() {
		if (session.getUser() == null) {
			return false;
		}

		return session.getUser().getRole() == UserRole.ADMIN;
	}

	/**
	 * Indicates whether the hyperlink to the login page gets displayed.
	 * 
	 * @return If the link to the login page gets shown.
	 */
	public boolean isShowingLogin() {
		return session.getUser() == null;
	}

	/**
	 * Indicates whether the hyperlink to the registration page gets displayed.
	 * 
	 * @return If the link to the registration page gets shown.
	 */
	public boolean isShowingRegistration() {
		return session.getUser() == null;
	}

	/**
	 * It will be called when the user clicks on the logout button. The current
	 * session of the user is invalidated and the user is lead back to the
	 * {@link Login}.
	 * 
	 * @return the String to the login page.
	 * @throws IOException
	 */
	public void logOut() throws IOException {
		session.setUser(null);
		ectx.invalidateSession();
		ectx.redirect(ectx.getRequestContextPath() + "/view/ffa/login.xhtml");
	}

	public String searchMedium() {
		ectx.getFlash().put("medium_search_term", mediumSearch.getGeneralSearchTerm());

		return "medium-search?faces-redirect=true";
	}
}
