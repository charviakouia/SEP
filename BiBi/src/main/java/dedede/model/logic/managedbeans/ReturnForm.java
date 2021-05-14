package dedede.model.logic.managedbeans;

import java.util.ArrayList;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.UserDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the return form.
 */
@Named
@ViewScoped
public class ReturnForm {
	
	private UserDto user;
	
	private ArrayList<CopyDto> copies;
	
	/**
	 * As library staff let the system know that the given copies were returned by the user.
	 */
	public void returnCopies() {
		
	}
	
	/**
	 * Add a signature input field.
	 */
	public void addSignaturInputField() {
		
	}

}
