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
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the direct landing page. Here, library staff or higher can
 * lend copies to user.
 *
 * @author Jonas Picker
 */
@Named
@ViewScoped
public class Lending implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;
	
	/**
	 * Recieves the email address of the top input field on value change 
	 */
	private UserDto user = new UserDto();
	
	/**
	 * Reflects the input fields for signatures as an ArrayList
	 */
	private ArrayList<CopyDto> copies = new ArrayList<CopyDto>();

	/**
	 * Initializes the Backing Bean with 5 copy signature input fields.
	 */
	@PostConstruct
	public void init() {
		for(int i = 0; i < 5; i++) {
			copies.add(new CopyDto());
		}
	}
	
	/**
	 * Lend the list of copies with existing signatures to the existing user.
	 * 
	 * @throws BuisnessException if unknown copy, user or invalid lending action
	 */
	public void lendCopies() {
		for(CopyDto copy : copies) {
			if (copy.getSignature() != null 
					&& copy.getSignature().trim() != "") {
				try {
					MediumDao.lendCopy(copy, user);
				} catch (CopyDoesNotExistException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the copy didn't exist.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (UserDoesNotExistException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the user wasn't found in "
							+ "the database.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (CopyIsNotAvailableException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the copy is not available "
							+ "for lending.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (InvalidUserForCopyException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the copy wasn't marked for "
							+ "pickup by this user.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				}
			}
		}
	}
	
	/**
	 * Action for a listener on the user email input field.
	 * 
	 * @param change holds the new user Email that was put in.
	 */
	public void setUserEmail(ValueChangeEvent change) {
		user.setEmailAddress(change.getNewValue().toString());
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
