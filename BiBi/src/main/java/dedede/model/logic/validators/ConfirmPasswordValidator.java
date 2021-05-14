package dedede.model.logic.validators;


import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks if a new password corresponds with the repeated input of the password
 * in order to ensure the user typed it in correctly.
 */
public class ConfirmPasswordValidator implements Validator<String> {

	/**
	 * Checks whether the first input of the password matches the second one.
	 *
	 * @param ctx            The FacesContext where this validator has been called.
	 * @param comp           The component where this validator has been called.
	 * @param passwordRepeat The String containing the passwords to be
	 *                       checked.
	 * @throws ValidatorException if the two inputs are not equal.
	 */
	public void validate(FacesContext ctx, UIComponent comp, String passwordRepeat)
			throws ValidatorException {

		
	}

}
