package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.ApplicationDao;
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

	private ApplicationDao applicationDao;

	private UserDto userDto;

	private String emailRegex;

	@PostConstruct
	public void init() {

	}

	/**
	 * As an anonymous user register to the system.
	 */
	public void register() {

	}
}
