package de.dedede.model.logic.validators;

import java.util.ResourceBundle;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.managed_beans.ReturnForm;
import de.dedede.model.logic.util.CopyReturnStatus;
import de.dedede.model.persistence.daos.MediumDao;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Validator for the signature input fields on the return-form page. 
 * Also checks the user field in the same form because its value is needed here
 * anyway.
 * 
 * @author Jonas Picker
 */
@Named
@Dependent
@FacesValidator(value = "returnFormSignatureValidator", managed = true)
public class ReturnFormSignatureValidator implements Validator<String> {
	
	/**
	 * The Backing Bean of the corresponding Facelets this Validator is 
	 * registered on.
	 */
	@Inject
	ReturnForm returnForm;
	
	/**
	 * Checks on press of return button if: 
	 * a) the user field wasn't left empty
	 * b) the copies signature exists
	 * c) the copy is to be returned by this user
	 * in this order and adds a message if the deadline was exceeded, 
	 * @throws a validatorException if one condition isn't met
	 */
	@Override
	public void validate(FacesContext context, UIComponent component,
			String signature) throws ValidatorException {
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context, 
				"#{msg}", ResourceBundle.class);
	    CopyDto signatureContainer = new CopyDto();
	    signatureContainer.setSignature(signature);			
		UserDto user = returnForm.getUser();
		if (user.getEmailAddress() == null 
				|| user.getEmailAddress().trim() == "" ) {
			String shortMessage = messages.getString("returnForm.enter_user"
					+ "_first_short");
	    	String longMessage = messages.getString("returnForm.enter_user"
	    			+ "_first_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    }
		CopyReturnStatus status = 
				MediumDao.validateReturnProcess(signatureContainer, user);
	    if (status == CopyReturnStatus.SIGNATURE_NOT_FOUND) {
	    	String shortMessage = messages.getString("returnForm.unknown"
	    			+ "_copy_short");
	    	String longMessage = messages.getString("returnForm.unknown"
	    			+ "_copy_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (status == CopyReturnStatus.COPY_NOT_LENT) {
	    	String shortMessage = messages.getString("returnForm.invalid"
	    			+ "_copy_status_short");
	    	String longMessage = messages.getString("returnForm.invalid"
	    			+ "_copy_status_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (status == CopyReturnStatus.WRONG_USER) {
	    	String shortMessage = messages.getString("returnForm.invalid"
	    			+ "_actor_return_short");
	    	String longMessage = messages.getString("returnForm.invalid"
	    			+ "_actor_return_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (status == CopyReturnStatus.RETURN_DEADLINE_EXCEEDED) {							
	    	String shortMessage = messages.getString("returnForm.invalid"		
	    			+ "_deadline_return_short");							
	    	String longMessage = messages.getString("returnForm.invalid"
	    			+ "_deadline_return_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	context.addMessage(null, msg);
	    }
		
	}

}
