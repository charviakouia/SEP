package de.dedede.model.persistence.daos;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.logic.managedbeans.PaginatedList;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.PaginationDto;

/**
 * This DAO (data access object) manages data pertaining to a medium or a copy.
 * A medium describes general library entities, of which several copies may
 * exist. Specifically, this DAO implements the CRUD methods (create, read,
 * update, delete) for both media and medium-copies.
 *
 * See the {@link MediumDto} and {@link CopyDto} classes for the used DTOs.
 *
 */
public final class MediumDao {

	private MediumDao() {}

	/**
	 * Enters new medium data into the persistent data store.
	 * Any copies associated with this medium are not entered automatically. 
	 * The enclosed ID must not be associated with an existing data entry 
	 * in the data store. Otherwise, an exception is thrown.
	 *
	 * @param mediumDto A DTO container with the new medium data
	 * @throws EntityInstanceNotUniqueException Is thrown when the enclosed ID
	 * 		is already associated with an existing data entry.
	 * @see MediumDto
	 */
	public static void createMedium(MediumDto mediumDto) 
			throws EntityInstanceNotUniqueException {
	}
	
	/**
	 * Fetches a medium from the persistent data store using an ID.
	 * The ID must be associated with an existing data entry.
	 * Otherwise, an exception is thrown.
	 * 
	 * @param id
	 * @throws EntityInstanceDoesNotExistException Is thrown when the passed ID
	 * 		is not associated with any existing data entry.
	 * @return A DTO container with the medium data referenced by the ID.
	 */
	public static MediumDto readMedium(long id) 
			throws EntityInstanceDoesNotExistException {
		return null;
	}
	
	/**
	 * Fetches a paged list of media from the persistent data store, for which 
	 * search filters are applied to the name first. The order of 
	 * the data and concurrent read/write behavior in the underlying data store 
	 * is unspecified.
	 *
	 * @param paginationDetails A container for the search terms, page size, 
	 * 		and page number.
	 * @return A paginated list of DTO containers with the medium data.
	 * @see MediumDto
	 */
	public static PaginatedList readMediaBySearchCriteria(PaginationDto paginationDetails) {
		return null;
	}
	
	/**
	 * Overwrites existing medium data in the persistent data store. The enclosed ID
	 * must be present in the data store and is used to identify and remove existing data.
	 * Otherwise, an exception is thrown.
	 *
	 * @param mediumDto A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't
	 * 		associated with any data entry.
	 * @see MediumDto
	 */
	public static void updateMedium(MediumDto mediumDto) 
			throws EntityInstanceDoesNotExistException {
	}
	
	/**
	 * Deletes existing medium data in the persistent data store referenced by an id. 
	 * This ID must be present in the data store and is used to remove existing data.
	 * Otherwise, an exception is thrown
	 *
	 * @param id An ID that refers to the medium to be deleted.
	 * @return A DTO container with the deleted medium data.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't 
	 * 		associated with any data entry.
	 * @see MediumDto
	 */
	public static MediumDto deleteMedium(long id) 
			throws EntityInstanceDoesNotExistException {
		return null;
	}
	
	/**
	 * Enters medium-copy data associated with a particular medium into the persistent 
	 * data store. The enclosed ID must not be associated with an existing data entry 
	 * in the data store. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the overwriting data
	 * @throws EntityInstanceNotUniqueException Is thrown if the passed ID is already
	 * 		associated with a data entry.
	 * @see CopyDto
	 */
	public static void createCopy(CopyDto copyDto) 
			throws EntityInstanceNotUniqueException {
	}
	
	/**
	 * Fetches a medium-copy, identified by its signature, from the persistent
	 * data store. The passed signature must be associated with an existing data entry 
	 * in the data store. Otherwise, an exception is thrown.
	 *
	 * @param signature The medium-copy signature, which identifies the 
	 * 		specific medium-copy.
	 * @return A DTO container with the medium-copy data for the given signature.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed signature
	 * 		isn't associated with any data entry.
	 * @see CopyDto
	 */
	public static CopyDto readCopy(String signature) {
		return null;
	}
	
	/**
	 * Fetches a paginated list of all overdue medium-copies from the persistent 
	 * data store. The list contains overdue material from all users, globally.
	 *
	 * @param paginationDetails A container for the page size and number.
	 * @return A paginated list of DTO containers with the medium-copy data.
	 * @see CopyDto
	 */
	public static PaginatedList readAllOverdueCopies(PaginationDto paginationDetails) {
		return null;
	}
	
	/**
	 * Fetches a paginated list of all medium-copies belonging to a specific medium from the
	 * persistent data store.
	 *
	 * @param mediumId An identifier for a medium, for which the copies are being queried.
	 * @param paginationDetails A container for the page size and number.
	 * @return A list of DTO containers with the medium-copy data for the given medium-id.
	 * @see CopyDto
	 */
	public static PaginatedList readAllCopiesFromMedium(long mediumId, 
			PaginationDto paginationDetails) {
		return null;
	}

	/**
	 * Overwrites existing medium-copy data in the persistent data store. The
	 * enclosed signature must be associated with an existing entry and is used to 
	 * identify and remove data. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the enclosed
	 * 		signature isn't associated with any data entry.
	 * @see CopyDto
	 */
	public static void updateCopy(CopyDto copyDto) 
			throws EntityInstanceDoesNotExistException {
	}
	
	/**
	 * Deletes existing medium-copy data in the persistent data store. The enclosed
	 * signature must be associated with an existing entry and is used to identify 
	 * and remove data. Otherwise, an exception is thrown.
	 *
	 * @param signature A signature that identifies the medium-copy to be deleted
	 * @return A DTO container with the deleted medium-copy data.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the signature isn't
	 * 		associated with any data entry.
	 * @see CopyDto
	 */
	public static CopyDto deleteCopy(String signature) {
		return null;
	}

	/**
	 * Registers that a specific medium-copy has been checked out by a specific
	 * user. Until it is returned, it is unavailable to other users.
	 *
	 * @param copySignature A signature that refers to the medium-copy.
	 * @param userId An ID that refers to the user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the either
	 * 		the medium-copy is currently checked out or when either entity
	 * 		doesn't exist in the data store.
	 * @see CopyDto
	 */
	public static void lendCopy(String copySignature, long userId) 
			throws EntityInstanceDoesNotExistException {
	}

	/**
	 * Registers that a specific medium-copy has been returned by a specific user.
	 * It is then available to other users for check-out.
	 *
	 * @param copySignature A signature that refers to the medium-copy.
	 * @param userId An ID that refers to the user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when either
	 * 		the medium-copy hasn't been checked out by the given user or
	 * 		either entity doesn't exist in the data store.
	 * @see CopyDto
	 */
	public static void returnCopy(String copySignature, long userId) 
			throws EntityInstanceDoesNotExistException {
	}

}
