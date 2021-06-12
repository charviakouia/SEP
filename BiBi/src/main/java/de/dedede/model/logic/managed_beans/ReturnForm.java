package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyIsNotAvailableException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the return form. Staff or higher can process returns of lent 
 * copies here.
 * 
 * @author Jonas Picker 
 */
@Named
@ViewScoped
public class ReturnForm implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	/**
	 * Recieves the email address of the user to make the return after value 
	 * change.
	 */
	private UserDto user = new UserDto();

	/**
	 * Holds one copy signature for each input field.
	 */
	private ArrayList<CopyDto> copies = new ArrayList<CopyDto>();

	/**
	 * Initializes the bean with 5 signature input fields.
	 */
	@PostConstruct
	public void init() {
		for(int i = 0; i < 5; i++) {
			copies.add(new CopyDto());
		}
	}


	
	/**
	 * Return the list of existing signatures lent by the existing user into
	 * the libraries inventory.
	 * 
	 * @throws BuisnessException if unknown copy, user or invalid return action
	 */
	public void returnCopies() {
		
		for(CopyDto copy : copies) {
			if (copy.getSignature() != null 
					|| copy.getSignature().trim() != "") {
				try {
					MediumDao.returnCopy(copy, user);
				} catch (CopyDoesNotExistException e) {
					String message = "An unexpected error occured during return"
							+ " process, the copy didn't exist.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (UserDoesNotExistException e) {
					String message = "An unexpected error occured during return"
							+ " process, the user wasn't found in the database"
							+ " or didn't lent this Copy.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (CopyIsNotAvailableException e) {
					String message = "An unexpected error occured during return"
							+ " process, the copy wasn't lent in the"
							+ " first place.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				}
			}
		}
	}
	
	/**
	 * Called by a Listener for value change on email address input field
	 * 
	 * @param change The new email address input
	 */
	public void setUserEmail(ValueChangeEvent change) {
		this.user.setEmailAddress(change.getNewValue().toString());
	}

	/**
	 * Add a signature input field.
	 */
	public void addSignatureInputField() {
		copies.add(new CopyDto());
	}
	
	/*getters and setters*/
	
	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public ArrayList<CopyDto> getCopies() {
		return copies;
	}

	public void setCopies(ArrayList<CopyDto> copies) {
		this.copies = copies;
	}
}
