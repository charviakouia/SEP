package dedede.model.logic.managedbeans;

import java.util.ArrayList;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.UserDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class Return {
	
	private UserDto user;
	
	private ArrayList<CopyDto> copies;
	
	public void returnMediums() {
		
	}
	
	public void addSignaturInputField() {
		
	}

}
