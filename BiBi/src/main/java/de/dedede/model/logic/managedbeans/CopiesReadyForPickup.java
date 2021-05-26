package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CopyMediumUser;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the facelet containg a list of copies ready for pickup for
 * the current user. On this page a user gets to know which copies they want to
 * pick up from the library and borrow thereafter and how much time they have
 * left to do so until they exceed the lending period.
 */
@Named
@SessionScoped
public class CopiesReadyForPickup extends PaginatedList implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private List<CopyMediumUser> items;

	@PostConstruct
	public void init() {

	}
	
	@Override
	public List<CopyMediumUser> getItems() {
		return items;
	}

}
