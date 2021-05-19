package de.dedede.model.logic.validators;

import de.dedede.model.data.dtos.UserDto;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

/**
 * The UserValidator for validating if a user already exists.
 */
@Named
@FacesValidator
public class UserValidator implements Validator<String> {

	private UserDto userValidator;

	/**
	 * validating if a user already exists
	 *
	 * @param facesContext the FacesContext
	 * @param uiComponent  the UIComponent
	 * @param user         the UserDto
	 * @throws ValidatorException the validator exception
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, String user) throws ValidatorException {

	}
}
