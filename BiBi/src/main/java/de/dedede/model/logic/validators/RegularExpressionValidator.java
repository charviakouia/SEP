package de.dedede.model.logic.validators;

import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Uses @see java.util.regex.Pattern.compile() to check for valid regular 
 * expression syntax.
 * 
 * @author Jonas Picker
 */
public class RegularExpressionValidator implements Validator<String> {

	@Override
	public void validate(FacesContext context, UIComponent component, 
			String regEx) throws ValidatorException {
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
		try {
			Pattern.compile(regEx);
		} catch (PatternSyntaxException e) {
			String shortMessage = messages.getString("administration.invalid"
					+ "_regex-format_short");
			String longMessage = messages.getString("administration.invalid"
					+ "_regex_format_long");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					shortMessage, longMessage);
			throw new ValidatorException(msg);
		}
	}
	
}
