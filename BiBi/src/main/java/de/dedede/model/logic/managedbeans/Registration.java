package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * The backing bean for the registration page. On this page an anonymous user
 * can register themself. Additionally, it can be used by administrators to
 * register new users.
 */
@Named
@RequestScoped
public class Registration {

	private ApplicationDto application;

	private UserDto user;


	@PostConstruct
	public void init() {

	}

	public ApplicationDto getApplication() {
		return application;
	}

	public void setApplication(ApplicationDto application) {
		this.application = application;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
	
	/**
	 * As an anonymous user register to the system.
	 */
	public void register() {

	}
}
