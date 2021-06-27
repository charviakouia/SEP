package de.dedede.model.logic.validators;

import de.dedede.model.logic.util.MessagingUtility;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Checks if a new password fulfills the necessary security constraints. In particular, a password should have
 * a digit, a capital letter, and should be 6 characters or more.
 *
 * @author Ivan Charviakou
 */
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator<String> {

	private static final Pattern noForbiddenCharacters = Pattern.compile("^[^\\s\\\\]*$");
	private static final Pattern correctLength = Pattern.compile("^.{6,}$");
	private static final Pattern containsDigit = Pattern.compile("[0-9]");
	private static final Pattern containsCapitalLetter = Pattern.compile("[A-Z]");

	/**
	 * Checks if a new password fulfills the necessary security constraints. In particular, a password should have
	 * a digit, a capital letter, and should be 6 characters or more.
	 *
	 * @param context The FacesContext where this validator has been called.
	 * @param uiComponent The component on which this validator has been called.
	 * @param s The password string to be checked.
	 * @throws ValidatorException Is thrown if one or more of the password criteria are not fulfilled.
	 */
	@Override
	public void validate(FacesContext context, UIComponent uiComponent, String s) throws ValidatorException {
		StringJoiner sj = new StringJoiner(", ");
		boolean containsError = !noForbiddenCharacters(s, sj, context);
		containsError = !correctLength(s, sj, context) || containsError;
		containsError = !containsDigit(s, sj, context) || containsError;
		containsError = !containsCapitalLetter(s, sj, context) || containsError;
		if (containsError){
			FacesMessage message = new FacesMessage(sj.toString());
			throw new ValidatorException(message);
		}
	}

	private boolean noForbiddenCharacters(String s, StringJoiner sj, FacesContext context){
		boolean bNoForbiddenCharacters = noForbiddenCharacters.matcher(s).matches();
		if (!bNoForbiddenCharacters){
			sj.add(MessagingUtility.getMessage(context, "registration.noForbiddenCharacters"));
		}
		return bNoForbiddenCharacters;
	}

	private boolean correctLength(String s, StringJoiner sj, FacesContext context){
		boolean bCorrectLength = correctLength.matcher(s).matches();
		if (!bCorrectLength){
			sj.add(MessagingUtility.getMessage(context, "registration.correctLength"));
		}
		return bCorrectLength;
	}

	private boolean containsDigit(String s, StringJoiner sj, FacesContext context){
		boolean bContainsDigit = containsDigit.matcher(s).find();
		if (!bContainsDigit){
			sj.add(MessagingUtility.getMessage(context, "registration.containsDigit"));
		}
		return bContainsDigit;
	}

	private boolean containsCapitalLetter(String s, StringJoiner sj, FacesContext context){
		boolean bContainsCapitalLetter = containsCapitalLetter.matcher(s).find();
		if (!bContainsCapitalLetter){
			sj.add(MessagingUtility.getMessage(context, "registration.containsCapitalLetter"));
		}
		return bContainsCapitalLetter;
	}

}
