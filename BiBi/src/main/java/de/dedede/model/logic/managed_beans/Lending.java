package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
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
		for(int i = 0; i < 5; i++) {
			copies.add(new CopyDto());
		}
	}
	
	/**
	 * Lend the selected list of copies to the given user.
	 */
	public void lendCopies() {
		for(CopyDto copy : copies) {
			try {																//To-Do: Fehlerbehandlung
				MediumDao.lendCopy(copy, user);
			} catch (EntityInstanceDoesNotExistException e) {
				Logger.development("Dao-Method lendUser() threw Exception");
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
