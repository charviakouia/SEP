package de.dedede.model.logic.validators;

import de.dedede.model.logic.util.MessagingUtility;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks whether the user's entered password and the repeated password match. This serves to confirm that the user
 * intended to use the given password.
 *
 * @author Ivan Charviakou
 */
@FacesValidator("confirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator<String> {

	private static final String firstPasswordComponentName = "password";

	/**
	 * Checks whether the first and second password inputs match.
	 *
	 * @param ctx The FacesContext with which this validator has been called.
	 * @param comp The component on which validator has been called.
	 * @param passwordRepeat The String containing the second password to be checked.
	 * @throws ValidatorException Is thrown if the two inputs are not equal.
	 */
	@Override
	public void validate(FacesContext ctx, UIComponent comp, String passwordRepeat) throws ValidatorException {
		String password = getStringFromComponent(comp, firstPasswordComponentName);
		if (!password.equals(passwordRepeat)){
			String msg = MessagingUtility.getMessage(ctx, "userValidator.passwordsDoNotMatch");
			FacesMessage fmsg = new FacesMessage(msg);
			throw new ValidatorException(fmsg);
		}
	}

	private String getStringFromComponent(UIComponent component, String componentName){
		UIInput input = (UIInput) component.findComponent(componentName);
		String result = (String) input.getLocalValue();
		if (result == null){
			return "";
		} else {
			return result;
		}
	}

}
