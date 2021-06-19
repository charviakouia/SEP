package de.dedede.model.logic.managed_beans;

import java.io.Serial;
import java.io.Serializable;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * Holds the part of system configurations that are saved in the database in 
 * order to reduce query strain.
 *   
 * @author Jonas Picker
 */
@Named
@ApplicationScoped
public class ApplicationCustomization implements Serializable {
	
	@Serial
	private static  final long serialVersionUID = 1L;
	
	/**
	 * The id of the database entry used to hold the application configuration.
	 */
	private static final long appDataId = 1;
	
	/**
	 * The customizations in a Dto capsule.
	 */
	private ApplicationDto applicationCustomization;
	
	/**
	 * Fetches the application data from db after instanziation.
	 */
	@PostConstruct
	public void init() {
		ApplicationDto idDummy = new ApplicationDto();
		idDummy.setId(appDataId);
		applicationCustomization = ApplicationDao.readCustomization(idDummy);
	}

	/**
	 * Allows access to the customizations.
	 * 
	 * @return the customizations
	 */
	public ApplicationDto getApplicationCustomization() {
		return applicationCustomization;
	}
	
	/**
	 * Allows the customization to be modified from the outside.
	 * 
	 * @param app the new customizations.
	 */
	public void setApplicationCustomization(ApplicationDto app) {
		Logger.detailed("Application customization has been updated.");
		this.applicationCustomization = app;
	} 
	
}
