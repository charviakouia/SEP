package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.ApplicationDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

/**
 * Backing bean for the administration facelet. Allows an administrator to
 * update the system settings. Further, they can search for any user inside of
 * the system from here. This Backing Bean for system settings too.
 *
 */
@Named
@ViewScoped
public class Administration implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;
	/**
	 * The settings for the application.
	 */
	private ApplicationDto application;


	@PostConstruct
	public void init(){

	}
	/**
	 * Save the changes made to the system settings.
	 */
	public void save() {

	}

	/**
	 * Search for a user inside of the system.
	 */
	public String searchUser() {
		return "";

	}

	public ApplicationDto getApplication() {
		return application;
	}

	public void setApplication(ApplicationDto application) {
		this.application = application;
	}
}
