package dedede.model.persistence.daos;

import java.util.List;

import dedede.model.data.dtos.CopyDto;
import dedede.model.data.dtos.MediumDto;
import dedede.model.data.dtos.UserDto;

/**
 * This DAO (data access object) manages data pertaining to a medium or a copy.
 * A medium describes general library entities, of which several copies may exist.
 * Specifically, this DAO implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link dedede.model.data.dtos.MediumDto} class for the used DTO.
 * @author Mario
 *
 */
public final class MediumDao {

	private MediumDao() {
		throw new IllegalStateException();
	}
	
	/**
	 * Enters medium data into the persistent data store.
	 * The enclosed ID must not be associated with an existing data entry in the data store.
	 *
	 * @param mediumDto			A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.MediumDto
	 */
	public static void createMedium(MediumDto mediumDto) {}

	/**
	 * Overwrites existing medium data in the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param mediumDto			A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.MediumDto
	 */
	public static void updateMedium(MediumDto mediumDto) {}

	/**
	 * Deletes existing medium data in the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param mediumDto			A DTO container with the ID of the entry to be deleted
	 * @see						dedede.model.data.dtos.MediumDto
	 */
	public static void deleteMedium(MediumDto mediumDto) {}

	// Wrong signature: By what criteria should a category be fetched?
	public static MediumDto loadMedium() {
		return null;
	}

	/**
	 * Fetches a paged list of mediums from the persistent data store.
	 * The order of the data and concurrent behaviour is unspecified.
	 *
	 * @param pageSize			The number of data elements being returned
	 * @param pageNumber		The page number in the conceptual data order, referring to the page size
	 * @return					A paginated list of DTO containers with the category data.
	 * @see						dedede.model.data.dtos.MediumDto
	 */
	public static List<MediumDto> getAllMediums(int pageSize, int pageNumber){
		return null;
	}

	/**
	 * Fetches a paged list of mediums from the persistent data store,
	 * for which a search filter is applied to the name first.
	 * The order of the data and concurrent behaviour is unspecified.
	 *
	 * @param text				The search term (case-insensitive), which the name must contain
	 * @param pageSize			The number of data elements being returned
	 * @param pageNumber		The page number in the conceptual data order, referring to the page size
	 * @return					A paginated list of DTO containers with the category data.
	 * @see						dedede.model.data.dtos.MediumDto
	 */
	public static List<MediumDto> searchMediums(String text, int pageSize, int pageNumber){
		return null;
	}
	
	//Exemplare

	/**
	 * Overwrites existing medium copy data in the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param copyDto			A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static void updateCopy(CopyDto copyDto) {}

	/**
	 * Deletes existing medium-copy data in the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param copyDto			A DTO container with the ID of the entry to be deleted
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static void deleteCopy(CopyDto copyDto) {}

	/**
	 * Enters medium-copy data into the persistent data store.
	 * The enclosed ID must not be associated with an existing data entry in the data store.
	 *
	 * @param copyDto			A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static void createCopy(CopyDto copyDto) {}

	/**
	 * Fetches a medium-copy, identified from its signature, from the persistent data store.
	 *
	 * @param signatureLocation The medium-copy signature, which identifies the specific medium-copy.
	 * @return					A DTO container with the medium-copy data for the given signature.
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static CopyDto loadCopy(String signatureLocation) {
		return null;
	}

	/**
	 * Fetches a list of all overdue medium-copies from the persistent data store.
	 * The list contains overdue material from all users, globally.
	 *
	 * @return					A list of DTO containers with the medium-copy data.
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static List<CopyDto> getAllOverdueCopies(){
		return null;
	}

	/**
	 * Fetches a list of all medium-copies belonging to a specific medium from the persistent data store.
	 *
	 * @param mediumDto			A DTO container for a medium, carrying the relevant medium-ID.
	 * @return					A list of DTO containers with the medium-copy data.
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static List<CopyDto> getAllCopiesByMediums(MediumDto mediumDto){
		return null;
	}

	/**
	 * Registers that a specific medium-copy has been checked out by a specific user.
	 * Until it is returned, it is unavailable to other users.
	 *
	 * @param copyDto			A DTO container for a medium-copy, carrying the relevant copy-ID.
	 * @param userDto			A DTO container for a user, carrying the relevant user-ID.
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static void lendCopy(CopyDto copyDto, UserDto userDto) {}

	/**
	 * Registers that a specific medium-copy has been returned by a specific user.
	 * It is then available to other users for check-out.
	 *
	 * @param copyDto			A DTO container for a medium-copy, carrying the relevant copy-ID.
	 * @param userDto			A DTO container for a user, carrying the relevant user-ID.
	 * @see						dedede.model.data.dtos.CopyDto
	 */
	public static void returnCopy(CopyDto copyDto, UserDto userDto) {}

}
