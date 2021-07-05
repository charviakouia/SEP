package de.dedede.model.persistence.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.dedede.model.data.dtos.*;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;
import de.dedede.model.persistence.util.Pagination;

/**
 * This DAO (data access object) manages data pertaining to a category. In the
 * application, a category is defined to organize mediums. In particular, this
 * DAO implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link CategoryDto} class for the used DTO.
 *
 */
public final class CategoryDao {

	private static final long ACQUIRING_CONNECTION_PERIOD = 5000;

	private CategoryDao() {
	}

	/**
	 * Enters category data into the persistent data store. The enclosed ID must not
	 * be associated with an existing data entry in the data store. Otherwise, an
	 * exception is thrown.
	 *
	 * @param categoryDto A DTO container with the overwriting data
	 * @throws ParentCategoryDoesNotExistException Is thrown if the passed name
	 *                                             doesnt associated with a data
	 *                                             entry.
	 * @see CategoryDto
	 */

	public static void createCategory(CategoryDto categoryDto)
			throws ParentCategoryDoesNotExistException, EntityInstanceNotUniqueException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		if (categoryDto.getParent().getId() == 0) {
			try {
				if (categoryDto.getParent().getName().isEmpty()) {
					throw new ParentCategoryDoesNotExistException("Parent category name cannot be empty.");
				}
				if (signatureExists(conn, categoryDto)) {
					throw new EntityInstanceNotUniqueException("Category with that title already exists.");
				}
				int parentCategoryId = getCategoryIdByName(conn, categoryDto.getParent());
				categoryDto.getParent().setId(parentCategoryId);
			} catch (CategoryDoesNotExistException e) {
				try {
					conn.rollback();
					String msg = "Database error occurred while checking for category titles";
					Logger.severe(msg);
					throw new ParentCategoryDoesNotExistException("Specified name " + categoryDto.getParent().getName()
							+ " doesn't seem to match any parent category entry", e);
				} catch (SQLException exception) {
					final var message = "Failed to rollback database transaction";
					Logger.severe(message);
					throw new LostConnectionException(message);
				}
			}
		}
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO category (categoryid, title, description, parentcategoryid) " + "VALUES (?, ?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			populateCategoryStatement(createStmt, categoryDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0) {
				attemptToInsertGeneratedKey(categoryDto, createStmt);
			}
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
				String msg = "Database error occurred while creating category entity with id: " + categoryDto.getId();
				Logger.severe(msg);
				throw new LostConnectionException(msg, e);
			} catch (SQLException exception) {
				final var message = "Failed to rollback database transaction";
				Logger.severe(message);
				throw new LostConnectionException(message);
			}
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Fetches category data from the persistent data store. The enclosed ID must be
	 * associated with an existing data entry. Otherwise, an exception is thrown.
	 *
	 * @param category A DTO container with the ID of the category data to be
	 *                 fetched.
	 * @throws CategoryDoesNotExistException Is thrown if the passed ID is not
	 *                                       associated with a data entry.
	 * @see CategoryDto
	 */
	public static CategoryDto readCategory(CategoryDto category) {

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {
			final var statement = connection.prepareStatement("""
							select
								ct.title, ct.description, ct.parentcategoryid
							from
								category ct
							where
								ct.categoryid = ?
					""");
			statement.setInt(1, category.getId());

			final var resultSet = statement.executeQuery();
			final var result = new CategoryDto();
			final var rowExists = resultSet.next();

			if (!rowExists) {
				final var message = "Category with ID %d does not exist".formatted(category.getId());
				Logger.severe(message);
				throw new CategoryDoesNotExistException(message);
			}

			result.setId(category.getId());
			result.setName(resultSet.getString(1));
			result.setDescription(resultSet.getString(2));
			final var parentCategory = new CategoryDto();
			parentCategory.setId(resultSet.getInt(3));
			result.setParent(parentCategory);

			connection.commit();

			return result;

		} catch (SQLException exception) {

			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while reading category: " + exception.getMessage();
			Logger.severe(message);
			throw new LostConnectionException(message, exception);
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}

	}

