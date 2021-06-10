package de.dedede.model.logic.validators;

import java.util.ResourceBundle;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.logic.managed_beans.ReturnForm;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyIsNotAvailableException;
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
import de.dedede.model.persistence.exceptions.UserExceededDeadlineException;
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
@FacesValidator(value = "returnFormSignatureValidator", managed = true)
public class ReturnFormSignatureValidator implements Validator<String> {
	
	@Inject
	ReturnForm returnForm;
	
	@Override
	public void validate(FacesContext context, UIComponent component, String signature) throws ValidatorException {
		ResourceBundle messages = context.getApplication().evaluateExpressionGet(context, "#{msg}", ResourceBundle.class);
	    CopyDto signatureContainer = new CopyDto();
	    signatureContainer.setSignature(signature);
				
		if (returnForm.getUser().getEmailAddress() == null || returnForm.getUser().getEmailAddress().trim() == "" ) {
			String shortMessage = messages.getString("returnForm.enter_user_first_short");
	    	String longMessage = messages.getString("returnForm.enter_user_first_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    }
	    
	    try {
	    	MediumDao.validateReturnProcess(signatureContainer, returnForm.getUser());
	    } catch (CopyDoesNotExistException e1) {
	    	String shortMessage = messages.getString("returnForm.unknown_copy_short");
	    	String longMessage = messages.getString("returnForm.unknown_copy_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg, e1);
	    } catch (CopyIsNotAvailableException e2) {
	    	String shortMessage = messages.getString("returnForm.invalid_actor_return_short");
	    	String longMessage = messages.getString("returnForm.invalid_actor_return_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg, e2);
	    } catch (InvalidUserForCopyException e3) {
	    	String shortMessage = messages.getString("returnForm.invalid_deadline_return_short");
	    	String longMessage = messages.getString("returnForm.invalid_deadline_return_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg, e3);
	    } catch (UserExceededDeadlineException e4) {
	    	String shortMessage = messages.getString("returnForm.invalid_deadline_return_short");
	    	String longMessage = messages.getString("returnForm.invalid_deadline_return_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg, e4);
	    }
		
	}

}
