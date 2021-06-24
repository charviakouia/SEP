package de.dedede.model.logic.validators;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.util.ResourceBundle;

@FacesValidator("mediumCreationSignatureValidator")
public class MediumCreationSignatureValidator implements Validator<String> {

	@Override
	public void validate(FacesContext context, UIComponent arg1, String arg2) throws ValidatorException {
		CopyDto copy = new CopyDto();
		copy.setSignature(arg2);
		if (MediumDao.signatureExists(copy)) {
			Application app = context.getApplication();
			ResourceBundle messages = app.evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
			String str = messages.getString("mediumCreator.error.signatureInUse").formatted(copy.getSignature());
			throw new ValidatorException(new FacesMessage(str));
		}
	}

}
