package de.dedede.model.logic.validators;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks if a new password fulfills the security constraints to have a digit
 * and a capital letter. As well as the password length should be 6 characters
 * or more.
 */
@FacesValidator
public class PasswordValidator implements Validator<String> {
	/**
	 * Checks if a new password fulfills the security constraints to have a digit
	 * and a capital letter. As well as the password length should be 6 characters
	 * or more.
	 *
	 * @param facesContext The FacesContext where this validator has been called.
	 * @param uiComponent  The component where this validator has been called.
	 * @param s            The string of the password to be checked.
	 * @throws ValidatorException if the password criteria are not fulfilled.
	 */

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, String s) throws ValidatorException {

	}
}
