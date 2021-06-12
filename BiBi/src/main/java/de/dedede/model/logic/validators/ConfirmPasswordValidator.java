package de.dedede.model.logic.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks if a new password corresponds with the repeated input of the password
 * in order to ensure the user typed it in correctly.
 */
@FacesValidator("confirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator<String> {

	/**
	 * Checks whether the first input of the password matches the second one.
	 *
	 * @param ctx            The FacesContext where this validator has been called.
	 * @param comp           The component where this validator has been called.
	 * @param passwordRepeat The String containing the passwords to be checked.
	 * @throws ValidatorException if the two inputs are not equal.
	 */
	public void validate(FacesContext ctx, UIComponent comp, String passwordRepeat) throws ValidatorException {
		String password = getStringFromComponent(comp, "password");
		if (!password.equals(passwordRepeat)){
			FacesMessage msg = new FacesMessage("Entered passwords do not match");
			throw new ValidatorException(msg);
		}
	}

	private String getStringFromComponent(UIComponent comp, String compName){
		UIInput input = (UIInput) comp.findComponent(compName);
		String result = (String) input.getLocalValue();
		if (result == null){
			return "";
		} else {
			return result;
		}
	}

}
