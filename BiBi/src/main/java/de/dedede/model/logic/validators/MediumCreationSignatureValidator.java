package de.dedede.model.logic.validators;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("mediumCreationSignatureValidator")
public class MediumCreationSignatureValidator implements Validator<String> {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, String arg2) throws ValidatorException {
		CopyDto copy = new CopyDto();
		copy.setSignature(arg2);
		if (MediumDao.signatureExists(copy)) {
			throw new ValidatorException(new FacesMessage("The signature " + copy.getSignature() + " is already in use"));
		}
	}

}
