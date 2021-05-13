package dedede.model.logic.managedbeans;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.MediumDto;
import dedede.model.data.dtos.UserDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the medium creation facelet.
 */
@Named
@ViewScoped
public class MediumCreation {
	
	private MediumDto medium;
	
	private CopyDto copy;
	
	private UserDto user;
	
	/**
	 * Create this medium and its first copy.
	 */
	public void createMediumAndFirstCopy() {
		
	}

}
