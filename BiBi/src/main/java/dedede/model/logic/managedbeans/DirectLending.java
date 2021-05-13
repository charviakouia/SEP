package dedede.model.logic.managedbeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.UserDto;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named
@SessionScoped
public class DirectLending implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	private UserDto user;

	private ArrayList<CopyDto> copies;

	public void lendMedium() {

	}

	public void addSignatureInputField() {

	}

}
