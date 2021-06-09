package de.dedede.model.persistence.daos;


import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;
import org.postgresql.util.PGInterval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


/**
 * This DAO (data access object) manages data and privileges for users, who are
 * identified by their email addresses. In particular, this DAO implements the
 * CRUD operations (create, read, update, delete).
 * <p>
 * See the {@link UserDto} class for the used DTO.
 */
public final class UserDao {

	private static final long ACQUIRING_CONNECTION_PERIOD = 5000;

	private UserDao() {
	}

	/**
	 * Enters new user data into the persistent data store.
	 * The enclosed email address must not be associated with an existing
	 * user in the data store. Otherwise, an exception is thrown.
	 *
	 * @param userDto A DTO container with the new user data.
	 * @throws EntityInstanceNotUniqueException Is thrown when the enclosed ID
	 *                                          is already associated with an existing data entry.
	 */
	public static void createUser(UserDto userDto)
			throws EntityInstanceNotUniqueException {
	}

	/**
	 * Fetches user data associated with a given email. The email address
	 * must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 *
	 * @param userDto A DTO container with the email address that identifies the
	 *                user to be fetched.
	 * @return A DTO container with the fetched user.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed
	 *                                             email address isn't associated with an existing data entry.
	 */
	public static UserDto readUserByEmail(UserDto userDto)
			throws EntityInstanceDoesNotExistException, MaxConnectionsException, LostConnectionException {
		Connection conn = getConnection();
		try {
			return readUserHelper(conn, userDto);
		} catch (SQLException e){
			String msg = "Database error occurred while reading application entity with id: " + userDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}


	public static List<UserDto> readUsersBySearchCriteria() {
		return null;

	}

	/**
	 * Fetches user data associated with a given ID. The ID
	 * must be existed in the data store. Otherwise, an exception is thrown.
	 *
	 * @param userDto A DTO container with the ID that identifies the
	 *                user to be fetched.
	 * @return A DTO container with the fetched user.
	 * @throws UserDoesNotExistException Is thrown when the enclosed
	 *                                      ID does not exist in the data store.
	 */
	public static UserDto readUserForProfile(UserDto userDto) throws UserDoesNotExistException {
		Connection conn = getConnection();
		try {
			return readUserForProfileHelper(conn, userDto);
		} catch (SQLException e){
			String message = "Database error occurred while reading user entity with id: " + userDto.getId();
			Logger.severe(message);
			throw new LostConnectionException(message, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Overwrites user data associated with a given email. The email address
	 * must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 *
	 * @param userDto A DTO container with the overwriting user data.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed
	 *                                             email address isn't associated with an existing data entry.
	 */
	public static void updateUser(UserDto userDto)
			throws EntityInstanceDoesNotExistException {
	}

	/**
	 * Deletes user data associated with a given email address. The email
	 * address must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 *
	 * @param userDto A DTO container with an email address identifying the user
	 *                to be deleted.
	 * @return A DTO container with data from the deleted user.
	 * @throws UserDoesNotExistException Is thrown when the enclosed
	 *                                             email address isn't associated with an existing data entry.
	 */
	public static UserDto deleteUser(UserDto userDto)
			throws UserDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = getConnection();
		try {
			if (userEntityExists(conn, userDto)){
				deleteUserHelper(conn, userDto);
				conn.commit();
				return userDto;
			} else {
				String msg = String.format("No user entity with the id: %d exists", userDto.getId());
				// Logger.severe(msg);
				throw new UserDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while deleting user entity with id: " + userDto.getId();
			// Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

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

	private static UserDto readUserHelper(Connection conn, UserDto userDto)
			throws SQLException {
		PreparedStatement readStmt = conn.prepareStatement(
				"select * from \"users\" where emailaddress = ?;"
		);
		readStmt.setString(1, userDto.getEmailAddress());
		ResultSet resultSet = readStmt.executeQuery();

		if (resultSet.next()){
			// Logger.development("hier i am  in if ResultSet::::...");
			userDto.setId(resultSet.getInt(1));
			userDto.setEmailAddress(resultSet.getString(2));
			userDto.setPasswordSalt(resultSet.getString(3));
			userDto.setPasswordHash(resultSet.getString(4));
			userDto.setFirstName(resultSet.getString(5));
			userDto.setLastName(resultSet.getString(6));
			userDto.setZipCode(resultSet.getInt(10));
			userDto.setCity(resultSet.getString(7));
			userDto.setStreet(resultSet.getString(8));
			userDto.setStreetNumber(resultSet.getString(11));
			conn.commit();
			return userDto;
		} else {
			conn.commit();
			return null;
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static boolean userEntityExists(Connection conn, UserDto userDto) throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(userid) FROM Users WHERE userid = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setInt(1, userDto.getId());
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void deleteUserHelper(Connection conn, UserDto userDto) throws SQLException {
		PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM Users " +
						"WHERE userid = ?;"
		);
		deleteStmt.setInt(1, Math.toIntExact(userDto.getId()));
		deleteStmt.executeUpdate();
		conn.commit();
	}

	/**
	 * @author Sergei Pravdin
	 */
	public static UserDto readUserForProfileHelper(Connection conn, UserDto userDto) throws SQLException, UserDoesNotExistException {
		PreparedStatement readStmt = conn.prepareStatement(
				"SELECT userid, emailaddress, passwordhashsalt," +
						" passwordhash, name, surname, postalcode, city, street," +
						" housenumber, token, tokencreation, userlendperiod," +
						"lendstatus, verificationstatus, userrole " +
						"FROM Users " +
						"WHERE userid = ?)");
		readStmt.setInt(1, Math.toIntExact(userDto.getId()));
		ResultSet resultSet = readStmt.executeQuery();
		if (resultSet.next()) {
			populateUserDto(resultSet, userDto);
			conn.commit();
			return userDto;
		} else {
			conn.commit();
			String msg = "A medium does not exist with id: " + userDto.getId();
			throw new UserDoesNotExistException(msg);
		}
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static void populateUserDto(ResultSet resultSet, UserDto userDto) throws SQLException {
		userDto.setEmailAddress(resultSet.getString(2));
		userDto.setPasswordSalt(resultSet.getString(3));
		userDto.setPasswordHash(resultSet.getString(4));
		userDto.setFirstName(resultSet.getString(5));
		userDto.setLastName(resultSet.getString(6));
		userDto.setZipCode(Integer.parseInt(resultSet.getString(7)));
		userDto.setCity(resultSet.getString(8));
		userDto.setStreet(resultSet.getString(9));
		userDto.setStreetNumber(resultSet.getString(10));
		userDto.setTokenDto(prepareTokenDto(resultSet));
		long lendingPeriod = Math.round(((PGInterval)
				resultSet.getObject(13)).getSeconds());
		userDto.setLendingPeriod(Duration.ofSeconds(lendingPeriod));
		UserLendStatus userLendStatus = UserLendStatus.valueOf
				(resultSet.getString(14));
		userDto.setUserLendStatus(userLendStatus);
		UserVerificationStatus verificationStatus =
				UserVerificationStatus.valueOf(resultSet.getString(15));
		userDto.setUserVerificationStatus(verificationStatus);
		UserRole userRole = UserRole.valueOf(resultSet.getString(16));
		userDto.setUserRole(userRole);
	}

	/**
	 * @author Sergei Pravdin
	 */
	private static TokenDto prepareTokenDto(ResultSet resultSet) throws SQLException {
		TokenDto tokenDto = new TokenDto();
		tokenDto.setContent(resultSet.getString(11));
		LocalDateTime localDateTime = resultSet.getTimestamp(12).toLocalDateTime();
		tokenDto.setCreationTime(localDateTime);
		return tokenDto;
	}
}
