package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserRole;
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

	/**
	 * holds the users data, this evaluates to null if the bean was instanziated
	 *  prior to the user being logged in.
	 */
	private UserDto user;

	/**
	 * Allows the user data to be retrieved programmatically.
	 * 
	 * @return the data in a Dto.
	 */
	public UserDto getUser() {
		return user;
	}

	/**
	 * Allows the user data to be modified programmatically.
	 * 
	 * @param user the Dto containing the new data.
	 */
	public void setUser(UserDto user) {
		this.user = user;
	}

	public boolean isAdmin() {
		return user != null && user.getRole() != null && user.getRole().equals(UserRole.ADMIN);
	}

}
