package de.dedede.model.logic.validators;

import java.util.ResourceBundle;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
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
@FacesValidator(value = "userExistsAndCanLendValidator", managed = true)
public class UserExistsAndCanLendValidator implements Validator<String> {

	/**
	 * Takes a user email as input and throws an Exception, if the email doesn't
	 * belong to any account in the database or the account is excluded
	 * from the lending process
	 *
	 * @param context 		the FacesContext the listener is registered in
	 * @param uiComponent  	the UIComponent for this listener
	 * @param email         the fields value as String
	 * @throws ValidatorException if the user doesn't exist or is blocked from 
	 * 								lending
	 */
	@Override
	public void validate(FacesContext context, UIComponent uiComponent,
			String email) throws ValidatorException {
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		UserDto userDto = new UserDto();
		userDto.setEmailAddress(email);
		try {
			UserDao.validateUserLending(userDto);
		} catch (UserDoesNotExistException e1) {
			String shortMessage = messages.getString("lending.unknown"
	    			+ "_user_short");
	    	String longMessage = messages.getString("lending.unknown"
	    			+ "_user_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
			throw new ValidatorException(msg);
		} catch (InvalidUserForCopyException e2) {
			String shortMessage = messages.getString("lending.blocked_user"
					+ "_short");
	    	String longMessage = messages.getString("lending.blocked_user"
	    			+ "_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
			throw new ValidatorException(msg);
		}			
	}
}

