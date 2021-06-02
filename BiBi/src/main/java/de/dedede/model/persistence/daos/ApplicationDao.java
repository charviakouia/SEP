package de.dedede.model.persistence.daos;

import de.dedede.model.data.dtos.ApplicationDto;
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

	private ApplicationDao() {}

	private static Connection getConnection() throws LostConnectionException, MaxConnectionsException {
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().fetchConnection();
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
	 * as represented by an ApplicationDto object. The enclosed ID must not
	 * be in use. Otherwise, an exception is thrown.
	 *
	 * @param appDTO A DTO container with global application data to be written.
	 * @throws EntityInstanceNotUniqueException Is thrown if the supplied 
	 * 		ID is already in use in the data store.
	 * @see ApplicationDto
	 */
	public static void createCustomization(ApplicationDto appDTO)
			throws MaxConnectionsException, LostConnectionException {
		Connection conn = getConnection();
		try {
			PreparedStatement createStmt = conn.prepareStatement(
					"INSERT INTO Application (bibName, emailRegEx, contactInfo, imprintInfo, privacyPolicy, " +
							"bibLogo, globalLendLimit, globalMarkingLimit, reminderOffset, registrationStatus," +
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
			ConnectionPool.getInstance().releaseConnection(conn);
			throw new LostConnectionException(msg);
		}
		ConnectionPool.getInstance().releaseConnection(conn);
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
	 * The enclosed ID must be present in the data store. Otherwise, an
	 * exception is thrown.
	 *
	 * @param appDTO A DTO container with the ID of the global application 
	 * 		data to be read.
	 * @throws EntityInstanceDoesNotExistException Is thrown if the supplied
	 * 		ID does not identify a data entry in the data store.
	 * @return A DTO container with global application data identified
	 * 		by the passed ID.
	 * @see ApplicationDto
	 */
	public static ApplicationDto readCustomization(ApplicationDto appDTO) 
			throws EntityInstanceDoesNotExistException {
		return null;
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
