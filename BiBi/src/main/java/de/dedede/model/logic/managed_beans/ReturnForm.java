package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the return form.
 */
@Named
@ViewScoped
public class ReturnForm implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	private UserDto user;

	private ArrayList<CopyDto> copies;

	@PostConstruct
	public void init() {

	}

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
	
	/**
	 * As library staff let the system know that the given copies were returned by
	 * the user.
	 */
	public void returnMedium() {

	}

	/**
	 * Add a signature input field.
	 */
	public void addSignatureInputField() {

	}
}