package de.dedede.model.persistence.daos;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.logic.managedbeans.PaginatedList;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;

/**
 * This DAO (data access object) manages data pertaining to a category.
 * In the application, a category is defined to organize mediums. In particular, 
 * this DAO implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link CategoryDto} class for the used DTO.
 *
 */
public final class CategoryDao {

	private CategoryDao() {}

	/**
	 * Enters category data into the persistent data store. The enclosed ID must not
	 * be associated with an existing data entry in the data store.
	 * Otherwise, an exception is thrown.
	 *
	 * @param categoryDto A DTO container with the overwriting data
	 * @throws EntityInstanceNotUniqueException Is thrown if the passed ID is already
	 * 		associated with a data entry.
	 * @see CategoryDto
	 */
	public static void createCategory(CategoryDto categoryDto) 
			throws EntityInstanceNotUniqueException {
	}
	
	/**
	 * Fetches category data from the persistent data store. The enclosed ID must
	 * be associated with an existing data entry. Otherwise, an exception is thrown.
	 *
	 * @param id The ID of the category data to be fetched.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID is not
	 * 		associated with a data entry.
	 * @see CategoryDto
	 */
	public static CategoryDto readCategory(long id) 
			throws EntityInstanceDoesNotExistException {
		return null;
	}
	
	/**
	 * Fetches a paged list of categories from the persistent data store based on a
	 * search term for their names and pagination details. The order of the data and 
	 * concurrent read/write behavior in the underlying data store is unspecified.
	 *
	 * @param paginationDetails A container for the search term, page size, and page number.
	 * @return A paginated list of DTO containers with the category data that conforms
	 * 		to the specified search term and pagination details.
	 * @see CategoryDto
	 */
	public static PaginatedList readCategoriesByName(PaginationDto paginationDetails) {
		return null;
	}

	/**
	 * Overwrites existing category data in the persistent data store. The enclosed
	 * ID must be present in the data store and is used to remove existing data.
	 * Otherwise, an error is thrown.
	 *
	 * @param categoryDto A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't
	 * 		associated with any data entry.
	 * @see CategoryDto
	 */
	public static void updateCategory(CategoryDto categoryDto) 
			throws EntityInstanceDoesNotExistException {
	}

	/**
	 * Deletes existing category data from the persistent data store. The enclosed
	 * ID must be present in the data store and is used to identify and remove the 
	 * referenced data entry. Otherwise, an exception is thrown.
	 *
	 * @param id The ID of the category data entry to be deleted
	 * @return A DTO container with the deleted category data entry.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't
	 * 		associated with any data entry.
	 * @see CategoryDto
	 */
	public static CategoryDto deleteCategory(long id) {
		return null;
	}

}