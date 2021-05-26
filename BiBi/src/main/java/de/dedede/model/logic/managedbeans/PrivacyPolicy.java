package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.ApplicationDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the privacy policy page. This page declares the privacy
 * policy of this system.
 */
@Named
@ViewScoped
public class PrivacyPolicy implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

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
	 * Save the changes made to the privacy policy by an admin.
	 */
	public void save() {
	}

}
