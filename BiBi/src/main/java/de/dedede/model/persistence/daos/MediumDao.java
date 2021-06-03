package de.dedede.model.persistence.daos;

import java.util.Collection;
import java.util.List;

import de.dedede.model.data.dtos.AttributeDto;
import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyMediumUser;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.UserDto;

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
		//TODO: MS2 von Ivan
	}
	
	/**
	 * Fetches a medium from the persistent data store using an ID.
	 * The ID must be associated with an existing data entry.
	 * Otherwise, an exception is thrown.
	 * 
	 * @param mediumDto A DTO container with the ID of the medium data to be fetched
	 * @throws EntityInstanceDoesNotExistException Is thrown when the passed ID
	 * 		is not associated with any existing data entry.
	 * @return A DTO container with the medium data referenced by the ID.
	 */
	public static MediumDto readMedium(MediumDto mediumDto) 
			throws EntityInstanceDoesNotExistException {
		//TODO: MS1 von Sergej
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
	 * @return A list of DTO containers with the medium data.
	 * @see MediumDto
	 */
	public static List<MediumDto> readMediaBySearchCriteria(PaginationDto paginationDetails) {
		//TODO: MS2 von León
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
		//TODO: MS2 von Sergej
	}
	
	/**
	 * Deletes existing medium data in the persistent data store referenced by an id. 
	 * This ID must be present in the data store and is used to remove existing data.
	 * Otherwise, an exception is thrown
	 *
	 * @param mediumDto A DTO container with an ID that refers to the medium to be deleted.
	 * @return A DTO container with the deleted medium data.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't 
	 * 		associated with any data entry.
	 * @see MediumDto
	 */
	public static MediumDto deleteMedium(MediumDto mediumDto) 
			throws EntityInstanceDoesNotExistException {
		//TODO: MS2 von Sergej
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
		//TODO: MS1 von Sergej
	}
	
	/**
	 * Fetches a medium-copy, identified by its signature, from the persistent
	 * data store. The passed signature must be associated with an existing data entry 
	 * in the data store. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the medium-copy signature, which identifies 
	 * 		the specific medium-copy.
	 * @return A DTO container with the medium-copy data for the given signature.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed signature
	 * 		isn't associated with any data entry.
	 * @see CopyDto
	 */
	/*
	public static CopyDto readCopy(CopyDto copyDto) {
		return null;
	}
	*/
	
	/**
	 * Fetches a paginated list of all overdue medium-copies from the persistent 
	 * data store. The list contains overdue material from all users, globally.
	 *
	 * @param paginationDetails A container for the page size and number.
	 * @return A list of DTO containers with the medium-copy data.
	 * @see CopyDto
	 */
	public static List<CopyDto> readAllOverdueCopies(PaginationDto paginationDetails) {
		//TODO: MS3 von Ivan
		return null;
	}
	
	public static List<CopyDto> readMarkedCopiesByUser(PaginationDto paginationDetails){
		//TODO: MS3 von Sergej
		return null;
		
	}
	
	public static List<CopyDto> readLentCopiesByUser(PaginationDto paginationDetails){
		//TODO: MS3 von Sergej
		return null;
		
	}

	/*	/**
	 * Fetches a paginated list of all medium-copies belonging to a specific medium from the
	 * persistent data store.
	 *
	 * @param mediumDto A DTO container with the medium-ID, for which the copies are being 
	 * 		queried.
	 * @param paginationDetails A container for the page size and number.
	 * @return A list of DTO containers with the medium-copy data for the given medium-ID.
	 * @see CopyDto
	 */
	/*
	public static List<CopyDto> readAllCopiesFromMedium(MediumDto mediumDto,
			PaginationDto paginationDetails) {

			// Wir wollen doch keine paginierte Liste von Exemplaren haben!
			// -> Diese Methode ist unnötig.

		return null;
	}
 */

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
		//TODO: MS2 von Sergej
	}
	
	/**
	 * Deletes existing medium-copy data in the persistent data store. The enclosed
	 * signature must be associated with an existing entry and is used to identify 
	 * and remove data. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with a signature that identifies the medium-copy 
	 * 		to be deleted
	 * @return A DTO container with the deleted medium-copy data.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the signature isn't
	 * 		associated with any data entry.
	 * @see CopyDto
	 */
	public static CopyDto deleteCopy(CopyDto copyDto) {
		//TODO: MS2 von Sergej
		return null;
	}

	/**
	 * Registers that a specific medium-copy has been checked out by a specific
	 * user. Until it is returned, it is unavailable to other users.
	 *
	 * @param copyDto A DTO container with a signature that refers to the medium-copy.
	 * @param userDto A DTO container with an ID that refers to the user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the either
	 * 		the medium-copy is currently checked out or when either entity
	 * 		doesn't exist in the data store.
	 * @see CopyDto
	 */
	public static void lendCopy(CopyDto copyDto, UserDto userDto) 
			throws EntityInstanceDoesNotExistException {
		//TODO: MS2 von Jonas
	}

	/**
	 * Registers that a specific medium-copy has been returned by a specific user.
	 * It is then available to other users for check-out.
	 *
	 * @param copyDto A DTO container with a signature that refers to the medium-copy.
	 * @param userDto A DTO container with an ID that refers to the user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when either
	 * 		the medium-copy hasn't been checked out by the given user or
	 * 		either entity doesn't exist in the data store.
	 * @see CopyDto
	 */
	public static void returnCopy(CopyDto copyDto, UserDto userDto) 
			throws EntityInstanceDoesNotExistException {
		//TODO: MS3 von Jonas
	}
	
	public static void updateGlobalAttributes(Collection<AttributeDto> attributes) {
		//TODO: MS2 von Jonas
	}
	
	public static void readGlobalAttributes() {
		//TODO: MS2 von Ivan
	}
	
	public static void updateMediumAttributes(MediumDto mediumDto, 
			Collection<AttributeDto> attributes) {
		//TODO: MS2 von Sergej
	}
	
	public static List<CopyMediumUser> readGlobalPickupList(MediumSearchDto search) {
		//TODO: MS1 von León
		return null;
	}
	
	public static void updateMarkedCopies() {
		//TODO: MS3 von Jonas
	}
	
	public static List<CopyMediumUser> readDueDateReminders(){
		//TODO: MS3 von Jonas
		return null;
	}
}