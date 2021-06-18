package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean for the administration facelet. Allows an administrator to
 * update the system settings. Further, they can search for any user inside of
 * the system from here. This Backing Bean for system settings too.
 *
 */
@Named
@RequestScoped //ApplicatioNScoped Bean um Einstellungen zu speichern?
public class Administration implements Serializable { //TrespassListener accessMode nicht vergessen

	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * The settings for the application capsuled in a Dto.
	 */
	private ApplicationDto application;
	
	@PostConstruct
	public void init() {
		ApplicationDto idContainer = new ApplicationDto();
		idContainer.setId(1);
		this.application = ApplicationDao.readCustomization(idContainer);
	}

	public ApplicationDto getApplication() {
		return application;
	}

	public void setApplication(ApplicationDto application) {
		this.application = application;
	}

	/**
	 * Save the changes made to the system settings.
	 */
	public void save() { //To-Do: Ausleihfreigabe umschalten => alle Nutzer geblocked?
		try {
			Logger.development(application.getName() + "|" + application.getPickupPeriod().toString() + "|" + application.getReturnPeriod().toString() + "|" + application.getWarningPeriod().toString() + "|" + application.getAnonRights() + "|" + application.getSystemRegistrationStatus() + "|" + application.getLendingStatus() + "|" + application.getEmailSuffixRegEx());
			ApplicationDao.updateCustomization(application);
		} catch (EntityInstanceDoesNotExistException e) {
			String message = "The application configuration entry couldn't be "
					+ "located in the database during save attempt.";
			Logger.severe(message);
			throw new BusinessException(message, e);
		}
	}

	/**
	 * Search for a user inside of the system.
	 */
	public String searchUser() {
		return "";
	}
	
	public SystemRegistrationStatus[] systemRegistrationStatusValues() {
		return SystemRegistrationStatus.values();
	} 
	
	public SystemAnonAccess[] systemAnonAccessValues() {
		return SystemAnonAccess.values();
	}
	
	public RegisteredUserLendStatus[] registeredUserLendStatusValues() {
		return RegisteredUserLendStatus.values();
	}
}
