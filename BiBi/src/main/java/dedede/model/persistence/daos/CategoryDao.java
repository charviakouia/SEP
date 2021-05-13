package dedede.model.persistence.daos;

import dedede.model.data.dtos.CategoryDto;
import dedede.model.logic.managedbeans.PaginatedList;
import dedede.model.logic.managedbeans.Page;

/**
 * This DAO (data access object) manages data pertaining to a category.
 * A category can be defined to categorize different mediums.
 * Specifically, this DAO implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link dedede.model.data.dtos.CategoryDto} class for the used DTO.
 * @author Meta Knight
 *
 */

public final class CategoryDao {

	private UserDao userService;
	private CategoryDao categoryDao;

	/**
	 * Enters category data into the persistent data store.
	 * The enclosed ID must not be associated with an existing data entry in the data store.
	 *
	 * @param categoryDto		A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.CategoryDto
	 */
	public static void createCategory(CategoryDto categoryDto) {}

	/**
	 * Overwrites existing category data in the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param categoryDto		A DTO container with the overwriting data
	 * @see						dedede.model.data.dtos.CategoryDto
	 */
    public static void updateCategory(CategoryDto categoryDto) {}

	/**
	 * Deletes existing category data from the persistent data store.
	 * The enclosed ID is used to remove existing data from the persistent data store.
	 *
	 * @param categoryDto		A DTO container with the ID of the entry to be deleted
	 * @see						dedede.model.data.dtos.CategoryDto
	 */
    public static void deleteCategory(CategoryDto categoryDto) {}

	// Wrong signature: By what criteria should a category be fetched?
    public static CategoryDto loadCategory() {
		return null;
	}

	/**
	 * Fetches a paged list of categories from the persistent data store.
	 * The order of the data and concurrent behaviour is unspecified.
	 *
	 * @param pageSize			The number of data elements being returned
	 * @param pageNumber		The page number in the conceptual data order, referring to the page size
	 * @return					A paginated list of DTO containers with the category data.
	 * @see						dedede.model.data.dtos.CategoryDto
	 */
    public static Page<CategoryDto> getAllCategories(int pageSize, int pageNumber){
		return null;
	}

	/**
	 * Fetches a paged list of categories from the persistent data store,
	 * for which a search filter is applied to the name first.
	 * The order of the data and concurrent behaviour is unspecified.
	 *
	 * @param text				The search term (case-insensitive), which the name must contain
	 * @param pageSize			The number of data elements being returned
	 * @param pageNumber		The page number in the conceptual data order, referring to the page size
	 * @return					A paginated list of DTO containers with the category data.
	 * @see						dedede.model.data.dtos.CategoryDto
	 */
    public static Page<CategoryDto> searchCategory(String text, int pageSize, int pageNumber){
		return null;
	}

}
