package dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.UserDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the direct landing page.
 * Here, library staff or higher can lend copies to user.
 *
 */
@Named
@SessionScoped
public class DirectLending implements Serializable {

	@Serial
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

}
