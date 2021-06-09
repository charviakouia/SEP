package de.dedede.model.persistence.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.postgresql.util.PGInterval;

import de.dedede.model.data.dtos.AttributeDto;
import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumCopyAttributeUserDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;

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

	private MediumDao() {
	}

	/**
	 * Enters new medium data into the persistent data store. Any copies associated
	 * with this medium are not entered automatically. The enclosed ID must not be
	 * associated with an existing data entry in the data store. Otherwise, an
	 * exception is thrown.
	 *
	 * @param mediumDto A DTO container with the new medium data
	 * @throws EntityInstanceNotUniqueException Is thrown when the enclosed ID is
	 *                                          already associated with an existing
	 *                                          data entry.
	 * @see MediumDto
	 */
	public static void createMedium(MediumDto mediumDto) throws EntityInstanceNotUniqueException {
		// TODO: MS2 von Ivan
	}

	/**
	 * Fetches a medium from the persistent data store using an ID. The ID must be
	 * associated with an existing data entry. Otherwise, an exception is thrown.
	 * 
	 * @param mediumDto A DTO container with the ID of the medium data to be fetched
	 * @throws EntityInstanceDoesNotExistException Is thrown when the passed ID
	 * 		is not associated with any existing data entry.
	 * @return A DTO container with the medium data referenced by the ID.
	 * @throws MediumDoesNotExistException 
	 */
	public static MediumDto readMedium(MediumDto mediumDto)
			throws EntityInstanceDoesNotExistException, LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
		Connection conn = getConnection();
		try {
			return readMediumHelper(conn, mediumDto);
		} catch (SQLException e){
			String msg = "Database error occurred while reading application entity with id: " + mediumDto.getId();
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
	 * @param paginationDetails A container for the search terms, page size, and
	 *                          page number.
	 * @return A list of DTO containers with the medium data.
	 * @see MediumDto
	 */
	public static List<MediumDto> readMediaBySearchCriteria(PaginationDto paginationDetails) {
		// TODO: MS2 von León
		return null;
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
		// TODO: MS2 von Sergej
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
	public static MediumDto deleteMedium(MediumDto mediumDto) throws MediumDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = getConnection();
		try {
			if (mediumEntityExists(conn, mediumDto)){
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
	 * @throws EntityInstanceNotUniqueException Is thrown if the passed ID is already
	 * 		associated with a data entry.
	 * @see CopyDto
	 */

	public static void createCopy(CopyDto copyDto, MediumDto mediumDto)
			throws EntityInstanceNotUniqueException, LostConnectionException, MaxConnectionsException {
		Connection conn = getConnection();
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Mediumcopy (copyid, mediumid, signature, bibposition, status, " +
							"deadline, actor) VALUES " +
							"(?, ?, ?, ?, CAST (? AS copyStatus), ?, ?);",
					Statement.RETURN_GENERATED_KEYS
			);
			createStmt.setInt(2, mediumDto.getId());
			populateCopyStatement(createStmt, copyDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0){ attemptToInsertGeneratedKey(copyDto, createStmt); }
			conn.commit();
		} catch (SQLException e){
			String msg = "Database error occurred while creating mediumCopy entity with id: " + copyDto.getId();
//			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
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
	public static List<CopyDto> readAllOverdueCopies(PaginationDto paginationDetails) {
		// TODO: MS3 von Ivan
		return null;
	}

	public static List<CopyDto> readMarkedCopiesByUser(PaginationDto paginationDetails) {
		// TODO: MS3 von Sergej
		return null;

	}

	public static List<CopyDto> readLentCopiesByUser(PaginationDto paginationDetails) {
		// TODO: MS3 von Sergej
		return null;

	}

	/*
	 * /** Fetches a paginated list of all medium-copies belonging to a specific
	 * medium from the persistent data store.
	 *
	 * @param mediumDto A DTO container with the medium-ID, for which the copies are
	 * being queried.
	 * 
	 * @param paginationDetails A container for the page size and number.
	 * 
	 * @return A list of DTO containers with the medium-copy data for the given
	 * medium-ID.
	 * 
	 * @see CopyDto
	 */
	/*
	 * public static List<CopyDto> readAllCopiesFromMedium(MediumDto mediumDto,
	 * PaginationDto paginationDetails) {
	 * 
	 * // Wir wollen doch keine paginierte Liste von Exemplaren haben! // -> Diese
	 * Methode ist unnötig.
	 * 
	 * return null; }
	 */

	/**
	 * Overwrites existing medium-copy data in the persistent data store. The
	 * enclosed signature must be associated with an existing entry and is used to
	 * identify and remove data. Otherwise, an exception is thrown.
	 *
	 * @param copyDto A DTO container with the overwriting data
	 * @throws CopyDoesNotExistException Is thrown if the enclosed
	 *                                             signature isn't associated with
	 *                                             any data entry.
	 * @see CopyDto
	 */
	public static void updateCopy(CopyDto copyDto) throws CopyDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = getConnection();
		try {
			PreparedStatement updateStmt = conn.prepareStatement(
					"UPDATE Mediumcopy" +
							"SET (copyid = ?, mediumid = ?, signature = ?, bibposition = ?," +
							" status = CAST(? AS copyStatus), deadline = ?, actor = ?) WHERE copyid = ? ");
			populateCopyStatement(updateStmt, copyDto);
			int numAffectedRows = updateStmt.executeUpdate();
			conn.commit();
			if (numAffectedRows == 0){
				String msg = String.format("No entity with the id: %d exists", copyDto.getId());
				// Logger.severe(msg);
				throw new CopyDoesNotExistException(msg);
			}
		} catch (SQLException e){
			String msg = "Database error occurred while updating application entity with id: " + copyDto.getId();
			// Logger.severe(msg);
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

	public static CopyDto deleteCopy(CopyDto copyDto) throws MediumDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = getConnection();
		try {
			if (copyEntityExists(conn, copyDto)){
				deleteCopyHelper(conn, copyDto);
				conn.commit();
				return copyDto;
			} else {
				String msg = String.format("No entity with the id: %d exists", copyDto.getId());
				// Logger.severe(msg);
				throw new MediumDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while deleting medium entity with id: " + copyDto.getId();
			// Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	public static void readGlobalAttributes() {
		// TODO: MS2 von Ivan
	}

	public static void updateMediumAttributes(MediumDto mediumDto, Collection<AttributeDto> attributes) {
		// TODO: MS2 von Sergej
	}

	public static List<MediumCopyAttributeUserDto> readCopiesReadyForPickup(PaginationDto paginationDetails)
			throws LostConnectionException, MaxConnectionsException {

		final int entriesPerPage = Integer.parseInt(ConfigReader.getInstance().getKey("MAX_PAGES", "20"));
		final var connection = getConnection();

		try {

			final var statement = connection.prepareStatement("""
					SELECT
						c.mediumid,
						c.signature, c.bibposition, c.deadline,
						a.attributename, a.attributevalue,
						u.emailaddress, u.name, u.surname
					FROM
						mediumcopy c,
						attributetype t,
						customattribute a,
						users u
					WHERE
						c.status = 'READY_FOR_PICKUP' AND
						c.mediumid = t.mediumid AND
						t.previewposition = 'SECOND' AND
						t.attributeid = a.attributeid AND
						c.actor = u.userid
					OFFSET ?
					LIMIT ?
					""");
			// @Task sorting
			statement.setInt(1, paginationDetails.getPageNumber() * entriesPerPage - 1);
			statement.setInt(2, entriesPerPage);

			final var resultSet = statement.executeQuery();
			Logger.development("MediumDao/CRFPAU len=" + resultSet.getFetchSize());
			final var results = new ArrayList<MediumCopyAttributeUserDto>();

			// @Bug does not handle multi-valued attributes correctly!
			while (resultSet.next()) {
				final var compound = new MediumCopyAttributeUserDto();

				final var medium = new MediumDto();
				medium.setId(resultSet.getInt(1));
				compound.setMedium(medium);
				
				final var copy = new CopyDto();
				copy.setSignature(resultSet.getString(2));
				copy.setLocation(resultSet.getString(3));
				copy.setDeadline(resultSet.getTimestamp(4));
				compound.setCopy(copy);

				final var attribute = new AttributeDto();
				attribute.setName(resultSet.getString(5));
				attribute.setTextValue(List.of(resultSet.getString(6)));
				compound.setAttribute(attribute);

				final var user = new UserDto();
				user.setEmailAddress(resultSet.getString(7));
				user.setFirstName(resultSet.getString(8));
				user.setLastName(resultSet.getString(9));
				compound.setUser(user);
			}

			connection.commit();

			return results;
		} catch (SQLException exeption) {

			final var message = "Database error occurred while reading copies ready for pickup: " + exeption.getMessage();
			Logger.severe(message);

			throw new LostConnectionException(message, exeption);

		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}
	

	// Helper methods:

	private static Connection getConnection() throws LostConnectionException, MaxConnectionsException {
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e) {
			Logger.severe("Couldn't configure the connection");
			ConnectionPool.getInstance().releaseConnection(conn);
			throw new LostConnectionException("Couldn't configure the connection");
		}
		return conn;
	}

	private static boolean mediumEntityExists(Connection conn, MediumDto mediumDto)
			throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(mediumid) FROM Medium WHERE mediumid = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setString(1, String.valueOf(mediumDto.getId()));
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
		checkingStmt.setString(1, String.valueOf(copyDto.getId()));
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	/**
	 * @author Sergei Pravdin
	 * @throws MediumDoesNotExistException 
	 */
	private static MediumDto readMediumHelper(Connection conn, MediumDto mediumDto)
			throws SQLException, LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
		PreparedStatement readStmt = conn.prepareStatement(
				"SELECT mediumid, mediumlendperiod, hascategory " + "FROM Medium " + "WHERE mediumid = ?;");
		readStmt.setInt(1, Math.toIntExact(mediumDto.getId()));
		ResultSet resultSet = readStmt.executeQuery();
		if (resultSet.next()) {
			populateMediumDto(resultSet, mediumDto);
			conn.commit();
			return mediumDto;
		} else {
			conn.commit();
			String msg = "A medium does not exist with id: " + mediumDto.getId();
			throw new MediumDoesNotExistException(msg);
		}
	}

	private static void populateMediumDto(ResultSet resultSet, MediumDto mediumDto) throws SQLException, LostConnectionException, MaxConnectionsException {
		mediumDto.setId(resultSet.getInt(1));
		long returnPeriodSeconds = Math.round(((PGInterval) resultSet.getObject(2)).getSeconds());
		mediumDto.setReturnPeriod(Duration.ofSeconds(returnPeriodSeconds));
		mediumDto.getCategory().setId(resultSet.getInt(3));
		try {
			mediumDto.setCategory(CategoryDao.readCategory(mediumDto.getCategory()));
		} catch (CategoryDoesNotExistException e) {
			// ignore
		}
		readCopiesHelper(mediumDto);
		readAttributesHelper(mediumDto);
	}

	private static void readCopiesHelper(MediumDto mediumDto) throws SQLException, LostConnectionException, MaxConnectionsException {
		Connection conn = getConnection();
		PreparedStatement readStmt = conn.prepareStatement(
				"SELECT copyid, mediumid = ?, signature, bibposition, status, deadline, actor " +
						"FROM Mediumcopy " +
						"WHERE mediumid = ?;"
		);
		readStmt.setInt(2, Math.toIntExact(mediumDto.getId()));
		ResultSet resultSet = readStmt.executeQuery();
		while (resultSet.next()) {
			CopyDto copyDto = new CopyDto();
			populateCopyDto(copyDto, resultSet);
			mediumDto.addCopy(copyDto.getId(), copyDto);
		}
	}

	private static void populateCopyDto(CopyDto copyDto, ResultSet resultSet) throws SQLException {
		copyDto.setId(resultSet.getInt(1));
		copyDto.setSignature(resultSet.getString(3));
		copyDto.setLocation(resultSet.getString(4));
		copyDto.setCopyStatus((CopyStatus) resultSet.getObject(5));
		copyDto.setDeadline(resultSet.getTimestamp(6));
		copyDto.setActor(resultSet.getInt(7));
	}

	private static void readAttributesHelper(MediumDto mediumDto) {
		// TODO: MS2 von Sergej
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static PGInterval toPGInterval(Duration duration){
		PGInterval result = new PGInterval();
		result.setSeconds(duration.getSeconds());
		return result;
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void populateCopyStatement(PreparedStatement stmt, CopyDto copyDto) throws SQLException {
		stmt.setInt(1, copyDto.getId());
		stmt.setString(3, copyDto.getSignature());
		stmt.setString(4, copyDto.getLocation());
		stmt.setString(5, String.valueOf(copyDto.getCopyStatus()));
		stmt.setTimestamp(6, copyDto.getDeadline());
		if (copyDto.getActor() != 0) {
			stmt.setInt(7, copyDto.getActor());
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void attemptToInsertGeneratedKey(CopyDto copyDto, Statement stmt)
			throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()){
			copyDto.setId(resultSet.getInt(1));
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void deleteMediumHelper(Connection conn, MediumDto mediumDto) throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM Medium " +
						"WHERE mediumid = ?;"
		);
		deleteStmt.setInt(1, Math.toIntExact(mediumDto.getId()));
		deleteStmt.executeUpdate();
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void deleteCopyHelper(Connection conn, CopyDto copyDto) throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM MediumCopy " +
						"WHERE copyid = ?;"
		);
		deleteStmt.setInt(1, Math.toIntExact(copyDto.getId()));
		deleteStmt.executeUpdate();
		conn.commit();
	}
	

	/**
	 * Checks if the copy was lent and inverts the result.
	 * 
	 * @param signature the copy's signature in a Dto
	 * @return true if the return attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	public static boolean invalidCopyStatusReturnAttempt(CopyDto signatureContainer) {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(signature) FROM mediumCopy WHERE ((signature = ?) AND (status = CAST('LENT' AS copyStatus)))) > 0 THEN false ELSE true END AS invalidCopyStatus;"
					);
			checkingStmt.setString(1, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured while with db communication checking for invalid actor on copy return.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}														
	}
	
	/**
	 * Checks if the copy was lent by the (existing) user who wants to return it 
	 * and inverts the result.
	 * 
	 * @param signature the copy's signature in a Dto
	 * @param userEmail the mail address of the user in a dto
	 * @return true if the return attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	public static boolean invalidActorReturnAttempt(CopyDto signatureContainer, UserDto userEmail) {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			int userId = checkUser(userEmail);
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(actor) FROM mediumCopy WHERE ((actor = ?) AND (signature = ?) AND (status = CAST('LENT' AS copyStatus)))) > 0 THEN false ELSE true END AS invalidActor;"
					);
			checkingStmt.setInt(1, userId);
			checkingStmt.setString(2, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while checking for invalid actor on copy return.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} catch (UserDoesNotExistException e) { 						//already checked by UserValidator at this point
			String errorMessage = "User wasn't found in DB during check for invalid actor on copy return attempt.";
			Logger.severe(errorMessage);
			return true;
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}														
	}
	
	/**
	 * Checks if the copy was lent by the (existing) user who wants to return 
	 * it, also checks if the deadline was met and inverts the result.
	 * 
	 * @param signature the copy's signature in a Dto
	 * @param userEmail the mail address of the user in a dto
	 * @return true if the return attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	public static boolean invalidDeadlineReturnAttempt(CopyDto signatureContainer, UserDto userEmail) {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			int userId = checkUser(userEmail);
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(actor) FROM mediumCopy WHERE ((actor = ?) AND (signature = ?) AND (status = CAST('LENT' AS copyStatus)) AND (deadline >= CURRENT_TIMESTAMP))) > 0 THEN false ELSE true END AS invalidDeadline;"
					);
			checkingStmt.setInt(1, userId);
			checkingStmt.setString(2, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while checking for valid copy return.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} catch (UserDoesNotExistException e) { 						//already checked by UserValidator at this point
			String errorMessage = "User wasn't foud in DB during check for invalid copy return attempt.";
			Logger.severe(errorMessage);
			return true;
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}														
	}
	
	/**
	 * Checks for the existance of a copy by its signature, modelled after 
	 * copyEntityExists() by @author Sergei Pravdin.
	 * 
	 * @see MediumDao.copyEntityExists()
	 * @param signature the copy's signature in a Dto
	 * @return true if the copy exists
	 * 
	 * @author Jonas Picker
	 */
	public static boolean copySignatureExists(CopyDto signatureContainer) {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		boolean result = false;
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(signature) FROM MediumCopy WHERE signature = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
			checkingStmt.setString(1, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1); 
			checkingStmt.close();
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while checking for Copy Signature existence.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
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
	public static boolean copyIsLentBySignature(CopyDto signatureContainer) {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(signature) FROM mediumCopy WHERE ((signature = ?) AND (status = CAST('LENT' AS copyStatus))) > 0 THEN true ELSE false END AS invalidLendingStatus;"
					);
			checkingStmt.setString(1, signature);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while checking for copy status on lending attempt.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}											
	}
	
	/**
	 * Checks if a lending attempt for a given copy signature and (existing) 
	 * user is valid and inverts the result.
	 * 
	 * @param signatureContainer the copy's signature in a Dto
	 * @param userEmail the user Email in a Dto
	 * @return true if the lending attempt is invalid
	 * 
	 * @author Jonas Picker
	 */
	public static boolean invalidUserLendingAttempt(CopyDto signatureContainer, UserDto userEmail) {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		boolean result = true;
		try {
			int userId = UserDao.readUserByEmail(userEmail).getId();
			PreparedStatement checkingStmt = conn.prepareStatement(
					"SELECT CASE WHEN (SELECT COUNT(signature) FROM mediumCopy WHERE ((signature = ?) AND (((actor = ?) AND (status = CAST('READY_FOR_PICKUP' AS copyStatus))) OR (status = CAST('AVAILABLE' AS copyStatus)))) > 0 THEN false ELSE true END AS invalidUserLending;"
					);
			checkingStmt.setString(1, signature);
			checkingStmt.setInt(2, userId);
			ResultSet resultSet = checkingStmt.executeQuery();
			conn.commit();
			resultSet.next();
			result = resultSet.getBoolean(1);
			checkingStmt.close();
			return result;
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication while checking for invalid user for copy on lending attempt.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} catch (EntityInstanceDoesNotExistException e) { 						//already checked by UserValidator at this point
			String errorMessage = "User wasn't found in DB during check for invalid user for copy on lending attempt.";
			Logger.severe(errorMessage);
			return true;
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}											
	}
		
	/**
	 * Registers that a specific medium-copy has been checked out by a specific
	 * user. Until it is returned, it is unavailable to other users.
	 *
	 * @param copyDto A DTO container with a signature that refers to the
	 *                medium-copy.
	 * @param userDto A DTO container with an ID that refers to the user.
	 * @throws CopyDoesNotExistException Is thrown when the either the
	 *                                   medium-copy has invalid status
	 *                                   or when the copy doesn't
	 *                                   exist in the data store.
	 * @throws UserDoesNotExistException if the User wasn't found in the 
	 * 									 database or another user marked it
	 * @author Jonas Picker
	 */
	public static void lendCopy(CopyDto signatureContainer, UserDto userEmail) throws CopyDoesNotExistException, UserDoesNotExistException {
		Connection conn = getConnection();										//ConnectionPool verbesserung abwarten!
		String signature = signatureContainer.getSignature();
		int userId = checkUser(userEmail);
		if (!copySignatureExists(signatureContainer)) {
			String msg = "Error during copy lending! Signature doesn't exist.";
			Logger.severe(msg);
			throw new CopyDoesNotExistException(msg);
		}
		refreshCopyStatusIfMarked();
		if (copyIsLentBySignature(signatureContainer)) {			
			String msg = "Error during copy lending! Copy is already lent.";
			Logger.severe(msg);
			throw new CopyDoesNotExistException(msg);
		} else if (invalidUserLendingAttempt(signatureContainer, userEmail)) {
			String msg = "Error during copy lending! The Copy wasn't marked for pickup by this user.";
			Logger.severe(msg);
			throw new UserDoesNotExistException(msg);
		} 
		try {
		Timestamp limitTimestamp = new Timestamp(System.currentTimeMillis() + smallestLimitMillis(conn, signature, userId));
		PreparedStatement lendCopy = conn.prepareStatement(
				"UPDATE mediumCopy SET status = CAST('LENT' AS copyStatus), actor = ?, deadline = ? WHERE signature = ?;"
				);
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
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	/**
	 * Refreshes the copy status on all marked copies with expired pickup 
	 * deadlines
	 * 
	 * @author Jonas Picker
	 */
	public static void refreshCopyStatusIfMarked() {
		Connection conn = getConnection();										//ConnectionPool verbesserung!
		try {
			PreparedStatement refreshMarkedDeadlines = conn.prepareStatement(
					"UPDATE mediumCopy SET status = CAST('AVAILABLE' AS copyStatus) WHERE ((status = CAST('READY_FOR_PICKUP' AS copyStatus)) AND (deadline < CURRENT_TIMESTAMP));"
					);
			int updated = refreshMarkedDeadlines.executeUpdate();
			conn.commit();
			Logger.detailed(updated + " copies were made availabe because pickup deadline expired.");
			refreshMarkedDeadlines.close();
		} catch (SQLException e) {
			String errorMessage = "Error occured with db communication whilerefreshing marked copies with expired deadline.";
			Logger.severe(errorMessage);
			throw new LostConnectionException(errorMessage, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	//returns smallest limit in milliseconds as long
	/* @author Jonas Picker */
	private static long smallestLimitMillis(Connection conn, String signature, int userId) throws SQLException {
		PreparedStatement getCopysMedium = conn.prepareStatement("SELECT mediumId FROM mediumCopy WHERE signature = ?;");
		getCopysMedium.setString(1, signature);
		ResultSet rs1 = getCopysMedium.executeQuery();
		conn.commit();
		rs1.next();
		int fromMedium = rs1.getInt(1);
		getCopysMedium.close();
		PreparedStatement getGlobalLimit = conn.prepareStatement("SELECT globalLendLimit FROM application WHERE one = 1;");
		ResultSet rs2 = getGlobalLimit.executeQuery();
		conn.commit();
		rs2.next();
		PGInterval globalLimit = new PGInterval(rs2.getString(1));
		getGlobalLimit.close();
		PreparedStatement getMediumLimit = conn.prepareStatement("SELECT mediumLendPeriod FROM medium WHERE mediumId = ?;");
		getMediumLimit.setInt(1, fromMedium);
		ResultSet rs3 = getMediumLimit.executeQuery();
		conn.commit();
		rs3.next();
		PGInterval mediumLimit = new PGInterval(rs3.getString(1));
		getMediumLimit.close();
		PreparedStatement getUserLimit = conn.prepareStatement("SELECT userLendPeriod FROM users WHERE userId = ?;");
		getUserLimit.setInt(1, userId);
		ResultSet rs4 = getUserLimit.executeQuery();
		conn.commit();
		rs4.next();
		PGInterval userLimit = new PGInterval(rs4.getString(1));
		getUserLimit.close();
		double[] intervals = new double[] {globalLimit.getSeconds(), userLimit.getSeconds(), mediumLimit.getSeconds()};
		Arrays.sort(intervals);
		long longTime = (long) (intervals[0] * 1000);
		return longTime;
	}
	
	//checks if a user exists in the database and returns his id, exception if not found
	/* @author Jonas Picker */
	private static int checkUser(UserDto userEmail) throws UserDoesNotExistException {
		try {
			UserDto completeUser = UserDao.readUserByEmail(userEmail);
			int id = completeUser.getId();
			return id;
		} catch (EntityInstanceDoesNotExistException noUser) {
			String errorMessage = "User wasn't found in DB during lending of copy.";
			Logger.severe(errorMessage);
			throw new UserDoesNotExistException(errorMessage, noUser);
		} 
	}

	/**
	 * Registers that a specific medium-copy has been returned by a specific user.
	 * It is then available to other users for check-out.
	 *
	 * @param copyDto A DTO container with a signature that refers to the
	 *                medium-copy.
	 * @param userDto A DTO container with an ID that refers to the user.
	 * @throws CopyDoesNotExistException Is thrown when the either the
	 *                                   medium-copy has invalid status
	 *                                   or when the copy doesn't
	 *                                   exist in the data store.
	 * @throws UserDoesNotExistException if the User wasn't found in the 
	 * 									 database or he hasn't lent the copy
	 * @author Jonas Picker
	 */
	public static void returnCopy(CopyDto signatureContainer, UserDto userEmail) throws UserDoesNotExistException, CopyDoesNotExistException {
		Connection conn = getConnection();										//ConnectionPool verbessert?
		String signature = signatureContainer.getSignature();
		int userId = checkUser(userEmail);
		if (!copySignatureExists(signatureContainer)) {
			String msg = "Error during copy return! Signature doesn't exist.";
			Logger.severe(msg);
			throw new CopyDoesNotExistException(msg);
		} else if (invalidCopyStatusReturnAttempt(signatureContainer)) {
			String msg = "Error during copy return! Copy wasn't lent in the first place.";
			Logger.severe(msg);
			throw new CopyDoesNotExistException(msg);
		} else if (invalidActorReturnAttempt(signatureContainer, userEmail)) {          
			String msg = "Copy couldn't be returned. The copy wasn't lent (by this user).";
			Logger.severe(msg);
			throw new UserDoesNotExistException(msg);
		} 
		try {
			PreparedStatement returnCopy = conn.prepareStatement(
					"UPDATE mediumCopy SET status = CAST('AVAILABLE' AS copyStatus), actor = null, deadline = null WHERE ((signature = ?) AND (actor = ?) AND (status = CAST('LENT' AS copyStatus)));"
					);
			returnCopy.setString(1, signature);
			returnCopy.setInt(2, userId);
			int updated = returnCopy.executeUpdate();
			conn.commit();
			Logger.development(updated + " copy was returned and became available.");
			returnCopy.close();
		} catch (SQLException e) {
			String msg = "An Error occured during communication with database on copy returning process.";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}	
	}
	
	/**
	 * 
	 * @param attributes
	 * 
	 * @author Jonas Picker
	 */
	public static void updateGlobalAttributes(Collection<AttributeDto> attributes) {
		// TODO: MS2 von Jonas
	}
	
	/**
	 * 
	 * 
	 * @author Jonas Picker
	 */
	public static void updateMarkedCopies() {
		// TODO: MS3 von Jonas
	}
	
	/**
	 * 
	 * 
	 * @author Jonas Picker
	 */
	public static List<MediumCopyAttributeUserDto> readDueDateReminders() {
		// TODO: MS3 von Jonas
		return null;
	}
}