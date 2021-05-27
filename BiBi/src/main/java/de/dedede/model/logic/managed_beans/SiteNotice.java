package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.ApplicationDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the site notice.
 */
@Named
@RequestScoped
public class SiteNotice {

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
	 * Save the changes made to the site notice by an admin.
	 */
	public void save() {

	}
}
