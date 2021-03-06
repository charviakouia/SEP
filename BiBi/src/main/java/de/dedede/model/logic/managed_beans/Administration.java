package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.daos.UserDao;
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
 * Backing bean for the administration facelet. Allows an administrator to
 * update the system settings. Further, they can search for any user inside of
 * the system from here. This Backing Bean for system settings too.
 * 
 * @author Jonas Picker
 */
@Named
@RequestScoped
public class Administration implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * The capsule for the db entries with an application scope.
	 */
	@Inject
	ApplicationCustomization appCustoms;
	
	/**
	 * The settings for the application capsuled in a Dto.
	 */
	private ApplicationDto application;
	
	/**
	 * Initializes the input capsule with the DB-held costumizations.
	 */
	@PostConstruct
	public void init() {
		ApplicationDto idContainer = new ApplicationDto();
		idContainer.setId(1);
		this.application = ApplicationDao.readCustomization(idContainer);
	}



	/**
	 * Save the changes made to the system settings in the DB and the 
	 * applicationscoped Bean, then reloads the page. Also displays the number of
	 * affected users from the changes in a message.
	 */
	public String save() { 
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ResourceBundle messages = app.evaluateExpressionGet(facesContext, 
				"#{msg}", ResourceBundle.class);
		try {
			ApplicationDao.updateCustomization(application);
			Logger.detailed("Application customizations saved in the database");
			ApplicationDto appCustomData = 
									appCustoms.getApplicationCustomization();
			RegisteredUserLendStatus newLendStatus = 
									application.getLendingStatus();
			RegisteredUserLendStatus oldLendStatus = 
									appCustomData.getLendingStatus();
			String longMsg = "";
			String shortMsg = "";
			if (newLendStatus == RegisteredUserLendStatus.MANUAL 
					&& oldLendStatus == RegisteredUserLendStatus.UNLOCKED) {
				int disabled = UserDao.setLendStatusOnRegisteredUsers(
						UserLendStatus.DISABLED);
				Logger.detailed(disabled + " registered user accounts have "
						+ "been excluded from lending.");
				shortMsg = disabled + " " 
						+ messages.getString("administration.users"
								+ "_disabled_short");
				longMsg = disabled + " " 
						+ messages.getString("administration.users"
								+ "_disabled_long");
			} else if (newLendStatus == RegisteredUserLendStatus.UNLOCKED 
					&& oldLendStatus == RegisteredUserLendStatus.MANUAL) {
				int enabled = UserDao.setLendStatusOnRegisteredUsers(
						UserLendStatus.ENABLED);
				Logger.detailed(enabled + " registered user accounts have "
						+ "been allowed to lend copies.");
				shortMsg = enabled + " " 
						+ messages.getString("administration.users"
								+ "_enabled_short");
				longMsg = enabled + " " 
						+ messages.getString("administration.users"
								+ "_enabled_long");
			}
			appCustoms.setApplicationCustomization(application);
			String shortSuccess = messages.getString("administration.update"
					+ "_success_short");
			String longSuccess = messages.getString("administration.update"
					+ "_success_long");
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					shortSuccess + shortMsg, longSuccess + longMsg);
			facesContext.addMessage(null, msg);
			
			return "";
		} catch (EntityInstanceDoesNotExistException e) {
			String message = "The application configuration entry couldn't be "
					+ "located in the database during save attempt.";
			Logger.severe(message);
			throw new BusinessException(message, e);
		}
	}
	
	/**
	 * Provides the facelet radio buttons with the enum values to be displayed.
	 * 
	 * @return the array of values of @see SystemRegistrationStatus.
	 */
	public SystemRegistrationStatus[] systemRegistrationStatusValues() {
		return SystemRegistrationStatus.values();
	} 

	/**
	 * Provides the facelet radio buttons with the enum values to be displayed.
	 * 
	 * @return the array of values of @see SystemAnonAccess.
	 */
	public SystemAnonAccess[] systemAnonAccessValues() {
		return SystemAnonAccess.values();
	}
	
	/**
	 * Provides the facelet radio buttons with the enum values to be displayed.
	 * 
	 * @return the array of values of @see RegisteredUserLendStatus.
	 */
	public RegisteredUserLendStatus[] registeredUserLendStatusValues() {
		return RegisteredUserLendStatus.values();
	}
	
	/**
	 * Allows reading access to the input capsule.
	 * 
	 * @return The Dto holding the settings.
	 */
	public ApplicationDto getApplication() {
		return application;
	}

	/**
	 * Allows modifications of the input capsule.
	 * 
	 * @param application the new settings held in a Dto.
	 */
	public void setApplication(ApplicationDto application) {
		this.application = application;
	}
	
}
