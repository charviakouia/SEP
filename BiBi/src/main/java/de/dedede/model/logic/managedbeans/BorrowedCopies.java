package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CopyDto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the page containing a list of borrowed copies of the current
 * user. This page informs the user which copies they borrowed from the library
 * and how much time they still have left until they have to return them to not
 * exceed the lending period.
 */
@Named
@SessionScoped
public class BorrowedCopies extends PaginatedList implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * The list of borrowed copies.
	 */
	private List<CopyDto> copies;

	@PostConstruct
	public void init() {

	}
	
	/**
	 * Loads the copies for the view
	 */
	public void loadCopies(){

	}

	@Override
	public List<CopyDto> getItems() {
		return copies;
	}
}
