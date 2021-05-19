package de.dedede.model.logic.managedbeans;

import java.io.Serializable;

import de.dedede.model.logic.util.UserLendStatus;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the user search page.
 */
@Named
@RequestScoped
public class UserSearch extends PaginatedList implements Serializable {

	private String searchTerm;

	private UserLendStatus userLendStatus;

	/**
	 * Search for a user inside of the system.
	 */
	public void userSearch() {

	}

}
