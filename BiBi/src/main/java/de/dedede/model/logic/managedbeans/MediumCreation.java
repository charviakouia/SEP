package de.dedede.model.logic.managedbeans;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.MediumDto;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Backing bean for the medium creation facelet.
 */
@Named
@ViewScoped
public class MediumCreation implements Serializable {

	private MediumDto medium;

	private CopyDto copy;

	private UserDto user;

	/**
	 * Create this medium and its first copy.
	 */
	public void createMediumAndFirstCopy() {

	}

}