	/**
	 * Fetches a paged list of categories from the persistent data store based on a
	 * search term for their names and pagination details. The order of the data and
	 * concurrent read/write behavior in the underlying data store is unspecified.
	 *
	 * @param pagination A container for the search term, page size, and page
	 *                   number.
	 * @return A list of DTO containers with the category data that conforms to the
	 *         specified search term and pagination details.
	 * @see CategoryDto
	 */
	public static List<CategoryDto> readCategoriesByName(CategorySearchDto categorySearch, PaginationDto pagination) {

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			final var statementBody = """
					from
						category ct
					where
						position(lower(?) in lower(ct.title)) > 0
					""";

			{
				final var countStatement = connection.prepareStatement("select count(ct.categoryid) " + statementBody);
				countStatement.setString(1, categorySearch.getSearchTerm());
				final var resultSet = countStatement.executeQuery();
				resultSet.next();

				Pagination.update(pagination, resultSet.getInt(1));
			}

			final var itemsStatement = connection.prepareStatement("""
							select
								ct.categoryid, ct.title, ct.description, ct.parentcategoryid
							%s
							order by
								title
									desc
							offset ?
							limit ?
					""".formatted(statementBody));
			var parameterIndex = 0;
			itemsStatement.setString(parameterIndex += 1, categorySearch.getSearchTerm());
			itemsStatement.setInt(parameterIndex += 1, Pagination.calculatePageOffset(pagination));
			itemsStatement.setInt(parameterIndex += 1, Pagination.getEntriesPerPage());

			final var resultSet = itemsStatement.executeQuery();
			final var results = new ArrayList<CategoryDto>();

			while (resultSet.next()) {
				final var category = new CategoryDto();
				category.setId(resultSet.getInt(1));
				category.setName(resultSet.getString(2));
				category.setDescription(resultSet.getString(3));

				final var parentCategory = new CategoryDto();
				parentCategory.setId(resultSet.getInt(4));
				category.setParent(parentCategory);
				results.add(category);
			}

			connection.commit();

			return results;

		} catch (SQLException exception) {

			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while searching for categories: " + exception.getMessage();
			Logger.severe(message);
			throw new LostConnectionException(message, exception);
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}

	/**
	 * Fetches all the categories as a forest of categories.
	 * 
	 * @return A list of all top-level/parentless categories.
	 */
	public static List<CategoryDto> readCategoryForest() {

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {
			final var statement = connection.prepareStatement("""
							with recursive categories(categoryid, title, description, parentcategoryid) as (
								select
									ct.categoryid, ct.title, ct.description, ct.parentcategoryid
								from
									category ct
								where
									ct.parentcategoryid is null
								union all
								select
									ct.categoryid, ct.title, ct.description, ct.parentcategoryid
								from
									category ct,
									categories pct
								where
									ct.parentcategoryid = pct.categoryid
							)
							select * from categories

					""");

			final var resultSet = statement.executeQuery();
			final var results = new ArrayList<CategoryDto>();
			final var categories = new HashMap<Integer, CategoryDto>();

			while (resultSet.next()) {
				final var category = new CategoryDto();
				category.setId(resultSet.getInt(1));
				category.setName(resultSet.getString(2));
				category.setDescription(resultSet.getString(3));

				categories.put(category.getId(), category);

				if (resultSet.getObject(4) != null) {
					// parent exists by now!
					final var parentCategory = categories.get(resultSet.getInt(4));

					category.setParent(parentCategory);
					parentCategory.getChildren().add(category);
				} else {
					// only put top-level/parentless categories directly into the list of results
					results.add(category);
				}
			}

			connection.commit();

			return results;

		} catch (SQLException exception) {

			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while reading categories: " + exception.getMessage();
			Logger.severe(message);
			throw new LostConnectionException(message, exception);
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}

	}

