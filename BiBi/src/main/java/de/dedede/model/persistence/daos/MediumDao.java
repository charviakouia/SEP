package de.dedede.model.persistence.daos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import de.dedede.model.data.dtos.*;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;
import org.postgresql.util.PGInterval;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import de.dedede.model.logic.util.AttributeModifiability;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import javax.imageio.ImageIO;

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
	 * with this medium are not entered automatically.
	 *
	 * @param mediumDto						A DTO container with the new medium data
	 * @throws LostConnectionException		Is thrown when the insertion operation
	 * 										couldn't be completed due to a faulty connection
	 * @see MediumDto
	 */
	public static void createMedium(MediumDto mediumDto) throws LostConnectionException {
		Connection conn = getConnection();
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Medium (mediumLendPeriod, hasCategory) VALUES " +
							"(CAST(? AS INTERVAL), ?);",
					Statement.RETURN_GENERATED_KEYS
			);
			populateMediumStatement(createStmt, mediumDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0){ attemptToInsertGeneratedKey(mediumDto, createStmt); }
			createAttributes(conn, mediumDto, mediumDto.getAttributes().values());
			conn.commit();
		} catch (SQLException e){
			String msg = "Database error occurred while creating medium entity with id: " + mediumDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} catch (IOException e) {
			String msg = "Error occurred while writing byte array for an image";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static void populateMediumStatement(PreparedStatement stmt, MediumDto mediumDto) throws SQLException {
		stmt.setObject(1, toPGInterval(mediumDto.getReturnPeriod()));
		stmt.setInt(2, mediumDto.getCategory().getId());
	}

	private static void createAttributes(Connection conn, MediumDto mediumDto,
			Collection<AttributeDto> attributeDtos) throws SQLException, IOException {
		PreparedStatement createStmt1 = conn.prepareStatement(
				"INSERT INTO CustomAttribute (mediumId, attributeName, attributeValue) VALUES " +
						"(?, ?, ?);",
				Statement.RETURN_GENERATED_KEYS
		);
		PreparedStatement createStmt2 = conn.prepareStatement(
				"INSERT INTO AttributeType (attributeId, mediumId, previewPosition, multiplicity, " +
						"modifiability, attributeDataType) VALUES " +
						"(?, ?, CAST(? AS mediumpreviewposition), CAST(? AS attributemultiplicity), " +
						"CAST(? AS attributemodifiability), CAST(? AS attributedatatype));"
		);
		for (AttributeDto attributeDto : attributeDtos){
			AttributeType type = attributeDto.getType();
			switch (type){
				case LINK:
					for (URL url : attributeDto.getUrlValue()){
						createAttribute(createStmt1, createStmt2, mediumDto, attributeDto, url.toString().getBytes());
					}
					break;
				case TEXT:
					for (String str : attributeDto.getTextValue()){
						createAttribute(createStmt1, createStmt2, mediumDto, attributeDto, str.getBytes());
					}
					break;
				case IMAGE:
					for (Image image : attributeDto.getImageValue()){
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write((BufferedImage) image, "jpg", baos);
						createAttribute(createStmt1, createStmt2, mediumDto, attributeDto, baos.toByteArray());
					}
					break;
			}
		}
	}

	private static void createAttribute(PreparedStatement stmt1, PreparedStatement stmt2,
			MediumDto mediumDto, AttributeDto attributeDto, byte[] bytes) throws SQLException {
		populateAttributeStatements(stmt1, stmt2, mediumDto, attributeDto);
		stmt1.setBytes(3, bytes);
		stmt1.executeUpdate();
		attemptToInsertGeneratedKey(attributeDto, stmt1);
		stmt2.setLong(1, attributeDto.getId());
		stmt2.executeUpdate();
	}

	private static void populateAttributeStatements(PreparedStatement stmt1, PreparedStatement stmt2,
			MediumDto mediumDto, AttributeDto attributeDto) throws SQLException {
		stmt1.setLong(1, mediumDto.getId());
		stmt1.setString(2, attributeDto.getName());
		stmt2.setLong(2, mediumDto.getId());
		stmt2.setString(3, attributeDto.getPosition().toString());
		stmt2.setString(4, attributeDto.getAttributeMultiplicity().toString());
		stmt2.setString(5, attributeDto.getAttributeModifiability().toString());
		stmt2.setString(6, attributeDto.getType().toString());
	}

	private static void attemptToInsertGeneratedKey(MediumDto mediumDto, Statement stmt)
			throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()){
			mediumDto.setId(Math.toIntExact(resultSet.getLong(1)));
		}
	}

	private static void attemptToInsertGeneratedKey(AttributeDto attributeDto, Statement stmt)
			throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()){
			attributeDto.setId(Math.toIntExact(resultSet.getLong(1)));
		}
	}

	/**
	 * Fetches a medium from the persistent data store using an ID. The ID must be
	 * associated with an existing data entry. Otherwise, an exception is thrown.
	 * 
	 * @param mediumDto A DTO container with the ID of the medium data to be fetched
	 * @throws MediumDoesNotExistException Is thrown when the passed ID
	 * 		is not associated with any existing data entry.
	 * @return A DTO container with the medium data referenced by the ID.
	 */
	public static MediumDto readMedium(MediumDto mediumDto)
			throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
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

	/**
	 * Registers that a specific medium-copy has been checked out by a specific
	 * user. Until it is returned, it is unavailable to other users.
	 *
	 * @param copyDto A DTO container with a signature that refers to the
	 *                medium-copy.
	 * @param userDto A DTO container with an ID that refers to the user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the either the
	 *                                             medium-copy is currently checked
	 *                                             out or when either entity doesn't
	 *                                             exist in the data store.
	 * @see CopyDto
	 */
	public static void lendCopy(CopyDto copyDto, UserDto userDto) throws EntityInstanceDoesNotExistException {
		// TODO: MS2 von Jonas
	}

	/**
	 * Registers that a specific medium-copy has been returned by a specific user.
	 * It is then available to other users for check-out.
	 *
	 * @param copyDto A DTO container with a signature that refers to the
	 *                medium-copy.
	 * @param userDto A DTO container with an ID that refers to the user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when either the
	 *                                             medium-copy hasn't been checked
	 *                                             out by the given user or either
	 *                                             entity doesn't exist in the data
	 *                                             store.
	 * @see CopyDto
	 */
	public static void returnCopy(CopyDto copyDto, UserDto userDto) throws EntityInstanceDoesNotExistException {
		// TODO: MS3 von Jonas
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

	public static void updateMarkedCopies() {
		// TODO: MS3 von Jonas
	}

	public static List<MediumCopyAttributeUserDto> readDueDateReminders() {
		// TODO: MS3 von Jonas
		return null;
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
}