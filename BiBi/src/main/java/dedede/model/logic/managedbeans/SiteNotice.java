package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.ApplicationDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the site notice.
 */
@Named
@RequestScoped
public class SiteNotice {

	private ApplicationDto application;
	
	/**
	 * Save the changes made to the site notice by an admin.
	 */
	public void save() {
		
	}

}
