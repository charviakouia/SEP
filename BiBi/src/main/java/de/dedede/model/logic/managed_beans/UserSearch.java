package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserSearchDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the user search page.
 */
@Named
@RequestScoped
public class UserSearch extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private UserSearchDto userSearch;

	private List<UserDto> users;

	@PostConstruct
	public void init() {

	}
	
	public UserSearchDto getUserSearch() {
		return userSearch;
	}

	public void setUserSearch(UserSearchDto userSearch) {
		this.userSearch = userSearch;
	}
	
	/**
	 * Search for a user inside of the system.
	 */
	public void searchUser() {

	}

	@Override
	public List<UserDto> getItems() {
		return users;
	}

}
