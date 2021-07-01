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
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
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
 * Backing bean for the direct landing page. Here, library staff or higher can
 * lend copies to user.
 *
 * @author Jonas Picker
 */
@Named
@ViewScoped
public class Lending implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	/**
	 * Recieves the email address of the top input field on value change
	 */
	private UserDto user;
	
	/**
	 * Prepares the same BB for a reaload with filled signature fields.
	 */
	@Inject
	private Lending fillSignatures; 
	
	/**
	 * Reflects the input fields for signatures as an ArrayList
	 */
	private List<CopyDto> copies;

	/**
	 * Initializes the Backing Bean with 4 copy signature input fields.
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
	 * Lend the list of copies with existing signatures to the existing user.
	 * 
	 * @throws BuisnessException if unknown copy, user or invalid lending action
	 */
	public void lendCopies() {
		int lent = 0;
		for (CopyDto copy : copies) {
			if (copy.getSignature() != null 
					&& copy.getSignature().trim() != "") {
				try {
					MediumDao.lendCopy(copy, user);
					lent++;
				} catch (CopyDoesNotExistException e) {
					String message = "An unexpected error occured during " 
							+ "lending process, the copy didn't exist.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (UserDoesNotExistException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the user wasn't found in " 
							+ "the database.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (CopyIsNotAvailableException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the copy is not available " 
							+ "for lending.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				} catch (InvalidUserForCopyException e) {
					String message = "An unexpected error occured during "
							+ "lending process, the copy wasn't marked for " 
							+ "pickup by this user.";
					Logger.severe(message);
					throw new BusinessException(message, e);
				}
			}
		}
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context,
				"#{msg}", ResourceBundle.class);
		if (lent == 0) {
			String shortMessage = messages.getString("lending.enter_" 
					+ "signature_short");
			String longMessage = messages.getString("lending.enter_" 
					+ "signature_long");
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, shortMessage, longMessage));
		} else {
			String shortContent = messages.getString("lending.copies" 
					+ "_lent_short");
			String longContent = messages.getString("lending.copies_" 
					+ "lent_long");
			String emailAddress = user.getEmailAddress();
			String lentCopies = String.valueOf(lent);
			String shortMessage = insertParams(lentCopies, emailAddress,
					shortContent);
			String longMessage = insertParams(lentCopies, emailAddress,
					longContent);
			context.addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_INFO, shortMessage,
							longMessage));
			this.user.setEmailAddress("");
			this.copies.clear();
		}
	}

	/**
	 * Action for a listener on the user email input field. Fills the signature 
	 * fields with marked copies on page reload if the email matches any user.
	 * 
	 * @param change holds the new user Email that was put in.
	 */
	public void setUserEmail(ValueChangeEvent change) {
		String newValue = change.getNewValue().toString();
		user.setEmailAddress(newValue);
		FacesContext ctx = FacesContext.getCurrentInstance();
		NavigationHandler hdl = ctx.getApplication().getNavigationHandler();
		if (UserDao.userEntityWithEmailExists(user)) {
			List<CopyDto> markedCopies = 
					MediumDao.getMarkedCopiesByEmail(user);
			UserDto userDto = new UserDto();
			userDto.setEmailAddress(newValue);
			boolean notEmpty = !markedCopies.isEmpty();
			if (notEmpty) {
				this.fillSignatures.setUser(userDto);
				this.fillSignatures.setCopies(markedCopies);
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
	 * Allows the corresponding facelet to access the variable number of CopyDto
	 * that mirror the signature input fields.
	 * 
	 * @return the list of CopyDtos.
	 */
	public List<CopyDto> getCopies() {

		return copies;
	}

	public void setCopies(List<CopyDto> copies) {
		this.copies = copies;
	}

	private String insertParams(String param1, String param2, String content) {
		MessageFormat messageFormat = new MessageFormat(content);
		Object[] args = { param1, param2 };
		String contentWithParam = messageFormat.format(args);

		return contentWithParam;
	}

}
