package de.dedede.model.logic.validators;

import java.util.ResourceBundle;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.UserDao;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks for a users E-Mail in the database to make sure it exists.
 * 
 * @author Jonas Picker
 */
@FacesValidator(value = "userExistsValidator", managed = true)
public class UserExistsValidator implements Validator<String> {

	/**
	 * Takes a user email as input and throws an Exception, if the email doesn't
	 * belong to any account in the database.
	 *
	 * @param context 		the FacesContext the validator is registered in.
	 * @param uiComponent  	the UIComponent for this validator.
	 * @param email         the fields value as String
	 * @throws ValidatorException if the user doesn't exist
	 */
	@Override
	public void validate(FacesContext context, UIComponent uiComponent,
			String email) throws ValidatorException {
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		UserDto userDto = new UserDto();
		userDto.setEmailAddress(email);
		if (!UserDao.userEntityWithEmailExists(userDto)){
			String shortMessage = messages.getString("lending.unknown"
	    			+ "_user_short");
	    	String longMessage = messages.getString("lending.unknown"
	    			+ "_user_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
			throw new ValidatorException(msg);
		}
	}

}
