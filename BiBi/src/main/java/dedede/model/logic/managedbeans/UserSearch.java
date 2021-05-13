package dedede.model.logic.managedbeans;

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the user search page.
 */
@Named
@RequestScoped
public class UserSearch extends PaginatedList implements Serializable {

	private String searchValue;

	/**
	 * Search for a user inside of the system.
	 */
	public void userSearch() {

	}

}
