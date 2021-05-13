package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.ApplicationDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the privacy policy page.
 * This page declares the privacy policy of this system.
 */
@Named
@ViewScoped
public class PrivacyPolicy {
	
	private ApplicationDto application;

	/**
	 * Save the changes made to the privacy policy by an admin.
	 */
	public void save() {}

}
