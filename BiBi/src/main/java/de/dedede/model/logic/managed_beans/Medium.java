package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.UserDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;

/**
 * Backing bean for the medium facelet. This page provides information about a
 * specific medium and their copies available in the library. Here, users can
 * borrow a copy by committing themselves to pick up the copy from a library
 * site and actually borrowing it there. To trigger the procedure, they can
 * either click on the pick-up button next to a specific copy or on an isolated
 * button which selects an available copy at random. If the parameter is
 * missing, the user will be redirected to the medium search page.
 */
@Named
@ViewScoped
public class Medium implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private MediumDto mediumDto;

	@PostConstruct

	private void init() {
		mediumDto = new MediumDto();
	}

	/**
	 * Save the changes made to the set of medium attributes.
	 */
	public void saveAttributes() {
	}

	/**
	 * Update the return period specific to this medium.
	 */
	public void updateReturnPeriod() {

	}

	/**
	 * Insert a new copy of this medium.
	 */

	public void createCopy() throws BusinessException {
		CopyDto newCopyDto = new CopyDto();
		try {
			MediumDao.createCopy(newCopyDto, mediumDto);
		} catch (LostConnectionException e) {
			String msg = "Database error occurred while creating copy with id: " + newCopyDto.getId();
			throw new BusinessException(msg, e);
		} catch (MaxConnectionsException e) {
			String msg = "Connection is not available while creating copy with id: " + newCopyDto.getId();
			throw new BusinessException(msg, e);
		} catch (EntityInstanceNotUniqueException e) {
			String msg = "A copy with this ID already exists: " + newCopyDto.getId();
			throw new BusinessException(msg, e);
		}

	}

	/**
	 * Pick up an arbitrary available copy.
	 */
	public void pickUpAnyCopy() {

	}

	/**
	 * Get the minimum return period of of the user-specific, the medium-specific
	 * and the global one.
	 */
	public String getReturnPeriod() {
		return null;
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
	 * Save changes made to a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void saveCopy(int index) throws IllegalArgumentException {

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
	public String lendCopy(int index) throws IllegalArgumentException {
		return null;
	}

	/**
	 * Go to the return form taking a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public String returnCopy(int index) throws IllegalStateException {
		return null;
	}

	/**
	 * Pick up a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void pickUpCopy(int index, UserDto user) throws IllegalStateException {
	}

	public MediumDto getMediumDto() {
		return mediumDto;
	}

	public void setMediumDto(MediumDto mediumDto) {
		this.mediumDto = mediumDto;
	}
}
