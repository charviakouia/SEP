package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.ApplicationDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the contact information facelet. The contact page allows any
 * user to get to know how to contact the site owners.
 *
 */
@Named
@ViewScoped
public class Contact {

	@Inject
	private ApplicationDto application;



	@PostConstruct
	public  void init(){

	}
	public ApplicationDto getApplication() {
		return application;
	}



	public void setApplication(ApplicationDto application) {
		this.application = application;
	}

	public void save() {

	}

}
