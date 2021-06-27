package de.dedede.model.logic.validators;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Ensures that a given signature assigned to a medium-copy is not already in use. The assigned signatures are
 * thus kept unique.
 *
 * @author Ivan Charviakou
 */
@FacesValidator("mediumCreationSignatureValidator")
public class MediumCreationSignatureValidator implements Validator<String> {

	/**
	 * Looks up the passed signature to ensure that it isn't currently in use in the datastore.
	 *
	 * @param context JSF's faces context.
	 * @param arg1 The UIComponent, on which the valdation is being performed
	 * @param arg2 The passed signature to be tested.
	 * @throws ValidatorException
	 */
	@Override
	public void validate(FacesContext context, UIComponent arg1, String arg2) throws ValidatorException {
		CopyDto copy = new CopyDto();
		copy.setSignature(arg2);
		if (MediumDao.signatureExists(copy)) {
			String str = MessagingUtility.getMessage(context, "mediumCreator.error.signatureInUse", arg2);
			throw new ValidatorException(new FacesMessage(str));
		}
	}

}
