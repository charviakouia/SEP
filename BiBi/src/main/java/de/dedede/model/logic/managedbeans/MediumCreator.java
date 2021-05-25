package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.MediumDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

//import java.io.Serial;
import java.io.Serializable;

/**
 * Backing bean for the medium creator facelet.
 */
@Named
@ViewScoped
public class MediumCreator implements Serializable {

	//@Serial
	private static  final long serialVersionUID = 1L;

	private MediumDto medium;

	private CopyDto copy;

	private UserDto user;

	/**
	 * Create this medium and its first copy.
	 */
	public void createMediumAndFirstCopy() {

	}

	public MediumDto getMedium() {
		return medium;
	}

	public void setMedium(MediumDto medium) {
		this.medium = medium;
	}

	public CopyDto getCopy() {
		return copy;
	}

	public void setCopy(CopyDto copy) {
		this.copy = copy;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
}
