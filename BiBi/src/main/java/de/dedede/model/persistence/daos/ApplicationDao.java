package de.dedede.model.persistence.daos;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;

/**
 * This DAO (data access object) is responsible for fetching global application
 * data from a persistent data store as well as saving to it.
 * It implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link ApplicationDto} class for the used DTO.
 *
 */
public final class ApplicationDao {

	private ApplicationDao() {}
	
	/**
	 * Writes new global application data to the persistent data store,
	 * as represented by an ApplicationDto object. The enclosed ID must not
	 * be in use. Otherwise, an exception is thrown.
	 *
	 * @param A DTO container with global application data to be written.
	 * @throws EntityInstanceNotUniqueException Is thrown if the supplied 
	 * 		ID is already in use in the data store.
	 * @see ApplicationDto
	 */
	public static void createCustomization(ApplicationDto appDTO) 
			throws EntityInstanceNotUniqueException {	
	}
	
	/**
	 * Fetches global application data from the persistent data store.
	 * The enclosed ID must be present in the data store. Otherwise, an
	 * exception is thrown.
	 *
	 * @param appDTO A DTO container with the ID of the global application 
	 * 		data to be read.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @return A DTO container with global application data identified
	 * 		by the passed ID.
	 * @see ApplicationDto
	 */
	public static ApplicationDto readCustomization(ApplicationDto appDTO) 
			throws EntityInstanceDoesNotExistException {
		return null;
	}

	/**
	 * Overwrites global application data in the persistent data store. 
	 * The enclosed ID must be present in the data store and is used to 
	 * identify and remove existing data. 
	 *
	 * @param applicationDto A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @see ApplicationDto
	 */
	public static void updateCustomization(ApplicationDto applicationDto) 
			throws EntityInstanceDoesNotExistException {
	}
	
	/**
	 * Deletes global application data from the persistent data store that
	 * corresponds to a given ID.
	 *
	 * @param appDTO A DTO container with the ID of the global application 
	 * 		data to be deleted.
	 * @return A DTO container with the deleted data store entry.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @see ApplicationDto
	 */
	public static ApplicationDto deleteCustomization(ApplicationDto appDTO) {
		return null;
	}

}
