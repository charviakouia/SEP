package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the direct landing page. Here, library staff or higher can
 * lend copies to user.
 *
 * @author Jonas Picker
 */
@Named
@RequestScoped
public class Lending implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	private UserDto user;

	private ArrayList<CopyDto> copies;

	@PostConstruct
	public void init() {
		MediumDao.refreshCopyStatusIfMarked();
		for(int i = 0; i < 5; i++) {
			copies.add(new CopyDto());
		}
	}
	
	/**
	 * Lend the selected list of copies to the given user.
	 */
	public void lendCopies() {
		for(CopyDto copy : copies) {
			try {
				MediumDao.lendCopy(copy, user);
			} catch (CopyDoesNotExistException e) {
				String message = "An unexpected error occured during lending process, the copy didn't exist or the transaction was invalid.";
				Logger.severe(message);
				throw new BusinessException(message, e);
			} catch (UserDoesNotExistException e) {
				String message = "An unexpected error occured during lending process, the user wasn't found in the database.";
				Logger.severe(message);
				throw new BusinessException(message, e);
			}
		}
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
