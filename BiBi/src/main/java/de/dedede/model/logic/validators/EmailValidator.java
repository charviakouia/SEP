package de.dedede.model.logic.validators;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Checks if the email address matches the specific domain.
 */
@FacesValidator("emailValidator")
public class EmailValidator implements Validator<String> {

	/**
	 * Check if the given email address belongs to the domain of the system. If it
	 * is in correct mail format, then the domain is checked. The exceptions of the
	 * store are handled with a message displayed to the user to try again. If the
	 * mail domain or the format of the mail is invalid a message is displayed.
	 *
	 * @param ctx   The FacesContext where this validator has been called.
	 * @param comp  The component where this validator has been called.
	 * @param email The String of the email address to be checked.
	 * @throws ValidatorException, if the address does not belong to this domain or
	 *                             is an incorrect mail format. Passes an
	 *                             appropriate message with the exception.
	 */
	public void validate(FacesContext ctx, UIComponent comp, String email) throws ValidatorException {

	}

}
