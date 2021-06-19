package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
public class Medium extends PaginatedList implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private MediumDto mediumDto;

	private CopyDto newCopy;

	@Inject
	private UserSession userSession;

	@Inject
	private FacesContext context;

	@PostConstruct
	private void init() {
		mediumDto = new MediumDto();
		newCopy = new CopyDto();
	}

	@Override
	public ArrayList<CopyDto> getItems() {
		return new ArrayList<>(mediumDto.getCopies().values());
	}

	@Override
	public void refresh() {
		onload();
	}

	public void onload() {
		try {
			mediumDto = MediumDao.readMedium(mediumDto);
		} catch (MediumDoesNotExistException e) {
			context.addMessage(null, new FacesMessage("There is no medium with this ID."));
		}
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
	 * @throws EntityInstanceNotUniqueException 
	 */
	public void createCopy() throws BusinessException, EntityInstanceNotUniqueException {
		try {
			UUID idOne = UUID.randomUUID();
			String str=""+idOne;
			int uid = str.hashCode();
			String filterStr=""+uid;
			str = filterStr.replaceAll("-", "");
			int newCopyID = Integer.parseInt(str);
			newCopy.setId(newCopyID);
			MediumDao.createCopy(newCopy, mediumDto);
		} catch (LostConnectionException e) {
			String msg = "Database error occurred while creating copy with id: " + newCopy.getId();
			throw new BusinessException(msg, e);
		} catch (MaxConnectionsException e) {
			String msg = "Connection is not available while creating copy with id: " + newCopy.getId();
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
	 * @throws MediumDoesNotExistException 
	 * @throws MaxConnectionsException 
	 * @throws LostConnectionException 
	 */
	public void deleteCopy(String index) throws IllegalArgumentException,
			CopyDoesNotExistException, LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
		int id = Integer.parseInt(index);
		MediumDao.deleteCopy(mediumDto.getCopy(id));
	}

	/**
	 * Save changes made to a copy of this medium.
	 * 
	 * @param copyId The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void saveCopy(String copyId) throws IllegalArgumentException,
			CopyDoesNotExistException {
		int id = Integer.parseInt(copyId);
		MediumDao.updateCopy(mediumDto.getCopy(id));
	}

	/**
	 * Cancel any pending pickup of the a copy this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void cancelPickup(String index) throws IllegalArgumentException {

	}

	/**
	 * Go to the direct lending page taking a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public String lendCopy(String index) throws IllegalArgumentException {
		return null;
	}

	/**
	 * Go to the return form taking a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public String returnCopy(String index) throws IllegalStateException {
		return null;
	}

	/**
	 * Pick up a copy of this medium.
	 * 
	 * @param index The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void pickUpCopy(String index, UserDto user) throws IllegalStateException {
	}

	public MediumDto getMediumDto() {
		return mediumDto;
	}

	public void setMediumDto(MediumDto mediumDto) {
		this.mediumDto = mediumDto;
	}

	public String delete() throws MediumDoesNotExistException {
		MediumDao.deleteMedium(mediumDto);
		return "/view/public/medium-search?faces-redirect=true";
	}

	public CopyDto getNewCopy() {
		return newCopy;
	}

	public void setNewCopy(CopyDto newCopy) {
		this.newCopy = newCopy;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	public FacesContext getContext() {
		return context;
	}

	public void setContext(FacesContext context) {
		this.context = context;
	}
}