	/**
	 * Overwrites existing category data in the persistent data store. The enclosed
	 * ID must be present in the data store and is used to remove existing data.
	 * Otherwise, an error is thrown.
	 *
	 * @param categoryDto A DTO container with the overwriting data.
	 * @throws CategoryDoesNotExistException If the category ID does not exist.
	 * @see CategoryDto
	 */
	public static void updateCategory(CategoryDto category) {

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			final var statement = connection.prepareStatement("""
							update
								category
							set
								title = ?, description = ?, parentcategoryid = ?
							where
								categoryid = ?
					""");
			var parameterIndex = 0;
			statement.setString(parameterIndex += 1, category.getName());
			statement.setString(parameterIndex += 1, category.getDescription());
			statement.setObject(parameterIndex += 1,
					category.getParent() == null ? null : category.getParent().getId());
			statement.setInt(parameterIndex += 1, category.getId());

			final var affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				final var message = "Non-existent category with id %d passed to updateCategory()"
						.formatted(category.getId());
				Logger.severe(message);
				throw new CategoryDoesNotExistException(message);
			}

			connection.commit();

		} catch (SQLException exception) {

			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while reading categories: " + exception.getMessage();
			Logger.severe(message);
			throw new LostConnectionException(message, exception);
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}

	}

	/**
	 * Deletes existing category data from the persistent data store. The enclosed
	 * ID must be present in the data store and is used to identify and remove the
	 * referenced data entry. Otherwise, an exception is thrown.
	 *
	 * @param category A DTO container with the ID of the category data entry to be
	 *                 deleted
	 * @see CategoryDto
	 */
	public static void deleteCategory(CategoryDto category) {
		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		// Sergei Pravdin
		if (category.getId() == 0) {
			int categoryId = getCategoryIdByName(connection, category);
			category.setId(categoryId);
		}

		try {

			final var statement = connection.prepareStatement("""
					delete from
						category
					where
						categoryid = ?
					""");
			statement.setInt(1, category.getId());
			statement.executeUpdate();
			connection.commit();

		} catch (SQLException exception) {

			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while deleting category entity with id: " + category.getId();
			Logger.severe(message);
			throw new LostConnectionException(message, exception);
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}

	public static boolean categoryExists(CategoryDto categoryDto) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE " + "WHEN (SELECT COUNT(categoryId) FROM Category WHERE categoryId = ?) > 0 THEN true "
							+ "ELSE false " + "END AS entityExists;");
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
	public static int getCategoryIdByName(Connection conn, CategoryDto categoryDto)
			throws CategoryDoesNotExistException {
		try {
			PreparedStatement readCategoryId = conn
					.prepareStatement("SELECT categoryid" + " FROM category WHERE title = ?;");
			readCategoryId.setString(1, categoryDto.getName());
			ResultSet rs = readCategoryId.executeQuery();
			rs.next();
			conn.commit();
			return rs.getInt(1);
		} catch (SQLException e) {
			Logger.development("CategoryId couldn't be retrieved for this name.");
			throw new CategoryDoesNotExistException(
					"Specified name " + categoryDto.getName() + " doesn't seem to match any category entry", e);
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
	private static void attemptToInsertGeneratedKey(CategoryDto categoryDto, Statement stmt) throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			categoryDto.setId(resultSet.getInt(1));
		}
	}

	/** @author Sergei Pravdin */
	private static boolean signatureExists(Connection conn, CategoryDto categoryDto) {
		try {
			PreparedStatement checkStmt = conn.prepareStatement(
					"SELECT CASE " + "WHEN (SELECT COUNT(DISTINCT title) FROM Category WHERE title = ?) > 0 THEN true "
							+ "ELSE false " + "END AS entityExists;");
			checkStmt.setString(1, categoryDto.getName());
			ResultSet resultSet = checkStmt.executeQuery();
			resultSet.next();
			conn.commit();
			return resultSet.getBoolean(1);
		} catch (SQLException e) {
			try {
				conn.rollback();
				String msg = "Database error occurred while checking for category titles";
				Logger.severe(msg);
				throw new LostConnectionException(msg, e);
			} catch (SQLException exception) {
				final var message = "Failed to rollback database transaction";
				Logger.severe(message);
				throw new LostConnectionException(message);
			}
		}
	}
}
