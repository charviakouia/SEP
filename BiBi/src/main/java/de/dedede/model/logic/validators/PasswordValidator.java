package de.dedede.model.logic.validators;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Checks if a new password fulfills the security constraints to have a digit
 * and a capital letter. As well as the password length should be 6 characters
 * or more.
 */
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator<String> {

	private static final Pattern noForbiddenCharacters = Pattern.compile("^[^\\s\\\\]*$");
	private static final Pattern correctLength = Pattern.compile("^.{6,}$");
	private static final Pattern containsDigit = Pattern.compile("[0-9]");
	private static final Pattern containsCapitalLetter = Pattern.compile("[A-Z]");

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
		ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msg");
		StringJoiner sj = new StringJoiner(", ");
		boolean containsError = !noForbiddenCharacters(s, sj, bundle);
		containsError = !correctLength(s, sj, bundle) || containsError;
		containsError = !containsDigit(s, sj, bundle) || containsError;
		containsError = !containsCapitalLetter(s, sj, bundle) || containsError;
		if (containsError){
			FacesMessage message = new FacesMessage(sj.toString());
			throw new ValidatorException(message);
		}
	}

	private boolean noForbiddenCharacters(String s, StringJoiner sj, ResourceBundle bundle){
		boolean bNoForbiddenCharacters = noForbiddenCharacters.matcher(s).matches();
		if (!bNoForbiddenCharacters){
			sj.add(bundle.getString("registration.noForbiddenCharacters"));
		}
		return bNoForbiddenCharacters;
	}

	private boolean correctLength(String s, StringJoiner sj, ResourceBundle bundle){
		boolean bCorrectLength = correctLength.matcher(s).matches();
		if (!bCorrectLength){
			sj.add(bundle.getString("registration.correctLength"));
		}
		return bCorrectLength;
	}

	private boolean containsDigit(String s, StringJoiner sj, ResourceBundle bundle){
		boolean bContainsDigit = containsDigit.matcher(s).find();
		if (!bContainsDigit){
			sj.add(bundle.getString("registration.containsDigit"));
		}
		return bContainsDigit;
	}

	private boolean containsCapitalLetter(String s, StringJoiner sj, ResourceBundle bundle){
		boolean bContainsCapitalLetter = containsCapitalLetter.matcher(s).find();
		if (!bContainsCapitalLetter){
			sj.add("registration.containsCapitalLetter");
		}
		return bContainsCapitalLetter;
	}

}
