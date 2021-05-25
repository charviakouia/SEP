package de.dedede.model.logic.managedbeans;


//import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the direct landing page. Here, library staff or higher can
 * lend copies to user.
 *
 */
@Named
@SessionScoped
public class DirectLending implements Serializable {

	//@Serial
	private static final long serialVersionUID = 1;

	private UserDto user;

	private ArrayList<CopyDto> copies;

	/**
	 * Lend the selected list of copies to the given user.
	 */
	public void lendCopies() {

	}

	/**
	 * Add a signature input field.
	 */
	public void addSignatureInputField() {

	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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
}
