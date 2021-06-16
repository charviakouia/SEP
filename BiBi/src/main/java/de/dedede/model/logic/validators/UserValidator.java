package de.dedede.model.logic.validators;

import java.util.regex.Pattern;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.ApplicationDao;
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

	private static final long DEFAULT_ID = 1;
	
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
		
		// Check if email has correct pattern
		ApplicationDto appDto = new ApplicationDto();
		appDto.setId(DEFAULT_ID);
		ApplicationDao.readCustomization(appDto);
		if (appDto != null && appDto.getEmailAddressSuffixRegEx() != null) {
			if (!Pattern.matches(appDto.getEmailAddressSuffixRegEx(), s)){
				throw new ValidatorException(new FacesMessage(
						"Given email doesn't match pattern:" + appDto.getEmailAddressSuffixRegEx()));
			}
		}
		
		// Check if email was already taken
		UserDto userDto = new UserDto();
		userDto.setEmailAddress(s);
		if (UserDao.userEntityWithEmailExists(userDto)){
			FacesMessage msg = new FacesMessage(
					"The email " + userDto.getEmailAddress() + " was already taken");
			throw new ValidatorException(msg);
		}
		
	}

}
