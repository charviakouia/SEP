package de.dedede.model.logic.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the return form.
 */
@Named
@ViewScoped
public class ReturnForm implements Serializable {

	private UserDto user;

	private ArrayList<CopyDto> copies;

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
