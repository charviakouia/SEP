package de.dedede.model.persistence.daos;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import de.dedede.model.data.dtos.*;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;

/**
 * This DAO (data access object) manages data pertaining to a category.
 * In the application, a category is defined to organize mediums. In particular, 
 * this DAO implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link CategoryDto} class for the used DTO.
 *
 */
public final class CategoryDao {
	
	private static final long ACQUIRING_CONNECTION_PERIOD = 5000;

	private CategoryDao() {}

	/**
	 * Enters category data into the persistent data store. The enclosed ID must not
	 * be associated with an existing data entry in the data store.
	 * Otherwise, an exception is thrown.
	 *
	 * @param categoryDto A DTO container with the overwriting data
	 * @throws ParentCategoryDoesNotExistException Is thrown if the passed name doesnt
	 *		associated with a data entry.
	 * @see CategoryDto
	 */
	public static void createCategory(CategoryDto categoryDto)
			throws ParentCategoryDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		if (categoryDto.getParent().getId() == 0) {
			try {
				if (categoryDto.getParent().getName().isEmpty()) {
					throw new ParentCategoryDoesNotExistException("Parent category name cannot be empty.");
				}
				int parentCategoryId = getCategoryIdByName(conn, categoryDto.getParent());
				categoryDto.getParent().setId(parentCategoryId);
			} catch (CategoryDoesNotExistException e) {
				throw new ParentCategoryDoesNotExistException("Specified name "
						+ categoryDto.getParent().getName()
						+ " doesn't seem to match any parent category entry", e);
			}
		}
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO category (categoryid, title, description, parentcategoryid) " +
							"VALUES (?, ?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			populateCategoryStatement(createStmt, categoryDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0) {
				attemptToInsertGeneratedKey(categoryDto, createStmt);
			}
			conn.commit();
		} catch (SQLException e) {
			String msg = "Database error occurred while creating category entity with id: " + categoryDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	/**
	 * Fetches category data from the persistent data store. The enclosed ID must
	 * be associated with an existing data entry. Otherwise, an exception is thrown.
	 *
	 * @param categoryDto A DTO container with the ID of the category data to be fetched.
	 * @throws CategoryDoesNotExistException Is thrown if the passed ID is not
	 * 		associated with a data entry.
	 * @see CategoryDto
	 */
	public static CategoryDto readCategory(CategoryDto categoryDto) 
			throws CategoryDoesNotExistException {
		return null;
	}
	
	/**
	 * Fetches a paged list of categories from the persistent data store based on a
	 * search term for their names and pagination details. The order of the data and 
	 * concurrent read/write behavior in the underlying data store is unspecified.
	 *
	 * @param paginationDetails A container for the search term, page size, and page number.
	 * @return A list of DTO containers with the category data that conforms
	 * 		to the specified search term and pagination details.
	 * @see CategoryDto
	 */
	public static List<CategoryDto> readCategoriesByName(CategorySearchDto categorySearchDto, PaginationDto paginationDetails) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT categoryId, title, description, parentCategoryId " +
					"FROM category " +
					"WHERE title LIKE ? " +
					"ORDER BY title DESC " +
					"LIMIT ? " +
					"OFFSET ?;"
			);
			stmt.setString(1, "%" + categorySearchDto.getSearchTerm() + "%");
			stmt.setInt(2, paginationDetails.getTotalAmountOfPages());
			stmt.setInt(3, paginationDetails.getPageNumber() * paginationDetails.getTotalAmountOfPages());
			ResultSet res = stmt.executeQuery();
			List<CategoryDto> list = new LinkedList<>();
			while (res.next()) {
				CategoryDto category = new CategoryDto();
				category.setId(Math.toIntExact(res.getLong(1)));
				category.setName(res.getString(2));
				category.setDescription(res.getString(3));
				CategoryDto parent = new CategoryDto();
				parent.setId(Math.toIntExact(res.getLong(4)));
				category.setParent(parent);
				list.add(category);
			}
			return list;
		} catch (SQLException e) {
			String msg = "Database error occurred while reading category entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
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
	 * @param categoryDto A DTO container with the ID of the category data entry to 
	 * 		be deleted
	 * @return A DTO container with the deleted category data entry.
	 * @throws CategoryDoesNotExistException Is thrown if the passed ID isn't
	 * 		associated with any data entry.
	 * @see CategoryDto
	 */
	public static CategoryDto deleteCategory(CategoryDto categoryDto) throws CategoryDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		if (categoryDto.getId() == 0) {
			int categoryId = getCategoryIdByName(conn, categoryDto);
			categoryDto.setId(categoryId);
		}
		try {
			deleteCategoryHelper(conn, categoryDto);
			return categoryDto;
		} catch (SQLException e) {
			String msg = "Database error occurred while deleting category entity with id: " + categoryDto.getId();
			// Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	public static boolean categoryExists(CategoryDto categoryDto) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE " +
							"WHEN (SELECT COUNT(categoryId) FROM Category WHERE categoryId = ?) > 0 THEN true " +
							"ELSE false " +
							"END AS entityExists;"
			);
			checkingStmt.setInt(1, categoryDto.getId());
			ResultSet resultSet = checkingStmt.executeQuery();
			resultSet.next();
			return resultSet.getBoolean(1);
		} catch (SQLException e) {
			String msg = "Database error occurred while reading category entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/** @author Sergei Pravdin */
	private static int getCategoryIdByName(Connection conn, CategoryDto categoryDto)
			throws CategoryDoesNotExistException {
		try {
			PreparedStatement readCategoryId = conn.prepareStatement("SELECT categoryid"
					+ " FROM category WHERE title = ?;");
			readCategoryId.setString(1, categoryDto.getName());
			ResultSet rs = readCategoryId.executeQuery();
			rs.next();
			conn.commit();
			return rs.getInt(1);
		} catch (SQLException e) {
			Logger.development("CategoryId couldn't be retrieved for this name.");
			throw new CategoryDoesNotExistException("Specified name " + categoryDto.getName()
					+ " doesn't seem to match any category entry", e);
		}
	}

	/** @author Sergei Pravdin */
	private static void populateCategoryStatement(PreparedStatement createStmt, CategoryDto categoryDto)
			throws SQLException {
		createStmt.setInt(1, categoryDto.getId());
		createStmt.setString(2, categoryDto.getName());
		createStmt.setString(3, categoryDto.getDescription());
		createStmt.setInt(4, categoryDto.getParent().getId());
	}

	/** @author Sergei Pravdin */
	private static void attemptToInsertGeneratedKey(CategoryDto categoryDto, Statement stmt)
			throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			categoryDto.setId(resultSet.getInt(1));
		}
	}

	/** @author Sergei Pravdin */
	private static void deleteCategoryHelper(Connection conn, CategoryDto categoryDto) throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM category " + "WHERE categoryid = ?;");
		deleteStmt.setInt(1, Math.toIntExact(categoryDto.getId()));
		deleteStmt.executeUpdate();
		conn.commit();
	}
}
