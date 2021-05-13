package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.ApplicationDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the contact information facelet.
 * The contact page allows any user to get to know how to contact the site owners.
 *
 */
@Named
@RequestScoped
public class Contact {

	private ApplicationDto application;
	
	public void save() {
		
	}

}
