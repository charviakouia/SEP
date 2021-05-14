package dedede.model.logic.validators;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * The ImageValidator for validating uploaded files.
 *
 */
@FacesValidator
public class LogoValidator implements Validator<String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, String s) throws ValidatorException {

	}
}
