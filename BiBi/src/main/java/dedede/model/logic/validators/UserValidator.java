package dedede.model.logic.validators;

import dedede.model.data.dtos.UserDto;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

@Named
@FacesValidator
public class UserValidator implements Validator<String> {
	
	private UserDto userValidator;

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, String s) throws ValidatorException {

	}
}