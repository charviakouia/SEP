package de.dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CopyDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
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
	private static  final long serialVersionUID = 1L;

	private List<CopyDto> data;
	/**
	 * The list of borrowed copies.
	 */
	private List<CopyDto> listDto;

	/**
	 * Loads the copies for the view
	 */
	public void loadCopies(){

	}

	public List<CopyDto> getData(){
		return data;
	}

	/**
	 * Sorts the borrowed copies as the user wants.
	 * @param value the value which user wants to sort.
	 */
	public void sortBy(String value){

	}

	/**
	 * The user session.
	 */
	@Inject
    private UserSession userSession;

}
