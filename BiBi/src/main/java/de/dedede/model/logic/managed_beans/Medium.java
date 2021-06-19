package de.dedede.model.logic.managed_beans;

import de.dedede.model.data.dtos.*;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
	 * @throws BusinessException
	 */
	public void createCopy() throws BusinessException {
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			prepareNewCopy(newCopy);
			MediumDao.createCopy(newCopy, mediumDto);
			newCopyClean(newCopy);
			refresh();
		} catch (LostConnectionException e) {
			String msg = "Database error occurred while creating copy with id: " + newCopy.getId();
			throw new BusinessException(msg, e);
		} catch (MaxConnectionsException e) {
			String msg = "Connection is not available while creating copy with id: " + newCopy.getId();
			throw new BusinessException(msg, e);
		} catch (EntityInstanceNotUniqueException exception) {
			context.addMessage(null, new FacesMessage(messages.getString("copy.notUnique")));
		}
	}

	private void newCopyClean(CopyDto newCopy) {
		newCopy.setLocation(null);
		newCopy.setId(0);
		newCopy.setSignature(null);
	}

	private void prepareNewCopy(CopyDto newCopy) {
		UUID idOne = UUID.randomUUID();
		String str=""+idOne;
		int uid = str.hashCode();
		String filterStr=""+uid;
		str = filterStr.replaceAll("-", "");
		int newCopyID = Integer.parseInt(str);
		newCopy.setId(newCopyID);
		newCopy.setCopyStatus(CopyStatus.AVAILABLE);
		newCopy.setMediumId(mediumDto.getId());
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
	 * @param copyDto The id of the deleted copy.
	 * @throws MediumDoesNotExistException 
	 * @throws MaxConnectionsException 
	 * @throws LostConnectionException 
	 */
	public void deleteCopy(CopyDto copyDto) throws IOException {
		copyDto.setMediumId(mediumDto.getId());
		Logger.development("This id: " + copyDto.getId());
		ResourceBundle messages =
				context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		try {
			MediumDao.deleteCopy(copyDto);
			throw new BusinessException("bla bla bla");
		} catch (MediumDoesNotExistException e) {
			context.addMessage(null, new FacesMessage(messages.getString("medium.doesntExist")));
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/BiBi/view/public/medium-search.xhtml");
		}
	}

	/**
	 * Save changes made to a copy of this medium.
	 * 
	 * @param copyDto The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void saveCopy(CopyDto copyDto) throws IllegalArgumentException,
			CopyDoesNotExistException {
		MediumDao.updateCopy(copyDto);
		refresh();
	}

	/**
	 * Cancel any pending pickup of the a copy this medium.
	 * 
	 * @param copyDto The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void cancelPickup(CopyDto copyDto) throws IllegalArgumentException {

	}

	/**
	 * Go to the direct lending page taking a copy of this medium.
	 * 
	 * @param copyDto The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public String lendCopy() throws IllegalArgumentException {
		return "/BiBi/view/staff/lending.xhtml?faces-redirect=true";
	}

	/**
	 * Go to the return form taking a copy of this medium.
	 * 
	 * @param copyDto The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public String returnCopy() throws IllegalStateException {
		return "/BiBi/view/staff/return-copy.xhtml?faces-redirect=true";
	}

	/**
	 * Pick up a copy of this medium.
	 * 
	 * @param copyDto The index into the list of copies.
	 * @throws IllegalArgumentException If the index is out of bounds.
	 */
	public void pickUpCopy(CopyDto copyDto, UserDto user) throws IllegalStateException {
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
