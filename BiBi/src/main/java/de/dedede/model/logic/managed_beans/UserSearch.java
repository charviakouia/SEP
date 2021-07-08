package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.data.dtos.UserSearchColumn;
import de.dedede.model.data.dtos.UserSearchDto;
import de.dedede.model.persistence.daos.UserDao;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the user search page.
 */
@Named
@ViewScoped
public class UserSearch extends PaginatedList<UserSearchColumn> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private UserSearchDto userSearch = new UserSearchDto();

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
	public void searchUsers() {
		users = UserDao.searchUsers(userSearch, getPaginatedList());
	}

	public boolean getRestrictionToDisabledLendStatus() {
		return userSearch.getLendStatus() == UserLendStatus.DISABLED;
	}

	public void setRestrictonToDisabledLendStatus(boolean status) {
		userSearch.setLendStatus(status ? UserLendStatus.DISABLED : UserLendStatus.ENABLED);
	}

	/**
	 * Get all existing user roles.
	 * 
	 * This is a mere delegation method to the static method
	 * {@link UserRole.values()}.
	 * 
	 * @return All existing user roles.
	 */
	public UserRole[] getAllUserRoles() {
		return UserRole.values();
	}
	
	public void setBlockedUserSearchRestriction(boolean blocked) {
		userSearch.setLendStatus(blocked ? UserLendStatus.DISABLED : UserLendStatus.ENABLED);
	}
	
	public boolean getBlockedUserSearchRestriction() {
		return userSearch.getLendStatus() == UserLendStatus.DISABLED;
	}

	@Override
	public List<UserDto> getItems() {
		return users;
	}

	@Override
	public void refresh() {
		searchUsers();
	}

}
