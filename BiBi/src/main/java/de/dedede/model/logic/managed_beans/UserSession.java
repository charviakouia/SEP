package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.util.Logger;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Managed bean for the user session containing the logged-in user.
 */
@Named
@SessionScoped
public class UserSession implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private UserDto user;

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public boolean isLoggedIn() {
		return user != null;
	}
}
