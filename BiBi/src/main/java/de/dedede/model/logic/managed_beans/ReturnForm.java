package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.MessagingUtility;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyIsNotAvailableException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the return form. Staff or higher can process returns of lent 
 * copies here.
 * 
 * @author Jonas Picker 
 */
@Named
@ViewScoped
public class ReturnForm implements Serializable {

	@Serial
	private static  final long serialVersionUID = 1L;

	/**
	 * Recieves the email address of the user to make the return after value 
	 * change.
	 */
	private UserDto user;
	
	/**
	 * Prepares the same BB for a page reload with filled signature fields.
	 */
	@Inject
	private ReturnForm fillSignatures;

	/**
	 * Holds one copy signature for each input field.
	 */
	private List<CopyDto> copies;

	/**
	 * Initializes the bean with 4 signature input fields.
	 */
	@PostConstruct
	public void init() {
		if (user == null) {
			user = new UserDto();
		}
		if (copies == null || copies.isEmpty()) {
			copies = new ArrayList<CopyDto>();
			for(int i = 0; i < 4; i++) {
				copies.add(new CopyDto());
			}
		}	
	}
	
	/**
	 * Return the list of existing signatures lent by the existing user into
	 * the libraries inventory.
	 * 
	 * @throws BusinessException if unknown copy, user or invalid return action
	 */
	public void returnCopies() {
		int returned = 0;
		for(CopyDto copy : copies) {
			if (copy.getSignature() != null 
					&& copy.getSignature().trim() != "") {
				try {
					MediumDao.returnCopy(copy, user);
					returned++;
				} catch (CopyDoesNotExistException e) {                   
					String message = "An unexpected error occured during return"
							+ " process, the copy wasn't found.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (UserDoesNotExistException e) {
					String message = "An unexpected error occured during return"
							+ " process, the user wasn't found in the database"
							+ " or didn't lent this Copy.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (CopyIsNotAvailableException e) {
					String message = "An unexpected error occured during return"
							+ " process, the copy wasn't lent in the"
							+ " first place.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				}
			}
		}
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context,
				"#{msg}", ResourceBundle.class);
		if (returned == 0) {
			String shortMessage = messages.getString("returnForm.enter"
					+ "_signature_short");
			String longMessage = messages.getString("returnForm.enter_signature"
					+ "_long");
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, shortMessage, longMessage));
		} else {
			String shortContent = messages.getString("returnForm.copies_returne"
					+ "d_short");
			String longContent = messages.getString("returnForm.copies_returned"
					+ "_long");
			String emailAddress = user.getEmailAddress();
			String lentCopies = String.valueOf(returned);
			String shortMessage = insertParams(lentCopies, emailAddress, 
					shortContent);
			String longMessage = insertParams(lentCopies, emailAddress, 
					longContent);
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, shortMessage, longMessage));
			this.user.setEmailAddress("");
			this.copies.clear();
		}
	}
	
	/**
	 * Called by a listener for value change on email address input field, fills
	 * Signature inputs by reloading the page if the user exists and has lent 
	 * copies.
	 * 
	 * @param change The new email address input
	 */
	public void setUserEmail(ValueChangeEvent change) {
		String newValue = change.getNewValue().toString();
		user.setEmailAddress(newValue);
		FacesContext ctx = FacesContext.getCurrentInstance();
		NavigationHandler hdl = ctx.getApplication().getNavigationHandler();
		if (UserDao.userEntityWithEmailExists(user)) {
			List<CopyDto> lentCopies = 
					MediumDao.getLentCopiesByEmail(user);
			UserDto userDto = new UserDto();
			userDto.setEmailAddress(newValue);
			boolean notEmpty = !lentCopies.isEmpty();
			if (notEmpty) {
				this.fillSignatures.setUser(userDto);
				this.fillSignatures.setCopies(lentCopies);
				hdl.handleNavigation(ctx, null, null, null);
			}
		} else {
			MessagingUtility.writeNegativeMessageWithKey(ctx, false, 
					"lending.no_such_email");
		}
	}
		
	/**
	 * Add a signature input field.
	 */
	public void addSignatureInputField() {
		copies.add(new CopyDto());
	}
	
	/**
	 * Grants the facelet access to the user input container.
	 * 
	 * @return the input container.
	 */	
	public UserDto getUser() {
		
		return user;
	}

	/**
	 * Allows the facelet to modify the user input container in this bean.
	 * 
	 * @param user the userDto holding the data.
	 */
	public void setUser(UserDto user) {
		this.user = user;
	}

	/**
	 * Allows the corresponding facelet to access the variable number of 
	 * CopyDtos that mirror the signature input fields.
	 * 
	 * @return the list of CopyDtos.
	 */
	public List<CopyDto> getCopies() {
		
		return copies;
	}

	/**
	 * Allows the facelet to fill the copyDtos with user input data.
	 * 
	 * @param lentCopies the list of copyDtos that hold the data.
	 */
	public void setCopies(List<CopyDto> lentCopies) {
		this.copies = lentCopies;
	}
	
	private String insertParams(String param1, String param2, String content) {
		MessageFormat messageFormat = new MessageFormat(content);
		Object[] args = {param1, param2};
		String contentWithParam = messageFormat.format(args);
		
		return contentWithParam;
	}
	
}
