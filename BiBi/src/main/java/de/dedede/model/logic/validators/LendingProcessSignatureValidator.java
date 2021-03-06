package de.dedede.model.logic.validators;


import java.util.ResourceBundle;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.managed_beans.Lending;
import de.dedede.model.logic.util.CopyLendingStatus;
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
 * Validator for the signature input fields on the lending page. 
 * Also checks the user field in the same form because its value is needed here
 * anyway.
 * 
 * @author Jonas Picker
 */
@Named
@Dependent
@FacesValidator(value = "lendingProcessSignatureValidator", managed = true)
public class LendingProcessSignatureValidator implements Validator<String> {
	
	/**
	 * The Backing Bean for the facelet this validator is registered on.
	 */
	@Inject
	Lending lending;

	/**
	 * Checks on press of lending button if: 
	 * a) the user field wasn't left empty
	 * b) the copies signature exists
	 * c) the copy wasn't already lent
	 * d) the copy wasn't marked by another user
	 * in this order and
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
	    UserDto user = lending.getUser();
		String backingBeanEmail = user.getEmailAddress();
		if (backingBeanEmail == null 
	    		|| backingBeanEmail.trim() == "" ) {
	    	String shortMessage = messages.getString("lending.enter_user"
	    			+ "_first_short");
	    	String longMessage = messages.getString("lending.enter_user"
	    			+ "_first_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    }
	    CopyLendingStatus status = 
	    		MediumDao.validateLendingProcess(signatureContainer, user);
	    if (status == CopyLendingStatus.SIGNATURE_NOT_FOUND) {
	    	String shortMessage = messages.getString("lending.unknown"
	    			+ "_copy_short");
	    	String longMessage = messages.getString("lending.unknown"
	    			+ "_copy_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    } else if (status == CopyLendingStatus.COPY_ALREADY_LENT) {
	    	String shortMessage = messages.getString("lending.already"
	    			+ "_lent_short");
	    	String longMessage = messages.getString("lending.already"
	    			+ "_lent_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
		} else if (status == CopyLendingStatus.WRONG_USER) {
	    	String shortMessage = messages.getString("lending.wrong"
	    			+ "_user_short");
	    	String longMessage = messages.getString("lending.wrong"
	    			+ "_user_long");
	    	FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                shortMessage, longMessage);
	    	throw new ValidatorException(msg);
	    }
	}
	
}
