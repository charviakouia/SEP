package de.dedede.model.logic.validators;

import java.util.regex.Pattern;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.daos.UserDao;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks whether a user email can be associated with a new account. Specifically, it is compared to the
 * currently set regex and is looked up to eliminate duplicates.
 *
 * @author Ivan Charviakou
 */
@FacesValidator("userEmailValidator")
public class UserValidator implements Validator<String> {

	private static final long DEFAULT_ID = 1;
	private static final String EMAIL_PATTERN_KEY = "userValidator.emailPattern";
	private static final String EMAIL_TAKEN_KEY = "userValidator.emailTaken";
	
	/**
	 * Compares the submitted email with the application's regex settings and checks the datastore for a
	 * duplicate entry.
	 *
	 * @param facesContext JSF's faces context
	 * @param uiComponent The UIComponent, on which the validation is being performed
	 * @param s The submitted email-string
	 * @throws ValidatorException the validator exception
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, String s) throws ValidatorException {
		checkPattern(facesContext, s);
		checkEmailUniqueness(facesContext, s);
	}

	private void checkPattern(FacesContext context, String s){
		ApplicationDto appDto = new ApplicationDto();
		appDto.setId(DEFAULT_ID);
		ApplicationDao.readCustomization(appDto);
		if (appDto.getEmailSuffixRegEx() != null && !Pattern.matches(appDto.getEmailSuffixRegEx(), s)){
			String msg = MessagingUtility.getMessage(context, EMAIL_PATTERN_KEY, appDto.getEmailSuffixRegEx());
			throw new ValidatorException(new FacesMessage(msg));
		}
	}

	private void checkEmailUniqueness(FacesContext context, String s){
		UserDto userDto = new UserDto();
		userDto.setEmailAddress(s);
		if (UserDao.userEntityWithEmailExists(userDto)){
			String msg = MessagingUtility.getMessage(context, EMAIL_TAKEN_KEY, s);
			throw new ValidatorException(new FacesMessage(msg));
		}
	}

}
