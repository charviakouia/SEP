package de.dedede.model.logic.managed_beans;

import java.util.ResourceBundle;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the contact information facelet. The contact page allows any
 * user to get to know how to contact the site owners.
 *
 */
@Named
@RequestScoped
public class Contact {
    
	/**
	 * The Applicationscoped object that holds the text to be displayed here.
	 */
	@Inject
	private ApplicationCustomization appData;
	
	/**
	 * Holds the users session data, used to check for admin.
	 */
	@Inject
	private UserSession userSession;
	
	/**
	 * Capsules the test field so nothing gets written directly into appData and
	 * it stays consistent with corresponding db entry.
	 */
	private ApplicationDto textContainer = new ApplicationDto();
	
	/**
	 * Initializes the text container with the contents in the applicationscoped
	 * appData bean.
	 */
	@PostConstruct
	public void init() {
		String text = appData.getApplicationCustomization().getContactInfo();
		textContainer.setContactInfo(text);
	}

	/**
	 * Persists the admins changes to the text field in the database and the
	 * applicationscoped bean with customizations.
	 */
	public void save() {
		ApplicationDto newData = appData.getApplicationCustomization();
		newData.setContactInfo(textContainer.getContactInfo());
		try {
			ApplicationDao.updateCustomization(newData);
		} catch (EntityInstanceDoesNotExistException e) {
			Logger.severe("An unexpected error occured during saving of"
					+ " new contact info, the db entry couldn't be found.");
		}
		Logger.detailed("Application customization has been updated");
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(context,
				"#{msg}", ResourceBundle.class);
		String shortMsg = messages.getString("administration.successful_text"
				+ "_update_short");
		String longMsg = messages.getString("administration.successful_text"
				+ "_update_long");
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				shortMsg, longMsg);
		context.addMessage(null, msg);
	}
	
	/**
	 * Decides, if the non-escaped text output is shown.
	 * 
	 * @return true if the user is not an admin.
	 */
	public boolean readOnly() {
		if (userSession != null && userSession.getUser() != null
				&& userSession.getUser().getRole() == UserRole.ADMIN) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Decides, if the text input field and submit button are to be shown.
	 * 
	 * @return true if the user is an admin.
	 */
	public boolean renderButton() {
		if (userSession != null && userSession.getUser() != null
				&& userSession.getUser().getRole() == UserRole.ADMIN) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Allows read access to the Dto that contains the text to be displayed.
	 * 
	 * @return the container for text.
	 */
	public ApplicationDto getTextContainer() {
		return textContainer;
	}

	/**
	 * Allows modifications of the Dto that contains the text to be displayed.
	 * 
	 * @param textContainer the new container with the new text.
	 */
	public void setTextContainer(ApplicationDto textContainer) {
		this.textContainer = textContainer;
	}

}
