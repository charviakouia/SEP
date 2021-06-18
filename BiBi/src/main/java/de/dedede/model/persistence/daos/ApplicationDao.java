package de.dedede.model.persistence.daos;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;
import org.postgresql.util.PGInterval;

import java.sql.*;
import java.time.Duration;

/**
 * This DAO (data access object) is responsible for fetching global application
 * data from a persistent data store as well as saving to it.
 * It implements the CRUD methods (create, read, update, delete).
 *
 * See the {@link ApplicationDto} class for the used DTO.
 *
 */
public final class ApplicationDao {

	private static final long ACQUIRING_CONNECTION_PERIOD = 5000;

	private ApplicationDao() {}

	/**
	 * Writes new global application data to the persistent data store,
	 * as represented by an ApplicationDto object.
	 *
	 * @param appDTO A DTO container with global application data to be written.
	 * @throws MaxConnectionsException	Is thrown if no connection was available
	 * 		to carry out the operation.
	 * @throws LostConnectionException	Is thrown if the used connection stopped
	 * 		working correctly before concluding the operation.
	 * @see ApplicationDto
	 */
	public static void createCustomization(ApplicationDto appDTO)
			throws MaxConnectionsException, LostConnectionException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);;
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Application (bibName, emailRegEx, contactInfo, imprintInfo, privacyPolicy, " +
							"bibLogo, globalLendLimit, globalMarkingLimit, reminderOffset, registrationStatus, " +
							"lookAndFeel, ananRights, userLendStatus) VALUES " +
							"(?, ?, ?, ?, ?, ?, CAST(? AS INTERVAL), CAST(? AS INTERVAL), CAST(? AS INTERVAL)," +
							"CAST(? AS systemRegistrationStatus), CAST(? AS systemLookAndFeel)," +
							"CAST(? AS systemAnonRights), CAST(? AS userLendStatus));",
					Statement.RETURN_GENERATED_KEYS
			);
			populateStatement(createStmt, appDTO);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0){ attemptToInsertGeneratedKey(appDTO, createStmt); }
			conn.commit();
		} catch (SQLException e){
			String msg = "Database error occurred while creating application entity with id: " + appDTO.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	/**
	 * Fetches global application data from the persistent data store.
	 * The enclosed ID must be present in the data store. Otherwise, null
	 * is returned.
	 *
	 * @param appDTO A DTO container with the ID of the global application 
	 * 		data to be read.
	 * @throws MaxConnectionsException	Is thrown if no connection was available
	 * 		to carry out the operation.
	 * @throws LostConnectionException	Is thrown if the used connection
	 * 		stopped working correctly before concluding the operation.
	 * @return A DTO container with global application data identified
	 * 		by the passed ID. If the enclosed ID isn't present in the data
	 * 		store, null is returned.
	 * @see ApplicationDto
	 */
	public static ApplicationDto readCustomization(ApplicationDto appDTO)
			throws LostConnectionException, MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);;
		try {
			ApplicationDto result = readCustomizationHelper(conn, appDTO);
			conn.commit();
			return result;
		} catch (SQLException e){
			String msg = "Database error occurred while reading application entity with id: " + appDTO.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Overwrites global application data in the persistent data store. 
	 * The enclosed ID must be present in the data store and is used to 
	 * identify and remove existing data. 
	 *
	 * @param appDTO A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @throws MaxConnectionsException    Is thrown if no connection was available
	 * 		to carry out the operation.
	 * @throws LostConnectionException	Is thrown if the used connection
	 * 		stopped working correctly before concluding the operation.
	 * @see ApplicationDto
	 */
	public static void updateCustomization(ApplicationDto appDTO)
			throws EntityInstanceDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);;
		try {
			PreparedStatement updateStmt = conn.prepareStatement(
					"UPDATE Application " +
							"SET bibName = ?, emailRegEx = ?, contactInfo = ?, imprintInfo = ?, privacyPolicy = ?, " +
							"bibLogo = ?, globalLendLimit = CAST(? AS INTERVAL), " +
							"globalMarkingLimit = CAST(? AS INTERVAL), reminderOffset = CAST(? AS INTERVAL), " +
							"registrationStatus = CAST(? AS systemRegistrationStatus), " +
							"anonRights = CAST(? AS systemAnonRights), " +
							"userLendStatus = CAST(? AS userLendStatus) " +
							"WHERE one = ?;"
			);
			populateStatement(updateStmt, appDTO);
			updateStmt.setLong(13, appDTO.getId());
			int numAffectedRows = updateStmt.executeUpdate();
			conn.commit();
			if (numAffectedRows == 0){
				String msg = String.format("No entity with the id: %d exists", appDTO.getId());
				Logger.severe(msg);
				throw new EntityInstanceDoesNotExistException(msg);
			}
		} catch (SQLException e){
			String msg = "Database error occurred while updating application entity with id: " + appDTO.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	/**
	 * Deletes global application data from the persistent data store that
	 * corresponds to a given ID.
	 *
	 * @param appDTO A DTO container with the ID of the global application 
	 * 		data to be deleted.
	 * @throws EntityInstanceDoesNotExistException	Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @throws MaxConnectionsException    			Is thrown if no connection
	 * 		was available to carry out the operation.
	 * @throws LostConnectionException	Is thrown if the used connection
	 * 		stopped working correctly before concluding the operation.
	 * @return A DTO container with the deleted data store entry.
	 * @see ApplicationDto
	 */
	public static ApplicationDto deleteCustomization(ApplicationDto appDTO)
			throws LostConnectionException, MaxConnectionsException, EntityInstanceDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (customizationEntityExists(conn, appDTO)){
				readCustomizationHelper(conn, appDTO);
				deleteCustomizationHelper(conn, appDTO);
				conn.commit();
				return appDTO;
			} else {
				String msg = String.format("No entity with the id: %d exists", appDTO.getId());
				Logger.severe(msg);
				throw new EntityInstanceDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while deleting application entity with id: " + appDTO.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	// Helper methods:

	private static boolean customizationEntityExists(Connection conn, ApplicationDto appDTO)
			throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " +
						"WHEN (SELECT COUNT(one) FROM Application WHERE one = ?) > 0 THEN true " +
						"ELSE false " +
						"END AS entityExists;"
		);
		checkingStmt.setString(1, String.valueOf(appDTO.getId()));
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	private static ApplicationDto readCustomizationHelper(Connection conn, ApplicationDto appDTO)
			throws SQLException {
		PreparedStatement readStmt = conn.prepareStatement(
				"SELECT one, bibName, emailRegEx, contactInfo, imprintInfo, privacyPolicy, bibLogo, " +
						"globalLendLimit, globalMarkingLimit, reminderOffset, registrationStatus, " +
						"lookAndFeel, anonRights, userLendStatus " +
						"FROM Application " +
						"WHERE one = ?;"
		);
		readStmt.setInt(1, Math.toIntExact(appDTO.getId()));
		ResultSet resultSet = readStmt.executeQuery();
		if (resultSet.next()){
			populateDto(resultSet, appDTO);
			return appDTO;
		} else {
			return null;
		}
	}

	private static void deleteCustomizationHelper(Connection conn, ApplicationDto appDto)
			throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM Application " +
						"WHERE one = ?;"
		);
		deleteStmt.setInt(1, Math.toIntExact(appDto.getId()));
		deleteStmt.executeUpdate();
	}

	private static void attemptToInsertGeneratedKey(ApplicationDto applicationDto, Statement stmt)
			throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()){
			applicationDto.setId(resultSet.getLong(1));
		}
	}

	private static void populateStatement(PreparedStatement stmt, ApplicationDto appDto) throws SQLException {
		stmt.setString(1, appDto.getName());
		stmt.setString(2, appDto.getEmailSuffixRegEx());
		stmt.setString(3, appDto.getContactInfo());
		stmt.setString(4, appDto.getSiteNotice());
		stmt.setString(5, appDto.getPrivacyPolicy());
		stmt.setBytes(6, appDto.getLogo());
		stmt.setObject(7, toPGInterval(appDto.getReturnPeriod()));
		stmt.setObject(8, toPGInterval(appDto.getPickupPeriod()));
		stmt.setObject(9, toPGInterval(appDto.getWarningPeriod()));
		stmt.setString(10, appDto.getSystemRegistrationStatus().toString());
		stmt.setString(11, appDto.getAnonRights().toString());
		stmt.setString(12, appDto.getLendingStatus().toString());
	}

	private static void populateDto(ResultSet resultSet, ApplicationDto appDTO) throws SQLException {
		appDTO.setId(resultSet.getInt(1));
		appDTO.setName(resultSet.getString(2));
		appDTO.setEmailSuffixRegEx(resultSet.getString(3));
		appDTO.setContactInfo(resultSet.getString(4));
		appDTO.setSiteNotice(resultSet.getString(5));
		appDTO.setPrivacyPolicy(resultSet.getString(6));
		appDTO.setLogo(resultSet.getBytes(7));
		long lendingPeriodSeconds = Math.round(((PGInterval) resultSet.getObject(8)).getSeconds());
		appDTO.setReturnPeriod(Duration.ofSeconds(lendingPeriodSeconds));
		long markingPeriodSeconds = Math.round(((PGInterval) resultSet.getObject(9)).getSeconds());
		appDTO.setPickupPeriod(Duration.ofSeconds(markingPeriodSeconds));
		long remindPeriodSeconds = Math.round(((PGInterval) resultSet.getObject(10)).getSeconds());
		appDTO.setWarningPeriod(Duration.ofSeconds(remindPeriodSeconds));
		SystemRegistrationStatus status = SystemRegistrationStatus.valueOf(resultSet.getString(11));
		appDTO.setSystemRegistrationStatus(status);
		appDTO.setAnonRights(SystemAnonAccess.valueOf(resultSet.getString(13)));
		appDTO.setLendingStatus(RegisteredUserLendStatus.valueOf(resultSet.getString(14)));
	}

	private static PGInterval toPGInterval(Duration duration){
		PGInterval result = new PGInterval();
		result.setSeconds(duration.getSeconds());
		return result;
	}

}
