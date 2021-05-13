package dedede.model.logic.managedbeans;

import java.util.ArrayList;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.MediumDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the medium facelet.
 * This page provides information about a specific medium and their copies available in
 * the library. Here, users can borrow a copy by committing themselves to pick up the copy from a
 * library site and actually borrowing it there. To trigger the procedure, they can either click on the
 * pick-up button next to a specific copy or on an isolated button which selects an available copy at
 * random. If the parameter is missing, the user will be redirected to the medium search page.
 */
@Named
@ViewScoped
public class Medium extends PaginatedList {

	private MediumDto medium;

	private ArrayList<CopyDto> copies;

	@PostConstruct
	public void init() {

	}

	/**
	 * Save the changes made to the set of medium attributes.
	 */
	public void update() {

	}

	/**
	 * Update the return period specific to this medium.
	 */
	public void updateReturnPeriod() {

	}

	/**
	 * Insert a new copy of this medium.
	 */
	public void createCopy() {

	}

	/**
	 * Pick up an arbitrary available copy.
	 */
	public void pickUpAnyCopy() {

	}

	/**
	 * Delete a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void deleteCopy(int index) throws IllegalArgumentException {

	}

	/**
	 * Edit a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void editCopy(int index) throws IllegalArgumentException {

	}

	/**
	 * Cancel any pending pickup of the a copy this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void cancelPickup(int index) throws IllegalArgumentException {

	}

	/**
	 * Go to the direct lending page taking a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public String lend(int index) throws IllegalArgumentException {
		return null;
	}

}
