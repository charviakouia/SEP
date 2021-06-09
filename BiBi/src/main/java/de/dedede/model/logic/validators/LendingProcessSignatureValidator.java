package de.dedede.model.logic.validators;


import java.util.ResourceBundle;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.logic.managed_beans.Lending;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@Dependent
@FacesValidator(value = "lendingProcessSignatureValidator", managed = true)
public class LendingProcessSignatureValidator implements Validator<String> {
	
	@Inject
	Lending lending;

	@Override
	public void validate(FacesContext context, UIComponent component, String signature) throws ValidatorException {
		ResourceBundle messages = context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
		String userEmail = lending.getUser().getEmailAddress();
		CopyDto signatureContainer = new CopyDto();
	    signatureContainer.setSignature(signature);
	    if (userEmail.trim() == "" || userEmail == null) {
	    	String shortMessage = messages.getString("lending.enter_user_first_short");
	    	String longMessage = messages.getString("lending.enter_user_first_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (!MediumDao.copySignatureExists(signatureContainer)) {
	    	String shortMessage = messages.getString("lending.unknown_copy_short");
	    	String longMessage = messages.getString("lending.unknown_copy_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (MediumDao.copyIsLentBySignature(signatureContainer)) {
	    	String shortMessage = messages.getString("lending.already_lend_short");
	    	String longMessage = messages.getString("lending.already_lend_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (MediumDao.invalidUserLendingAttempt(signatureContainer, lending.getUser())) {
	    	String shortMessage = messages.getString("lending.wrong_user_short");
	    	String longMessage = messages.getString("lending.wrong_user_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    }		
	}
	
}
