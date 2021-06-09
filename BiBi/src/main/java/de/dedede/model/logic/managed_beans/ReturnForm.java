package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the return form.
 * 
 * @author Jonas Picker 
 */
@Named
@RequestScoped
public class ReturnForm implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private UserDto user;

	private ArrayList<CopyDto> copies;

	@PostConstruct
	public void init() {

	}


	
	/**
	 * As library staff let the system know that the given copies were returned 
	 * by the user.
	 * @throws EntityInstanceDoesNotExistException 
	 */
	public void returnCopies() {     											//runtime?
		for(CopyDto copy : copies) {
			try {
				MediumDao.returnCopy(copy, user);
			} catch (CopyDoesNotExistException e) {
				String message = "An unexpected error occured during return process, the copy didn't exist or wasn't regiostered as lent by the system.";
				Logger.severe(message);
				throw new BusinessException(message, e);
			} catch (UserDoesNotExistException e) {
				String message = "An unexpected error occured during return process, the user wasn't found in the database or didn't lent this Copy.";
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
