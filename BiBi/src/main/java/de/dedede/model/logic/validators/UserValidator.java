package de.dedede.model.logic.validators;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.UserDao;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

/**
 * The UserValidator for validating if a user already exists.
 */
@FacesValidator("userEmailValidator")
public class UserValidator implements Validator<String> {

	/**
	 * validating if a user already exists
	 *
	 * @param facesContext 	the FacesContext
	 * @param uiComponent  	the UIComponent
	 * @param s         	the UserDto
	 * @throws ValidatorException the validator exception
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, String s) throws ValidatorException {
		UserDto userDto = new UserDto();
		userDto.setEmailAddress(s);
		if (UserDao.userEntityWithEmailExists(userDto)){
			FacesMessage msg = new FacesMessage("The email " + userDto.getEmailAddress()
					+ " was already taken");
			throw new ValidatorException(msg);
		}
	}

}
