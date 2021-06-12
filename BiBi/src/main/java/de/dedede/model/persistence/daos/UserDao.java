package de.dedede.model.persistence.daos;


import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
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
	 * Reads an existing user by id and retrieves his token or sets a newly 
	 * generated one if it expired
	 * 
	 * @param user in a userDto
	 * @param token the new token to be set if the old one expired
	 * @return The token that was set or retrieved as String
	 * @throws LostConnectionException, MaxConnectionsException
	 * 
	 *  @author Jonas Picker
	 */
	public static TokenDto setOrRetrieveUserToken(UserDto user, TokenDto token)
			throws UserDoesNotExistException, LostConnectionException,
					MaxConnectionsException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (userTokenIsNull(conn, user) || userTokenExpired(conn, user)) {
				PreparedStatement updateToken = conn.prepareStatement(
						"UPDATE users SET tokenCreation = CURRENT_TIMESTAMP,"
						+ " token = ? WHERE userid = ?;"
						);
				updateToken.setString(1, token.getContent());
				updateToken.setInt(2, user.getId());
				int changed = updateToken.executeUpdate();
				Logger.development(changed + " usertoken was newly generated");
				updateToken.close();
				token.setCreationTime(LocalDateTime.now());
				return token;
			} else {
				PreparedStatement getToken = conn.prepareStatement(
						"SELECT token, tokenCreation "
						+ "FROM users WHERE userid = ?;"
						);
				getToken.setInt(1, user.getId());
				ResultSet rs = getToken.executeQuery();
				rs.next();
				String content = rs.getString(1);
				LocalDateTime creation = rs.getTimestamp(2).toLocalDateTime();
				getToken.close();
				TokenDto result = new TokenDto();
				result.setContent(content);
				result.setCreationTime(creation);
				return result;
			}
		} catch (SQLException e) {
			String message = "SQLException while checking or "
					+ "setting valid user token";
			Logger.development(message);
			throw new LostConnectionException(message, e);
		}
		
	}
	
	//reads an existing user by id and checks if his token expired but will 
	//falsely return false if token is null
	/* @author Jonas Picker */
	private static boolean userTokenExpired(Connection conn, UserDto userId) 
			throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE WHEN (CAST((SELECT tokenCreation FROM users WHERE"
				+ " userid = ?) AS TIMESTAMP) + INTERVAL '30 minutes')"
				+ " < (CURRENT_TIMESTAMP) THEN true ELSE false END "
				+ "AS tokenExpired;"
				);
		checkingStmt.setInt(1, userId.getId());
		ResultSet rs = checkingStmt.executeQuery();
		rs.next();
		boolean result = rs.getBoolean(1);
		checkingStmt.close();
		return result;
	}
	//reads an existing user by id and checks if his token is null
	/* @author Jonas Picker */
	private static boolean userTokenIsNull(Connection conn, UserDto userId) 
			throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE WHEN (SELECT tokenCreation FROM users WHERE "
				+ "userid = ?) IS NULL THEN true ELSE false END AS tokenIsNull;"
				);
			checkingStmt.setInt(1, userId.getId());
			ResultSet rs = checkingStmt.executeQuery();
			rs.next();
			boolean result = rs.getBoolean(1);
			checkingStmt.close();
			return result;
	}

	/**
	 * Fetches user data associated with a given email. The email address
	 * must be associated with a user in the data store. Otherwise,
	 * an exception is thrown.
	 *
	 * @param userDto A DTO container with the email address that identifies the
	 *                user to be fetched.
	 * @return A DTO container with the fetched user.
	 * @throws UserDoesNotExistException Is thrown when the enclosed
	 *                                   email address isn't associated with an 
	 *                                   existing data entry.
	 *                                             
	 * @author Jonas Picker, but re-uses @author Sergei Pravdins's Code
	 */
	public static UserDto readUserByEmail(UserDto userDto)
			throws UserDoesNotExistException, MaxConnectionsException, 
			LostConnectionException {
		int id = getUserIdByEmail(userDto);	
		userDto.setId(id);
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			UserDto completeUser = readUserForProfileHelper(conn, userDto);
			return completeUser;
		} catch (SQLException e){
			String message = "Database error occurred while reading user entity"
					+ " with id: " + userDto.getId();
			Logger.severe(message);
			throw new LostConnectionException(message, e);
		} finally {
			instance.releaseConnection(conn);
		}
	}


	public static List<UserDto> readUsersBySearchCriteria() {
		return null;

	}
	
	//checks if a user exists in the database and returns his id, exception 
	//if not found.
	/* @author Jonas Picker */
	public static int getUserIdByEmail(UserDto userEmail) 
			throws UserDoesNotExistException  {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
			try {
			PreparedStatement readUserId = conn.prepareStatement("SELECT userId"
					+ " FROM users WHERE emailAddress = ?;");
			readUserId.setString(1, userEmail.getEmailAddress());
			ResultSet rs = readUserId.executeQuery();
			rs.next();
			int id = rs.getInt(1);
			return id;
		} catch (SQLException e) {
			Logger.development("UserId couldn't be retrieved for this email.");
			throw new UserDoesNotExistException("Specified email doesn't seem"
					+ " to match any user entry");
		} finally {
			instance.releaseConnection(conn);
		} 
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
	
	@Deprecated
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
						"WHERE userid = ?");
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
