package de.dedede.model.logic.managed_beans;

import java.io.Serializable;
import java.io.Serial;

import de.dedede.model.data.dtos.ApplicationDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

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
	private static final long serialVersionUID = 1L;
	
	/**
	 * The settings for the application.
	 */
	private ApplicationDto application;

	@PostConstruct
	public void init() {

	}

	public ApplicationDto getApplication() {
		return application;
	}

	public void setApplication(ApplicationDto application) {
		this.application = application;
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
}