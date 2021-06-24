package de.dedede.model.persistence.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import de.dedede.model.persistence.exceptions.*;
import org.postgresql.util.PGInterval;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.MediumSearchCriterion;
import de.dedede.model.data.dtos.MediumSearchDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.SearchOperator;
import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;
import de.dedede.model.persistence.util.Pagination; 

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

	private static final long ACQUIRING_CONNECTION_PERIOD = 5000;
	private static final double DEFAULT_MAXIMUM_LEND_LIMIT_SECONDS = 15770000;
	private static final double DEFAULT_REMINDER_OFFSET_SECONDS = 3600;

	private MediumDao() {
	}
	
	public static void readCopyBySignature(CopyDto copy) throws EntityInstanceDoesNotExistException {
		if (!signatureExists(copy)) {
			throw new EntityInstanceDoesNotExistException("Copy entity with signature " + copy.getSignature() + " doesn't exist");
		}
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement readStmt = conn.prepareStatement(
					"SELECT copyid, mediumid, signature, bibposition, status, deadline, actor "
							+ "FROM Mediumcopy " + "WHERE signature = ?;"
			);
			readStmt.setString(1, copy.getSignature());
			ResultSet resultSet = readStmt.executeQuery();
			if (resultSet.next()) {
				populateCopyDto(copy, resultSet);
			}
			conn.commit();
		} catch (SQLException e) {
			try { conn.rollback(); } catch (SQLException ignore) {}
			String msg = "Database error occurred while reading copy entity with signature: " + copy.getSignature();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Enters new medium data into the persistent data store. Any copies associated
	 * with this medium are not entered automatically.
	 *
	 * @param mediumDto A DTO container with the new medium data
	 * @throws LostConnectionException Is thrown when the insertion operation
	 *                                 couldn't be completed due to a faulty
	 *                                 connection
	 * @see MediumDto
	 */
	public static void createMedium(MediumDto mediumDto) throws LostConnectionException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Medium (mediumLendPeriod, hasCategory, title, author1, author2, "
							+ "author3, author4, author5, mediumType, edition, publisher, releaseYear, "
							+ "isbn, mediumLink, demoText) VALUES "
							+ "(CAST(? AS INTERVAL), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			populateMediumStatement(createStmt, mediumDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0) {
				attemptToInsertGeneratedKey(mediumDto, createStmt);
			}
			conn.commit();
		} catch (SQLException e) {
			String msg = "Database error occurred while creating medium entity with id: " + mediumDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	public static boolean signatureExists(CopyDto copy) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement checkStmt = conn.prepareStatement("SELECT CASE "
					+ "WHEN (SELECT COUNT(DISTINCT signature) FROM MediumCopy WHERE signature = ?) > 0 THEN true "
					+ "ELSE false " + "END AS entityExists;");
			checkStmt.setString(1, copy.getSignature());
			ResultSet resultSet = checkStmt.executeQuery();
			resultSet.next();
			return resultSet.getBoolean(1);
		} catch (SQLException e) {
			String msg = "Database error occured while checking for copy signatures";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static void populateMediumStatement(PreparedStatement stmt, MediumDto mediumDto) throws SQLException {
		if (mediumDto.getReturnPeriod() == null){
			stmt.setObject(1, null);
		} else {
			stmt.setObject(1, toPGInterval(mediumDto.getReturnPeriod()));
		}
		if (mediumDto.getCategory() == null) {
			stmt.setObject(2, 1);
		} else {
			stmt.setInt(2, mediumDto.getCategory().getId());
		}
		stmt.setString(3, mediumDto.getTitle());
		stmt.setString(4, mediumDto.getAuthor1());
		stmt.setString(5, mediumDto.getAuthor2());
		stmt.setString(6, mediumDto.getAuthor3());
		stmt.setString(7, mediumDto.getAuthor4());
		stmt.setString(8, mediumDto.getAuthor5());
		stmt.setString(9, mediumDto.getMediumType());
		stmt.setString(10, mediumDto.getEdition());
		stmt.setString(11, mediumDto.getPublisher());
		stmt.setInt(12, mediumDto.getReleaseYear());
		stmt.setString(13, mediumDto.getIsbn());
		stmt.setString(14, mediumDto.getMediumLink());
		stmt.setString(15, mediumDto.getText());
	}

	private static void attemptToInsertGeneratedKey(MediumDto mediumDto, Statement stmt) throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			mediumDto.setId(Math.toIntExact(resultSet.getLong(1)));
		}
	}

	/**
	 * Fetches a medium from the persistent data store using an ID. The ID must be
	 * associated with an existing data entry. Otherwise, an exception is thrown.
	 * 
	 * @param mediumDto A DTO container with the ID of the medium data to be fetched
	 * @throws MediumDoesNotExistException Is thrown when the passed ID is not
	 *                                     associated with any existing data entry.
	 * @return A DTO container with the medium data referenced by the ID.
	 */
	public static MediumDto readMedium(MediumDto mediumDto)
			throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			return readMediumHelper(conn, mediumDto);
		} catch (SQLException e) {
			String msg = "Database error occurred while reading medium entity with id: " + mediumDto.getId();

			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Fetches a paged list of media from the persistent data store, for which
	 * search filters are applied to the name first. The order of the data and
	 * concurrent read/write behavior in the underlying data store is unspecified.
	 *
	 * @param mediumSearch A container for the search parameters.
	 * @param pagination   A container for the search terms, page size, and page
	 *                     number.
	 * @return A list of DTO containers with the medium data.
	 * @see MediumDto
	 */
	public static List<MediumDto> searchMedia(MediumSearchDto mediumSearch, PaginationDto pagination) {

		final var queryBody = new StringBuilder();
		final var parameters = new ArrayList<Object>();

		queryBody.append("""
				from
					medium m
						left join
					category ct
						on
					m.hascategory = ct.categoryid
						left join
					mediumcopy cp
						on
					m.mediumid = cp.mediumid
				where
					""");

		translateGeneralSearchTerm(queryBody, parameters, mediumSearch.getGeneralSearchTerm());

		for (final var nuancedSearchQuery : mediumSearch.getNuancedSearchQueries()) {
			translateNuancedSearchQuery(queryBody, parameters, nuancedSearchQuery);
		}

		final var itemsQuery = """
				select distinct
					m.mediumid, m.title, m.author1, m.author2, m.edition, m.publisher,
					ct.title
				%s
				offset ?
				limit ?
				""".formatted(queryBody);

		final var countQuery = "select count(distinct m.mediumid) " + queryBody;

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			{
				final var countStatement = connection.prepareStatement(countQuery);

				for (var index = 0; index < parameters.size(); index += 1) {
					countStatement.setObject(index + 1, parameters.get(index));
				}

				final var resultSet = countStatement.executeQuery();
				resultSet.next();

				Pagination.updatePagination(pagination, resultSet.getInt(1));
			}

			final var itemsStatement = connection.prepareStatement(itemsQuery);

			parameters.add(Pagination.pageOffset(pagination));
			parameters.add(Pagination.getEntriesPerPage());

			for (var index = 0; index < parameters.size(); index += 1) {
				itemsStatement.setObject(index + 1, parameters.get(index));
			}

			final var resultSet = itemsStatement.executeQuery();
			final var results = new ArrayList<MediumDto>();

			while (resultSet.next()) {

				final var medium = new MediumDto();

				medium.setId(resultSet.getInt(1));
				medium.setTitle(resultSet.getString(2));
				medium.setAuthor1(resultSet.getString(3));
				medium.setAuthor2(resultSet.getString(4));
				medium.setEdition(resultSet.getString(5));
				medium.setPublisher(resultSet.getString(6));

				final var category = new CategoryDto();

				category.setName(resultSet.getString(7));

				medium.setCategory(category);

				results.add(medium);
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

			final var message = "Database error occurred while searching for media: " + exception.getMessage();
			Logger.severe(message);
			throw new LostConnectionException(message, exception);

		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}

	// @Task needs a lotta love!!
	private static void translateGeneralSearchTerm(StringBuilder query, List<Object> parameters,
			String generalSearchTerm) {

		if (generalSearchTerm == null || generalSearchTerm.trim().isEmpty()) {
			query.append("true");
			return;
		}

		query.append("(false ");

		// @Question only search a subset of these?
		for (final var searchCriterion : MediumSearchCriterion.values()) {
			if (!searchCriterion.isGeneralSearchCriterion()) {
				continue;
			}

			final var nuancedSearchQuery = new MediumSearchDto.NuancedSearchQuery();
			nuancedSearchQuery.setOperator(SearchOperator.OR);
			nuancedSearchQuery.setCriterion(searchCriterion);
			nuancedSearchQuery.setTerm(generalSearchTerm);

			translateNuancedSearchQuery(query, parameters, nuancedSearchQuery);
		}

		query.append(") ");
	}

	private static void translateNuancedSearchQuery(StringBuilder query, List<Object> parameters,
			MediumSearchDto.NuancedSearchQuery nuancedQuery) {

		if (nuancedQuery.getTerm() == null || nuancedQuery.getTerm().trim().isEmpty()) {
			return;
		}

		query.append(" ");

		query.append(switch (nuancedQuery.getOperator()) {
		case AND -> "and";
		case OR -> "or";
		case AND_NOT -> "and not";
		});

		query.append(" (");

		query.append(switch (nuancedQuery.getCriterion()) {
		case AUTHORS -> {
			for (var index = 0; index < 5; index += 1) {
				parameters.add(nuancedQuery.getTerm());
			}

			yield """
					(  position(lower(?) in lower(m.author1)) > 0
					or position(lower(?) in lower(m.author2)) > 0
					or position(lower(?) in lower(m.author3)) > 0
					or position(lower(?) in lower(m.author4)) > 0
					or position(lower(?) in lower(m.author5)) > 0
					)
					""";
		}
		case YEAR_OF_RELEASE -> {
			// cannot use try/catch inside here because of an
			// internal compiler error (eclipse codegen)
			final var year = parseInt(nuancedQuery.getTerm());

			if (year.isPresent()) {
				parameters.add(year.get());
				yield "m.releaseyear = ?";
			} else {
				yield "false";
			}
		}
		case CATEGORY -> {
			parameters.add(nuancedQuery.getTerm());

			yield "position(lower(?) in lower(ct.title)) > 0";
		}
		case SIGNATURE -> {
			parameters.add(nuancedQuery.getTerm());

			yield "position(lower(?) in lower(cp.signature)) > 0";
		}
		default -> {
			final var column = switch (nuancedQuery.getCriterion()) {
			case TITLE -> "title";
			case TYPE -> "mediumtype";
			case EDITION -> "edition";
			case PUBLISHER -> "publisher";
			case ISBN -> "isbn";
			case URL -> "mediumlink";
			case SUMMARY -> "demotext";
			default -> throw new IllegalStateException();
			};

			parameters.add(nuancedQuery.getTerm());

			yield "position(lower(?) in lower(m.%s)) > 0".formatted(column);
		}
		});

		query.append(") ");
	}

	// existence of this function necessitated by an internal compiler error
	// (eclipse codegen)
	private static Optional<Integer> parseInt(String source) {
		try {
			return Optional.of(Integer.parseInt(source));
		} catch (NumberFormatException exception) {
			return Optional.empty();
		}
	}
	
	// @Task docs
	public static List<MediumDto> readMediaGivenCategory(CategoryDto category, PaginationDto pagination) {
		
		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			final var statementBody = """
					from
						medium m
					where
						m.hascategory = ?
					""";

			{
				final var countStatement = connection.prepareStatement("select count(m.mediumid) " + statementBody);
				countStatement.setInt(1, category.getId());
				final var resultSet = countStatement.executeQuery();
				resultSet.next();
				
				Pagination.updatePagination(pagination, resultSet.getInt(1));
			}

			final var itemsStatement = connection.prepareStatement("""
					select
						m.mediumid, m.title, m.author1, m.author2, m.edition, m.publisher
					%s
					offset ?
					limit ?
					""".formatted(statementBody));
			var parameterIndex = 0;
			itemsStatement.setInt(parameterIndex += 1, category.getId());
			// @Task sorting
			itemsStatement.setInt(parameterIndex += 1, Pagination.pageOffset(pagination));
			itemsStatement.setInt(parameterIndex += 1, Pagination.getEntriesPerPage());

			final var resultSet = itemsStatement.executeQuery();
			final var results = new ArrayList<MediumDto>();

			while (resultSet.next()) {
				final var medium = new MediumDto();
				medium.setId(resultSet.getInt(1));
				medium.setTitle(resultSet.getString(2));
				medium.setAuthor1(resultSet.getString(3));
				medium.setAuthor2(resultSet.getString(4));
				medium.setEdition(resultSet.getString(5));
				medium.setPublisher(resultSet.getString(6));
				results.add(medium);
			}

			connection.commit();

			return results;
		} catch (SQLException exeption) {
			
			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while reading media given a category: "
					+ exeption.getMessage();
			Logger.severe(message);

			throw new LostConnectionException(message, exeption);

		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}

	/**
	 * Overwrites existing medium data in the persistent data store. The enclosed ID
	 * must be present in the data store and is used to identify and remove existing
	 * data. Otherwise, an exception is thrown.
	 *
	 * @param mediumDto A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't
	 *                                             associated with any data entry.
	 * @see MediumDto
	 */
	public static void updateMedium(MediumDto mediumDto) throws EntityInstanceDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement updateStmt = conn.prepareStatement("UPDATE Medium "
					+ "SET mediumlendperiod = ?, hascategory = ?, title = ?, author1 = ?, "
					+ "author2 = ?, author3 = ?, author4 = ?, author5 = ?, mediumtype = ?, edition = ?, "
					+ "publisher = ?, releaseyear = ?, isbn = ?, mediumlink = ?, demotext = ?"
					+ "WHERE mediumid = ?;");
			populateMediumStatement(updateStmt, mediumDto);
			updateStmt.setLong(16, mediumDto.getId());
			int numAffectedRows = updateStmt.executeUpdate();
			conn.commit();
			if (numAffectedRows == 0) {
				conn.rollback();
				String msg = String.format("No entity with the id: %d exists", mediumDto.getId());
				Logger.severe(msg);
				throw new EntityInstanceDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while updating user entity with id: " + mediumDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Deletes existing medium data in the persistent data store referenced by an
	 * id. This ID must be present in the data store and is used to remove existing
	 * data. Otherwise, an exception is thrown
	 *
	 * @param mediumDto A DTO container with an ID that refers to the medium to be
	 *                  deleted.
	 * @return A DTO container with the deleted medium data.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed ID isn't
	 *                                             associated with any data entry.
	 * @see MediumDto
	 */
	public static MediumDto deleteMedium(MediumDto mediumDto)
			throws MediumDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (mediumEntityExists(conn, mediumDto)) {
				deleteMediumHelper(conn, mediumDto);
				conn.commit();
				return mediumDto;
			} else {
				String msg = String.format("No entity with the id: %d exists", mediumDto.getId());
				// Logger.severe(msg);
				throw new MediumDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while deleting medium entity with id: " + mediumDto.getId();
			// Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Enters medium-copy data associated with a particular medium into the
	 * persistent data store. The enclosed ID must not be associated with an
	 * existing data entry in the data store. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the overwriting data
	 * @throws EntityInstanceNotUniqueException Is thrown if the passed ID is
	 *                                          already associated with a data
	 *                                          entry.
	 * @see CopyDto
	 */

	public static void createCopy(CopyDto copyDto, MediumDto mediumDto)
			throws LostConnectionException, MaxConnectionsException, EntityInstanceNotUniqueException {
		if (copyExists(copyDto)) {
			throw new EntityInstanceNotUniqueException("Signature already being used");
		}
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Mediumcopy (mediumid, signature, bibposition, status, "
							+ "deadline, actor) VALUES " + "(?, ?, ?, CAST (? AS copyStatus), ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			copyDto.setMediumId(mediumDto.getId());
			populateCopyStatement(createStmt, copyDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0) {
				attemptToInsertGeneratedKey(copyDto, createStmt);
			}
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
				String msg = "Database error occurred while creating mediumCopy entity with id: " + copyDto.getId();
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
	 * Fetches a medium-copy, identified by its signature, from the persistent data
	 * store. The passed signature must be associated with an existing data entry in
	 * the data store. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the medium-copy signature, which
	 *                identifies the specific medium-copy.
	 * @return A DTO container with the medium-copy data for the given signature.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the passed signature
	 *                                             isn't associated with any data
	 *                                             entry.
	 * @see CopyDto
	 */
	/*
	 * public static CopyDto readCopy(CopyDto copyDto) { return null; }
	 */

	/**
	 * Fetches a paginated list of all overdue medium-copies from the persistent
	 * data store. The list contains overdue material from all users, globally.
	 *
	 * @param paginationDetails A container for the page size and number.
	 * @return A list of DTO containers with the medium-copy data.
	 * @see CopyDto
	 */
	public static List<MediumCopyUserDto> readAllOverdueCopies(PaginationDto paginationDetails) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT COALESCE(u.userlendperiod, m.mediumlendperiod, a.globallendlimit) AS lendPeriod, " +
					"u.userid, u.emailaddress, u.passwordhashsalt, u.passwordhash, u.name, u.surname, u.postalcode, u.city, u.street, " +
					"u.housenumber, u.token, u.tokencreation, u.userlendperiod, u.lendstatus, u.verificationstatus, u.userrole, " +
					"m.mediumid, m.mediumlendperiod, m.hascategory, m.title, m.author1, m.author2, m.author3, m.author4, m.author5, " +
					"m.mediumtype, m.edition, m.publisher, m.releaseyear, m.isbn, m.mediumlink, m.demotext, " +
					"c.copyid, c.mediumid, c.signature, c.bibposition, c.status, c.deadline, c.actor " +
					"FROM users AS u " +
					"JOIN mediumcopy AS c ON u.userid = c.actor " +
					"JOIN medium AS m ON c.mediumid = m.mediumid " +
					"CROSS JOIN (SELECT * FROM application ORDER BY one ASC LIMIT 1) AS a " +
					"WHERE c.deadline < CURRENT_TIMESTAMP " +
					"AND c.status = 'BORROWED' " +
					"ORDER BY u.userid, m.mediumid, c.copyid DESC " +
					"LIMIT ? " +
					"OFFSET ?;"
			);
			stmt.setInt(1, paginationDetails.getTotalAmountOfRows());
			stmt.setInt(2, paginationDetails.getPageNumber() * paginationDetails.getTotalAmountOfRows());
			ResultSet resultSet = stmt.executeQuery();
			List<MediumCopyUserDto> result = new LinkedList<>();
			while (resultSet.next()) {
				MediumCopyUserDto dto = new MediumCopyUserDto();
				dto.setCopy(new CopyDto());
				dto.setMedium(new MediumDto());
				dto.setUser(new UserDto());
				dto.getUser().setToken(new TokenDto());
				populateMCUDto(resultSet, dto);
				result.add(dto);
			}
			conn.commit();
			return result;
		} catch (SQLException e){
			try { conn.rollback(); } catch (SQLException ignore) {}
			String msg = "Database error occurred while reading mediumCopyUser entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	public static int readNumberOfAllOverdueCopies() {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT COUNT(*) FROM (" +
							"SELECT *" +
							"FROM users AS u " +
							"JOIN mediumcopy AS c ON u.userid = c.actor " +
							"JOIN medium AS m ON c.mediumid = m.mediumid " +
							"CROSS JOIN (SELECT * FROM application ORDER BY one ASC LIMIT 1) AS a " +
							"WHERE c.deadline < CURRENT_TIMESTAMP " +
							"AND c.status = 'BORROWED' " +
							"ORDER BY u.userid, m.mediumid, c.copyid DESC" +
							") AS countingTable;"
			);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()){
				int result = resultSet.getInt(1);
				conn.commit();
				return result;
			} else {
				try { conn.rollback(); } catch (SQLException ignore) {}
				String msg = "SQL query returned unexpected result, single int required";
				Logger.severe(msg);
				throw new InvalidConfigurationException(msg);
			}
		} catch (SQLException e){
			try { conn.rollback(); } catch (SQLException ignore) {}
			String msg = "Database error occurred while reading mediumCopyUser entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	private static void populateMCUDto(ResultSet resultSet, MediumCopyUserDto dto) throws SQLException {
		dto.setLendingDuration(getDuration((PGInterval) resultSet.getObject(1)));
		UserDto userDto = dto.getUser();
		userDto.setId(resultSet.getInt(2));
		userDto.setEmailAddress(resultSet.getString(3));
		userDto.setPasswordSalt(resultSet.getString(4));
		userDto.setPasswordHash(resultSet.getString(5));
		userDto.setFirstName(resultSet.getString(6));
		userDto.setLastName(resultSet.getString(7));
		userDto.setZipCode(resultSet.getInt(8));
		userDto.setCity(resultSet.getString(9));
		userDto.setStreet(resultSet.getString(10));
		userDto.setStreetNumber(resultSet.getString(11));
		TokenDto tokenDto = userDto.getToken();
		tokenDto.setContent(resultSet.getString(12));
		Timestamp tokenCreationTime = resultSet.getTimestamp(13);
		tokenDto.setCreationTime((tokenCreationTime == null ? null : tokenCreationTime.toLocalDateTime()));
		userDto.setLendingPeriod(getDuration((PGInterval) resultSet.getObject(14)));
		String userLendStatusStr = resultSet.getString(15);
		userDto.setUserLendStatus((userLendStatusStr == null ? null : UserLendStatus.valueOf(userLendStatusStr)));
		String userVerificationStatusStr = resultSet.getString(16);
		userDto.setUserVerificationStatus((userVerificationStatusStr == null ? null : UserVerificationStatus.valueOf(userVerificationStatusStr)));
		String userRoleStr = resultSet.getString(17);
		userDto.setUserRole((userRoleStr == null ? null : UserRole.valueOf(userRoleStr)));
		MediumDto mediumDto = dto.getMedium();
		mediumDto.setId(resultSet.getInt(18));
		mediumDto.setReturnPeriod(getDuration((PGInterval) resultSet.getObject(19)));
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(resultSet.getInt(20));
		mediumDto.setCategory(categoryDto);
		mediumDto.setTitle(resultSet.getString(21));
		mediumDto.setAuthor1(resultSet.getString(22));
		mediumDto.setAuthor2(resultSet.getString(23));
		mediumDto.setAuthor3(resultSet.getString(24));
		mediumDto.setAuthor4(resultSet.getString(25));
		mediumDto.setAuthor5(resultSet.getString(26));
		mediumDto.setMediumType(resultSet.getString(27));
		mediumDto.setEdition(resultSet.getString(28));
		mediumDto.setPublisher(resultSet.getString(29));
		mediumDto.setReleaseYear(resultSet.getInt(30));
		mediumDto.setIsbn(resultSet.getString(31));
		mediumDto.setMediumLink(resultSet.getString(32));
		mediumDto.setText(resultSet.getString(33));
		CopyDto copyDto = dto.getCopy();
		copyDto.setId(resultSet.getInt(34));
		copyDto.setSignature(resultSet.getString(36));
		copyDto.setLocation(resultSet.getString(37));
		String copyStatusStr = resultSet.getString(38);
		copyDto.setCopyStatus((copyStatusStr == null ? null : CopyStatus.valueOf(copyStatusStr)));
		copyDto.setDeadline(resultSet.getTimestamp(39));
		copyDto.setActor(resultSet.getInt(40));
	}
	
	private static Duration getDuration(PGInterval interval) {
		if (interval == null) {
			return null;
		} else {
			double seconds = interval.getSeconds() + 
					interval.getMinutes() * 60 + 
					interval.getHours() * 60 * 60 + 
					interval.getDays() * 24 * 60 * 60 +
					interval.getMonths() * 24 * 60 * 60 * 30 +
					interval.getYears() * 24 * 60 * 60 * 30 * 12;
			return Duration.ofSeconds(Math.round(seconds));
		}
	}

	public static List<MediumCopyUserDto> readMarkedCopiesByUser(PaginationDto paginationDetails, UserDto userDto) {
		List<MediumCopyUserDto> result = MediumDao.readCopiesReadyForPickup(paginationDetails);
		result.removeIf(p -> p.getUser().getId() != userDto.getId());
		return result;
	}

	public static List<MediumCopyUserDto> readLentCopiesByUser(PaginationDto paginationDetails, UserDto userDto) {
		List<MediumCopyUserDto> result = MediumDao.readLentCopies(paginationDetails);
		result.removeIf(p -> p.getUser().getId() != userDto.getId());
		return result;
	}

	/**
	 * Overwrites existing medium-copy data in the persistent data store. The
	 * enclosed signature must be associated with an existing entry and is used to
	 * identify and remove data. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the overwriting data
	 * @throws CopyDoesNotExistException Is thrown if the enclosed signature isn't
	 *                                   associated with any data entry.
	 * @see CopyDto
	 */
	public static void updateCopy(CopyDto copyDto)
			throws CopyDoesNotExistException, LostConnectionException, MaxConnectionsException,
			CopyIsNotAvailableException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		if (copyIsLentBySignature(conn, copyDto)) {
			try {
				conn.rollback();
				Logger.severe("Copy is not available: " + copyDto.getId());
				throw new CopyIsNotAvailableException("Copy is not available.");
			} catch (SQLException exception) {
				final var message = "Failed to rollback database transaction";
				Logger.severe(message);
				throw new LostConnectionException(message);
			}
		}
		try {
			PreparedStatement updateStmt = conn.prepareStatement(
					"UPDATE Mediumcopy " + "SET mediumid = ?, signature = ?, bibposition = ?,"
							+ " status = CAST(? AS copyStatus), deadline = ?, actor = ? WHERE copyid = ? ");
			populateCopyStatement(updateStmt, copyDto);
			updateStmt.setInt(7, copyDto.getId());
			int numAffectedRows = updateStmt.executeUpdate();
			conn.commit();
			if (numAffectedRows == 0) {
				String msg = String.format("No entity with the id: %d exists", copyDto.getId());
				Logger.severe(msg);
				throw new CopyDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while updating application entity with id: " + copyDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Deletes existing medium-copy data in the persistent data store. The enclosed
	 * signature must be associated with an existing entry and is used to identify
	 * and remove data. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with a signature that identifies the
	 *                medium-copy to be deleted
	 * @return A DTO container with the deleted medium-copy data.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the signature isn't
	 *                                             associated with any data entry.
	 * @see CopyDto
	 */

	public static CopyDto deleteCopy(CopyDto copyDto)
			throws CopyDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (copyEntityExists(conn, copyDto)) {
				deleteCopyHelper(conn, copyDto);
				conn.commit();
				return copyDto;
			} else {
				String msg = String.format("No entity with the id: %d exists", copyDto.getId());
				// Logger.severe(msg);
				throw new CopyDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while deleting medium entity with id: " + copyDto.getId();
			// Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Read all copies from the database that are ready to be picked up.
	 * 
	 * @param pagination A container defining the page size and the amount of pages.
	 * @return The list of copies ready for pickup and some related data.
	 */
	public static List<MediumCopyUserDto> readCopiesReadyForPickup(PaginationDto pagination) {

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			final var statementBody = """
					from
						medium m,
						mediumcopy c,
						users u
					where
						c.status = 'READY_FOR_PICKUP'
							and
						c.mediumid = m.mediumid
							and
						c.actor = u.userid
					""";

			{
				final var countStatement = connection.prepareStatement("select count(c.copyid) " + statementBody);

				final var resultSet = countStatement.executeQuery();
				resultSet.next();
				
				Pagination.updatePagination(pagination, resultSet.getInt(1));
			}

			final var itemsStatement = connection.prepareStatement("""
					select
						m.mediumid, m.title,
						c.signature, c.bibposition, c.deadline,
						u.userid, u.emailaddress, u.name, u.surname
					%s
					offset ?
					limit ?
					""".formatted(statementBody));
			// @Task sorting
			itemsStatement.setInt(1, Pagination.pageOffset(pagination));
			itemsStatement.setInt(2, Pagination.getEntriesPerPage());

			final var resultSet = itemsStatement.executeQuery();
			final var results = new ArrayList<MediumCopyUserDto>();

			while (resultSet.next()) {

				final var compound = new MediumCopyUserDto();

				final var medium = new MediumDto();
				medium.setId(resultSet.getInt(1));
				medium.setTitle(resultSet.getString(2));
				compound.setMedium(medium);

				final var copy = new CopyDto();
				copy.setSignature(resultSet.getString(3));
				copy.setLocation(resultSet.getString(4));
				copy.setDeadline(resultSet.getTimestamp(5));
				compound.setCopy(copy);

				final var user = new UserDto();
				user.setId(resultSet.getInt(6));
				user.setEmailAddress(resultSet.getString(7));
				user.setFirstName(resultSet.getString(8));
				user.setLastName(resultSet.getString(9));
				compound.setUser(user);

				results.add(compound);
			}

			connection.commit();

			return results;
		} catch (SQLException exeption) {
			
			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				final var message = "Failed to rollback database transaction: " + rollbackException.getMessage();
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while reading copies ready for pickup: "
					+ exeption.getMessage();
			Logger.severe(message);

			throw new LostConnectionException(message, exeption);

		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}

	public static boolean mediumExists(MediumDto mediumDto) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			return mediumEntityExists(conn, mediumDto);
		} catch (SQLException e) {
			String msg = "Database error occurred while reading medium entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	// Helper methods:

	private static boolean mediumEntityExists(Connection conn, MediumDto mediumDto) throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(mediumid) FROM Medium WHERE mediumid = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setInt(1, mediumDto.getId());
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	public static boolean copyExists(CopyDto copyDto) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			return copyEntityExistsBySignature(conn, copyDto);
		} catch (SQLException e) {
			String msg = "Database error occurred while reading copy entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static boolean copyEntityExistsBySignature(Connection conn, CopyDto copyDto) throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(signature) FROM MediumCopy WHERE signature = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setString(1, copyDto.getSignature());
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static boolean copyEntityExists(Connection conn, CopyDto copyDto) throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(copyid) FROM MediumCopy WHERE copyid = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setInt(1, copyDto.getId());
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static MediumDto readMediumHelper(Connection conn, MediumDto mediumDto)
			throws SQLException, LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
		PreparedStatement readStmt = conn.prepareStatement(
				"SELECT mediumid, mediumlendperiod, hascategory, title, author1, author2, author3, author4,"
						+ " author5, mediumtype, edition, publisher, releaseyear, isbn, mediumlink, demotext "
						+ "FROM Medium " + "WHERE mediumid = ?;");
		readStmt.setInt(1, Math.toIntExact(mediumDto.getId()));
		ResultSet resultSet = readStmt.executeQuery();
		if (resultSet.next()) {
			populateMediumDto(resultSet, mediumDto);
			setDurationHelper(conn, mediumDto);
			conn.commit();
			return mediumDto;
		} else {
			conn.commit();
			String msg = "A medium does not exist with id: " + mediumDto.getId();
			throw new MediumDoesNotExistException(msg);
		}
	}

	private static void setDurationHelper(Connection conn, MediumDto mediumDto) throws SQLException {
		PreparedStatement getMediumLimit = conn.prepareStatement(
				"SELECT EXTRACT (EPOCH FROM (SELECT mediumLendPeriod FROM medium WHERE mediumId = ?));");
		getMediumLimit.setInt(1, Math.toIntExact(mediumDto.getId()));
		ResultSet rs = getMediumLimit.executeQuery();
		rs.next();
		double mediumSeconds = rs.getDouble(1);
		getMediumLimit.close();
		Duration duration = Duration.ofSeconds((long) (mediumSeconds));
		mediumDto.setReturnPeriod(duration);
	}

	private static void populateMediumDto(ResultSet resultSet, MediumDto mediumDto)
			throws SQLException, LostConnectionException, MaxConnectionsException {
//		setCategoryHelper(resultSet, mediumDto);
		mediumDto.setTitle(resultSet.getString(4));
		mediumDto.setAuthor1(resultSet.getString(5));
		mediumDto.setAuthor2(resultSet.getString(6));
		mediumDto.setAuthor3(resultSet.getString(7));
		mediumDto.setAuthor4(resultSet.getString(8));
		mediumDto.setAuthor5(resultSet.getString(9));
		mediumDto.setMediumType(resultSet.getString(10));
		mediumDto.setEdition(resultSet.getString(11));
		mediumDto.setPublisher(resultSet.getString(12));
		mediumDto.setReleaseYear(resultSet.getInt(13));
		mediumDto.setIsbn(resultSet.getString(14));
		mediumDto.setMediumLink(resultSet.getString(15));
		mediumDto.setText(resultSet.getString(16));
		readCopiesHelper(mediumDto);
	}

	/*
	 * private static void setCategoryHelper(ResultSet resultSet, MediumDto
	 * mediumDto) throws SQLException, CategoryDoesNotExistException { if
	 * (resultSet.getInt(3) != 0) {
	 * mediumDto.getCategory().setId(resultSet.getInt(3)); try {
	 * mediumDto.setCategory(CategoryDao.readCategory(mediumDto.getCategory())); }
	 * catch (CategoryDoesNotExistException e) { // ignore } } else {
	 * mediumDto.getCategory().setId(1);
	 * mediumDto.setCategory(CategoryDao.readCategory(mediumDto.getCategory())); } }
	 */

	private static void readCopiesHelper(MediumDto mediumDto)
			throws SQLException, LostConnectionException, MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		PreparedStatement readStmt = conn
				.prepareStatement("SELECT copyid, mediumid, signature, bibposition, status, deadline, actor "
						+ "FROM Mediumcopy " + "WHERE mediumid = ?;");
		readStmt.setInt(1, Math.toIntExact(mediumDto.getId()));
		ResultSet resultSet = readStmt.executeQuery();
		conn.commit();
		while (resultSet.next()) {
			CopyDto copyDto = new CopyDto();
			populateCopyDto(copyDto, resultSet);
			mediumDto.addCopy(copyDto.getId(), copyDto);
		}
		ConnectionPool.getInstance().releaseConnection(conn);
	}

	private static void populateCopyDto(CopyDto copyDto, ResultSet resultSet) throws SQLException {
		copyDto.setId(resultSet.getInt(1));
		copyDto.setMediumId(resultSet.getInt(2));
		copyDto.setSignature(resultSet.getString(3));
		copyDto.setLocation(resultSet.getString(4));
		CopyStatus copyStatus = CopyStatus.valueOf(resultSet.getString(5));
		copyDto.setCopyStatus(copyStatus);
		copyDto.setDeadline(resultSet.getTimestamp(6));
		copyDto.setActor(resultSet.getInt(7));
		if (copyDto.getActor() == 0) {
			copyDto.setActor(null);
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static PGInterval toPGInterval(Duration duration) {
		PGInterval result = new PGInterval();
		result.setSeconds(duration.getSeconds());
		return result;
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void populateCopyStatement(PreparedStatement stmt, CopyDto copyDto) throws SQLException {
		// stmt.setInt(1, copyDto.getId());
		stmt.setInt(1, copyDto.getMediumId());
		stmt.setString(2, copyDto.getSignature());
		stmt.setString(3, copyDto.getLocation());
		stmt.setString(4, copyDto.getCopyStatus().name());
		stmt.setTimestamp(5, copyDto.getDeadline());
		if (copyDto.getActor() != null && copyDto.getActor() != 0) {
			stmt.setInt(6, copyDto.getActor());
		} else {
			stmt.setObject(6, null);
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void attemptToInsertGeneratedKey(CopyDto copyDto, Statement stmt) throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			copyDto.setId(resultSet.getInt(1));
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void deleteMediumHelper(Connection conn, MediumDto mediumDto) throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM Medium " + "WHERE mediumid = ?;");
		deleteStmt.setInt(1, Math.toIntExact(mediumDto.getId()));
		deleteStmt.executeUpdate();
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void deleteCopyHelper(Connection conn, CopyDto copyDto) throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM MediumCopy " + "WHERE copyid = ?;");
		deleteStmt.setInt(1, Math.toIntExact(copyDto.getId()));
		deleteStmt.executeUpdate();
		conn.commit();
	}
	
	public static void deleteCopyBySignature(CopyDto copyDto)
			throws LostConnectionException, MaxConnectionsException, EntityInstanceDoesNotExistException {
		if (!copyExists(copyDto)) {
			throw new EntityInstanceDoesNotExistException("Copy doesn't exist");
		}
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"DELETE FROM Mediumcopy WHERE signature = ?;"
			);
			stmt.setString(1, copyDto.getSignature());
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
				String msg = "Database error occurred while deleting copy entity with id: " + copyDto.getId();
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
	
	public static void deleteCopiesByMediumId(MediumDto mediumDto)
			throws LostConnectionException, MaxConnectionsException, EntityInstanceDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"DELETE FROM Mediumcopy WHERE mediumid = ?;"
			);
			stmt.setInt(1, mediumDto.getId());
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
				String msg = "Database error occurred while deleting copy entities with meidum-id: " + mediumDto.getId();
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
	
	public static void returnOverdueCopies()
			throws LostConnectionException, MaxConnectionsException, EntityInstanceDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE Mediumcopy SET status = 'AVAILABLE', deadline = null, actor = null WHERE deadline < CURRENT_TIMESTAMP;"
			);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
				String msg = "Database error occurred while deleting copy entities";
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
	 * Checks if the copy was lent and inverts the result.
	 *
	 * @param conn               the connection being used for the operation
	 * @param signatureContainer the copy's signature in a Dto
	 * @return true if the return attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	public static boolean invalidCopyStatusReturnAttempt(Connection conn, 
			CopyDto signatureContainer) {
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(signature) FROM mediumCopy"
					+ " WHERE ((signature = ?) AND (status = CAST('BORROWED'"
					+ " AS copyStatus)))) > 0 THEN false ELSE true END" 
					+ " AS invalidCopyStatus;");
			checkingStmt.setString(1, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured while with db communication"
					+ " checking for invalid actor on copy return.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		}
	}

	/**
	 * Checks if the copy was lent by the (existing) user who wants to return it 
	 * and inverts the result.
	 *
	 * @param conn               the connection being used for the operation
	 * @param signatureContainer the copy's signature in a Dto
	 * @param userEmail          the mail address of the user in a dto
	 * @return true if the return attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	private static boolean invalidActorReturnAttempt(Connection conn,
			CopyDto signatureContainer, UserDto userEmail) {
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			int userId = UserDao.getUserIdByEmail(conn, userEmail);
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(actor) FROM mediumCopy"
							+ " WHERE ((actor = ?) AND (signature = ?) "
							+ "AND (status = "
							+ "CAST('BORROWED' AS copyStatus)))) > 0 THEN false" 
							+ " ELSE true END AS invalidActor;");
			checkingStmt.setInt(1, userId);
			checkingStmt.setString(2, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while"
					+ " checking for invalid actor on copy return.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} catch (UserDoesNotExistException e) { 
			String errorMessage = "User wasn't found in DB during check for" 
					+ " invalid actor on copy return attempt.";
			Logger.severe(errorMessage);
			
			return true;
		}
	}

	/**
	 * Checks if the copy was lent by the (existing) user who wants to return
	 *  it, also checks if the deadline was met and inverts the result.
	 * 
	 * @param conn               the connection being used for the operation
	 * @param userEmail          the mail address of the user in a dto
	 * @param signatureContainer the DTO that contains the signature
	 * @return true if the return attempt is invalid
	 * 
	 * @author Jonas Picker
	 */

	private static boolean invalidDeadlineReturnAttempt(Connection conn, 
			CopyDto signatureContainer,
			UserDto userEmail) {
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			int userId = UserDao.getUserIdByEmail(conn, userEmail);
			PreparedStatement checkingStmt = conn
					.prepareStatement("SELECT CASE WHEN (SELECT COUNT(actor) "
							+ "FROM mediumCopy"
							+ " WHERE ((actor = ?) AND (signature = ?)"
							+ " AND (status = "
							+ "CAST('BORROWED' AS copyStatus)) AND (deadline >="
							+ " CURRENT_TIMESTAMP))) > 0 THEN false ELSE true" 
							+ " END AS invalidDeadline;");
			checkingStmt.setInt(1, userId);
			checkingStmt.setString(2, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while" 
					+ " checking for valid copy return.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} catch (UserDoesNotExistException e) { 
			String errorMessage = "User wasn't foud in DB during check" 
					+ " for invalid copy return attempt.";
			Logger.severe(errorMessage);
			
			return true;
		}
	}

	/**
	 * Checks for the existance of a copy by its signature, modelled after
	 * copyEntityExists() by @author Sergei Pravdin.
	 *
	 * @param conn               the copy's signature in a Dto
	 * @param signatureContainer copy DTO that contains the signature
	 * @return true if the copy exists
	 * 
	 * @author Jonas Picker
	 */
	private static boolean copySignatureExists(Connection conn,
			CopyDto signatureContainer) {
		String signature = signatureContainer.getSignature();
		boolean result = false;
		try {
			PreparedStatement checkingStmt = conn
					.prepareStatement(
							"SELECT CASE WHEN (SELECT COUNT(signature)"
							+ " FROM MediumCopy"
							+ " WHERE (signature = ?)) > 0 "
							+ "THEN true ELSE false END " 
							+ "AS entityExists;");
			checkingStmt.setString(1, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication"
					+ " while checking for Copy Signature existence.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		}
	}

	/**
	 * Checks if a copy is already lent.
	 * 
	 * @param signatureContainer the copy's signature in a Dto
	 * @return true if the lending attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	private static boolean copyIsLentBySignature(Connection conn, 
			CopyDto signatureContainer) {
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(signature) FROM"
					+ " mediumCopy WHERE ((signature = ?) AND (status ="
					+ " CAST('BORROWED' AS copyStatus)))) > 0 THEN true" 
					+ " ELSE false END AS invalidLendingStatus;");
			checkingStmt.setString(1, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication"
					+ " while checking for copy status on lending attempt.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		}
	}

	/**
	 * Checks if a lending attempt for a given copy signature and (existing) 
	 * user is valid and inverts the result.
	 * 
	 * @param signatureContainer the copy's signature in a Dto
	 * @param userEmail          the user Email in a Dto
	 * @return true if the lending attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	private static boolean invalidUserLendingAttempt(Connection conn, 
			CopyDto signatureContainer, UserDto userEmail) {
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			int userId = UserDao.getUserIdByEmail(conn, userEmail);
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(signature) FROM "
					+ "mediumCopy WHERE ((signature = ?) AND (((actor = ?)"
					+ " AND (status = CAST('READY_FOR_PICKUP' AS copyStatus)))"
					+ " OR (status = CAST('AVAILABLE' AS copyStatus))))) > 0"
					+ " THEN false ELSE true END AS invalidUserLending;");
			checkingStmt.setString(1, signature);
			checkingStmt.setInt(2, userId);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while "
					+ "checking for invalid user for copy on lending attempt.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} catch (UserDoesNotExistException e) { 
			String errorMessage = "User wasn't found in DB during check for"
					+ " invalid user for copy on lending attempt.";
			Logger.severe(errorMessage);
			
			return true;
		}
	}

	/**
	 * Registers that a specific medium-copy has been checked out by a specific
	 * user. Until it is returned, it is unavailable to other users.
	 *
	 * @param signatureContainer A DTO container with a signature that refers to 
	 *                           the medium-copy.
	 * @param userEmail      A DTO container with an ID that refers to the user.
	 * @throws CopyDoesNotExistException   Is thrown when the either the medium
	 * 								-copy has invalid status or when the copy
	 *                                     doesn't exist in the data store.
	 * @throws UserDoesNotExistException if the User wasn't found in the 
	 * 									database or another user marked it
	 * @throws CopyIsNotAvailableException if copy has invalid status for 
	 * 									lending
	 * @author Jonas Picker
	 */
	public static void lendCopy(CopyDto signatureContainer, UserDto userEmail) 
			throws CopyDoesNotExistException, InvalidUserForCopyException, 
			CopyIsNotAvailableException, UserDoesNotExistException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		String signature = signatureContainer.getSignature();
		int userId = UserDao.getUserIdByEmail(conn, userEmail);
		if (!copySignatureExists(conn, signatureContainer)) {
			String msg = "Error during copy lending! Signature doesn't exist.";
			Logger.severe(msg);
			throw new CopyDoesNotExistException(msg);
		}
		updateMarkedCopies();
		if (copyIsLentBySignature(conn, signatureContainer)) {
			String msg = "Error during copy lending! Copy is already lent.";
			Logger.severe(msg);
			throw new CopyIsNotAvailableException(msg);
		} else if (invalidUserLendingAttempt(conn, 
				signatureContainer, userEmail)) {
			String msg = "Error during copy lending! The Copy wasn't marked " 
						+ "for pickup by this user.";
			Logger.severe(msg);
			throw new InvalidUserForCopyException(msg);
		}
		Timestamp limitTimestamp;
		try {
			limitTimestamp = new Timestamp(
					System.currentTimeMillis() + getAppliedLendingLimit(conn,
							signature, userId));
		} catch (SQLException e1) {
			Logger.development("SQLException while comparing lend limits");
			throw new CopyDoesNotExistException("SQLException while comparing" 
												+ " lend limits");
		}
		try {
			PreparedStatement lendCopy = conn.prepareStatement(
				"UPDATE mediumCopy SET status = CAST('BORROWED' AS copyStatus),"
				+ " actor = ?, deadline = ? WHERE signature = ?;");
			lendCopy.setInt(1, userId);
			lendCopy.setTimestamp(2, limitTimestamp);
			lendCopy.setString(3, signature);
			int rowsUpdated = lendCopy.executeUpdate();
			conn.commit();
			Logger.development(rowsUpdated + " copy was lent to a user.");
			lendCopy.close();
		} catch (SQLException e) {
			String errorMessage = "Connection Error while trying to lend copy";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} finally {
			instance.releaseConnection(conn);
		}
	}

	/**
	 * Refreshes the copy status on all marked copies with expired pickup 
	 * deadlines. 
	 * 
	 * @author Jonas Picker
	 */
	public static void updateMarkedCopies() {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement refreshMarkedDeadlines = conn.prepareStatement(
					"UPDATE mediumCopy SET status = CAST('AVAILABLE' "
					+ "AS copyStatus), actor = null, deadline = null"
					+ " WHERE ((status = CAST('READY_FOR_PICKUP' "
					+ "AS copyStatus)) AND (deadline < CURRENT_TIMESTAMP));");
			int updated = refreshMarkedDeadlines.executeUpdate();
			conn.commit();
			Logger.detailed(updated + " copies were made availabe because" 
					+ " pickup deadline expired.");
			refreshMarkedDeadlines.close();
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while"
					+ " refreshing marked copies with expired deadline.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} finally {
			instance.releaseConnection(conn);
		}
	}

	// returns applying limit in milliseconds as long, user and signature must
	// exist, limit hierarchie is user > medium > global, non existing values 
	// are filtered out
	/* @author Jonas Picker */
	private static long getAppliedLendingLimit(Connection conn,
			String signature, int userId) throws SQLException {
		PreparedStatement getCopysMedium = conn
				.prepareStatement("SELECT" 
						+ " mediumId FROM mediumCopy WHERE signature = ?;");
		getCopysMedium.setString(1, signature);
		ResultSet rs1 = getCopysMedium.executeQuery();
		conn.commit();
		rs1.next();
		int fromMedium = rs1.getInt(1);
		rs1.close();
		getCopysMedium.close();
		double globalSeconds = ApplicationDao.getLendingPeriodSeconds(conn);
		PreparedStatement getMediumLimit = conn.prepareStatement(
				"SELECT EXTRACT (EPOCH FROM (SELECT mediumLendPeriod " 
						+ "FROM medium WHERE mediumId = ?));");
		getMediumLimit.setInt(1, fromMedium);
		ResultSet rs3 = getMediumLimit.executeQuery();
		conn.commit();
		rs3.next();
		double mediumSeconds = rs3.getDouble(1);
		rs3.close();
		getMediumLimit.close();
		PreparedStatement getUserLimit = conn.prepareStatement(
				"SELECT EXTRACT (EPOCH FROM (SELECT userLendPeriod "           
						+ "FROM users WHERE userId = ?));");
		getUserLimit.setInt(1, userId);
		ResultSet rs4 = getUserLimit.executeQuery();
		conn.commit();
		rs4.next();
		double userSeconds = rs4.getDouble(1);
		rs4.close();
		getUserLimit.close();
		double applyingLimit = 0;
		if (globalSeconds == 0 && mediumSeconds == 0 && userSeconds == 0) {
			applyingLimit = DEFAULT_MAXIMUM_LEND_LIMIT_SECONDS;
		} else if (mediumSeconds == 0 && userSeconds == 0) {
			applyingLimit = globalSeconds;
		} else if (userSeconds == 0 && globalSeconds == 0) {
			applyingLimit = mediumSeconds;
		} else if (globalSeconds == 0 && mediumSeconds == 0) {
			applyingLimit = userSeconds;
		} else if (globalSeconds == 0) {
			applyingLimit = userSeconds;
		} else if (mediumSeconds == 0) {
			applyingLimit = userSeconds;
		} else if (userSeconds == 0) {
			applyingLimit = mediumSeconds;
		} else if (globalSeconds != 0 && mediumSeconds != 0 
				&& userSeconds != 0) {
			applyingLimit = userSeconds;
		} else {
			Logger.development("This is the dark basement of the ifElse-Tower, "
		+ "noone should ever visit it.");
		}
		long longTime = (long) (applyingLimit * 1000);
		Timestamp ts = new Timestamp(System.currentTimeMillis() + longTime);
		Logger.development("Calculated lend limit expires on " 
		+ ts.toLocalDateTime().toString());
		return longTime;
	}

	/**
	 * Registers that a specific medium-copy has been returned by a specific 
	 * user. It is then available to other users for check-out.
	 *
	 * @param signatureContainer A DTO container with a signature that refers to 
	 *                           the medium-copy.
	 * @param userEmail          A DTO container with an ID that refers to the 
	 * user.
	 * @throws CopyDoesNotExistException   Is thrown when the either the medium
	 *                                     -copy has invalid status or when the
	 *                                     copy doesn't exist in the data store.
	 * @throws UserDoesNotExistException   if the User wasn't found in the
	 *                                      database or he hasn't lent the copy
	 * @throws CopyIsNotAvailableException if the copy wasn't marked as lent
	 * @author Jonas Picker
	 */
	public static void returnCopy(CopyDto signatureContainer, UserDto userEmail)
			throws UserDoesNotExistException, CopyDoesNotExistException, 
			CopyIsNotAvailableException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		String signature = signatureContainer.getSignature();
		int userId = UserDao.getUserIdByEmail(conn, userEmail);
		if (!copySignatureExists(conn, signatureContainer)) {
			String msg = "Error during copy return! Signature doesn't exist.";
			Logger.severe(msg);
			instance.releaseConnection(conn);
			throw new CopyDoesNotExistException(msg);
		} else if (invalidCopyStatusReturnAttempt(conn, signatureContainer)) {
			String msg = "Error during copy return! Copy wasn't lent in the " 
		+ "first place.";
			Logger.severe(msg);
			instance.releaseConnection(conn);
			throw new CopyIsNotAvailableException(msg);
		} else if (invalidActorReturnAttempt(conn, 
				signatureContainer, userEmail)) {
			String msg = "Copy couldn't be returned. The copy wasn't lent " 
		+ "(by this user).";
			Logger.severe(msg);
			instance.releaseConnection(conn);
			throw new UserDoesNotExistException(msg);
		}
		try {
			PreparedStatement returnCopy = conn.prepareStatement(
					"UPDATE mediumCopy SET status = CAST('AVAILABLE' "
					+ "AS copyStatus), actor = null, deadline = null " 
							+ "WHERE ((signature = ?) AND (actor = ?) "
					+ "AND (status = CAST('BORROWED' AS copyStatus)));");
			returnCopy.setString(1, signature);
			returnCopy.setInt(2, userId);
			int updated = returnCopy.executeUpdate();
			conn.commit();
			Logger.development(updated 
						+ " copy was returned and" 
						+ " became available.");
			returnCopy.close();
		} catch (SQLException e) {
			String msg = "An Error occured during communication with" 
						+ " database on copy returning process.";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			instance.releaseConnection(conn);
		}
	}

	/**
	 * Validates a return attempt by a given user with a given copysignature.
	 * 
	 * @param signatureContainer CopyDto with signature input
	 * @param user               UserDto with userEmail
	 * @throws CopyDoesNotExistException     if signature wasn't found
	 * @throws CopyIsNotAvailableException   if copystatus invalid for returning
	 * @throws InvalidUserForCopyException   if wrong user tries to return
	 * @throws UserExceededDeadlineException if return deadline was exceeded
	 * @author Jonas Picker
	 */
	public static void validateReturnProcess(CopyDto signatureContainer, 
			UserDto user) throws CopyDoesNotExistException, 
					CopyIsNotAvailableException, InvalidUserForCopyException,
					UserExceededDeadlineException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (!copySignatureExists(conn, signatureContainer)) {
				throw new CopyDoesNotExistException("Signature doesn't exist");
			} else if (invalidCopyStatusReturnAttempt(conn, 
					signatureContainer)) {
				throw new CopyIsNotAvailableException("Invalid copy status"
						+ " for this return");
			} else if (invalidActorReturnAttempt(conn, 
					signatureContainer, user)) {
				throw new InvalidUserForCopyException("Invalid user for "
						+ "this return");
			} else if (invalidDeadlineReturnAttempt(conn, 
					signatureContainer, user)) {
				throw new UserExceededDeadlineException("The return deadline"
						+ " was exceeded by this user");
			}
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Validates a lending attempt by a given user with a given copysignature.
	 * 
	 * @param signatureContainer CopyDto with signature input
	 * @param user               UserDto with userEmail
	 * @throws CopyDoesNotExistException   if signature wasn't found
	 * @throws CopyIsNotAvailableException if copystatus invalid for lending
	 * @throws InvalidUserForCopyException if wrong user tries to lend
	 * @author Jonas Picker
	 */
	public static void validateLendingProcess(CopyDto signatureContainer, 
			UserDto user) throws CopyDoesNotExistException, 
					CopyIsNotAvailableException, InvalidUserForCopyException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (!copySignatureExists(conn, signatureContainer)) {
				throw new CopyDoesNotExistException("Signature doesn't exist");
			} else if (copyIsLentBySignature(conn, signatureContainer)) {
				throw new CopyIsNotAvailableException("Invalid copy status," 
												+ " cannot lend copy");
			} else if (invalidUserLendingAttempt(conn, 
					signatureContainer, user)) {
				throw new InvalidUserForCopyException("Invalid user for " 
												+ "lending process");
			}
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Iterates through the copies and retrieves the borrowed ones whose dead-
	 * line will expire within a margin defined by the applications remider
	 * offset. Also fetches the users who borrowed it and the medium the copy 
	 * belongs to. Uses @author Sergei Pravdins helper methods.
	 *  
	 * @throws MediumDoesNotExistException if a mediumid couldn't be found
	 * @throws UserDoesNotExistException if the userid wasn't found in db
	 * @author Jonas Picker
	 */
	public static List<MediumCopyUserDto> readDueDateReminders() 
			throws LostConnectionException, MaxConnectionsException,
			MediumDoesNotExistException, UserDoesNotExistException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = 
				instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		List<MediumCopyUserDto> result = new ArrayList<MediumCopyUserDto>();
		try {
			PreparedStatement getReminderOffset = conn.prepareStatement(		
					"SELECT EXTRACT (EPOCH FROM (SELECT reminderoffset " 
							+ "FROM application WHERE one = 1));");
			ResultSet rs1 = getReminderOffset.executeQuery();
			conn.commit();
			rs1.next();
			double reminderOffset = rs1.getDouble(1);
			getReminderOffset.close();
			rs1.close();
			if (reminderOffset == 0) {
				reminderOffset = DEFAULT_REMINDER_OFFSET_SECONDS;
			}
			Timestamp offset = new Timestamp(System.currentTimeMillis() 
					+ (((long) reminderOffset) * 1000));
			PreparedStatement readAlmostDue = conn.prepareStatement(
					"SELECT copyid, mediumid, signature, bibposition, "
					+ "status, deadline, actor FROM mediumcopy WHERE "
					+ "(deadline is not null) AND (deadline <= ?) "
					+ "AND (deadline > CURRENT_TIMESTAMP) "
					+ "AND (status = CAST('BORROWED' AS copystatus));"
					);
			readAlmostDue.setTimestamp(1, offset);
			ResultSet rs2 = readAlmostDue.executeQuery();
			conn.commit();
			while (rs2.next()) {
				CopyDto copy = new CopyDto();
				populateCopyDto(copy, rs2);
				MediumDto medium = new MediumDto();
				medium.setId(copy.getMediumId());
				readMediumHelper(conn, medium);
				UserDto user = new UserDto();
				user.setId(copy.getActor());
				UserDao.readUserForProfileHelper(conn, user);
				MediumCopyUserDto entry = new MediumCopyUserDto();
				entry.setCopy(copy);
				entry.setUser(user);
				entry.setMedium(medium);
				result.add(entry);
			}
			rs2.close();
			readAlmostDue.close();
		} catch (SQLException e) {
			String message = "An Error occured while trying to read due date "
					+ "reminders";
			Logger.severe(message);
			throw new LostConnectionException(message, e);
		} finally {
			instance.releaseConnection(conn);
		}
		
		return result;
	}

	/**
	 * Read all copies from the database that are ready to be picked up.
	 *
	 * @param pagination A container defining the page size and the amount of pages.
	 * @return The list of copies ready for pickup and some related data.
	 */
	public static List<MediumCopyUserDto> readLentCopies(PaginationDto pagination) {

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			final var statementBody = """
					from
						medium m,
						mediumcopy c,
						users u
					where
						c.status = 'BORROWED'
							and
						c.mediumid = m.mediumid
							and
						c.actor = u.userid
					""";

			{
				final var countStatement = connection.prepareStatement("select count(c.copyid) " + statementBody);

				final var resultSet = countStatement.executeQuery();
				resultSet.next();

				Pagination.updatePagination(pagination, resultSet.getInt(1));
			}

			final var itemsStatement = connection.prepareStatement("""
					select
						m.mediumid, m.title,
						c.signature, c.bibposition, c.deadline,
						u.userid, u.emailaddress, u.name, u.surname
					%s
					offset ?
					limit ?
					""".formatted(statementBody));
			// @Task sorting
			itemsStatement.setInt(1, Pagination.pageOffset(pagination));
			itemsStatement.setInt(2, Pagination.getEntriesPerPage());

			final var resultSet = itemsStatement.executeQuery();
			final var results = new ArrayList<MediumCopyUserDto>();

			while (resultSet.next()) {

				final var compound = new MediumCopyUserDto();

				final var medium = new MediumDto();
				medium.setId(resultSet.getInt(1));
				medium.setTitle(resultSet.getString(2));
				compound.setMedium(medium);

				final var copy = new CopyDto();
				copy.setSignature(resultSet.getString(3));
				copy.setLocation(resultSet.getString(4));
				copy.setDeadline(resultSet.getTimestamp(5));
				compound.setCopy(copy);

				final var user = new UserDto();
				user.setId(resultSet.getInt(6));
				user.setEmailAddress(resultSet.getString(7));
				user.setFirstName(resultSet.getString(8));
				user.setLastName(resultSet.getString(9));
				compound.setUser(user);

				results.add(compound);
			}

			connection.commit();

			return results;
		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				final var message = "Failed to rollback database transaction";
				Logger.severe(message);
				throw new LostConnectionException(message);
			}

			final var message = "Database error occurred while reading copies ready for pickup: "
					+ exception.getMessage();
			Logger.severe(message);

			throw new LostConnectionException(message, exception);

		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}
}
