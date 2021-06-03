package de.dedede.model.persistence.daos;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
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

	private static Connection getConnection() throws LostConnectionException, MaxConnectionsException {
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e){
			Logger.severe("Couldn't configure the connection");
			ConnectionPool.getInstance().releaseConnection(conn);
			throw new LostConnectionException("Couldn't configure the connection");
		}
		return conn;
	}

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
		Connection conn = getConnection();
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Application (bibName, emailRegEx, contactInfo, imprintInfo, privacyPolicy, " +
							"bibLogo, globalLendLimit, globalMarkingLimit, reminderOffset, registrationStatus, " +
							"lookAndFeel, ananRights, userLendStatus) VALUES " +
							"(?, ?, ?, ?, ?, ?, CAST(? AS INTERVAL), CAST(? AS INTERVAL), CAST(? AS INTERVAL)," +
							"CAST(? AS systemRegistrationStatus), CAST(? AS systemLookAndFeel)," +
							"CAST(? AS systemAnonRights), CAST(? AS userLendStatus));"
			);
			createStmt.setString(1, appDTO.getName());
			createStmt.setString(2, appDTO.getEmailAddressSuffixRegEx());
			createStmt.setString(3, appDTO.getContactInfo());
			createStmt.setString(4, appDTO.getSiteNotice());
			createStmt.setString(5, appDTO.getPrivacyPolicy());
			createStmt.setBytes(6, appDTO.getLogo());
			createStmt.setObject(7, toPGInterval(appDTO.getReturnPeriod()));
			createStmt.setObject(8, toPGInterval(appDTO.getPickupPeriod()));
			createStmt.setObject(9, toPGInterval(appDTO.getWarningPeriod()));
			createStmt.setString(10, appDTO.getSystemRegistrationStatus().toString());
			createStmt.setString(11, appDTO.getLookAndFeel());
			createStmt.setString(12, appDTO.getAnonRights());
			createStmt.setString(13, appDTO.getLendingStatus());
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0){ attemptToInsertGeneratedKey(appDTO, createStmt); }
			conn.commit();
		} catch (SQLException e){
			String msg = "Database error occurred while creating application entity";
			Logger.severe(msg);
			throw new LostConnectionException(msg);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static void attemptToInsertGeneratedKey(ApplicationDto applicationDto, Statement stmt)
			throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()){
			applicationDto.setId(resultSet.getLong(1));
		}
	}

	private static PGInterval toPGInterval(Duration duration){
		PGInterval result = new PGInterval();
		result.setSeconds(duration.getSeconds());
		return result;
	}

	private static boolean customizationEntityExists(Connection conn, ApplicationDto appDTO) throws SQLException {
		try {
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
		} catch (SQLException e) {
			String msg = "Database error occurred while checking for application entity uniqueness";
			Logger.severe(msg);
			throw e;
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
		Connection conn = getConnection();
		try {
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
				appDTO.setId(resultSet.getInt(1));
				appDTO.setName(resultSet.getString(2));
				appDTO.setEmailAddressSuffixRegEx(resultSet.getString(3));
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
				appDTO.setLookAndFeel(resultSet.getString(12));
				appDTO.setAnonRights(resultSet.getString(13));
				appDTO.setLendingStatus(resultSet.getString(14));
				conn.commit();
				return appDTO;
			} else {
				return null;
			}
		} catch (SQLException e){
			String msg = "Database error occurred while reading application entity";
			Logger.severe(msg);
			throw new LostConnectionException(msg);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Overwrites global application data in the persistent data store. 
	 * The enclosed ID must be present in the data store and is used to 
	 * identify and remove existing data. 
	 *
	 * @param applicationDto A DTO container with the overwriting data
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @see ApplicationDto
	 */
	public static void updateCustomization(ApplicationDto applicationDto) 
			throws EntityInstanceDoesNotExistException {
	}
	
	/**
	 * Deletes global application data from the persistent data store that
	 * corresponds to a given ID.
	 *
	 * @param appDTO A DTO container with the ID of the global application 
	 * 		data to be deleted.
	 * @return A DTO container with the deleted data store entry.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @see ApplicationDto
	 */
	public static ApplicationDto deleteCustomization(ApplicationDto appDTO) {
		return null;
	}

}
