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
import java.util.List;

import org.postgresql.util.PGInterval;

import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.data.dtos.UserSearchColumn;
import de.dedede.model.data.dtos.UserSearchDto;
import de.dedede.model.logic.util.LendingProcessUserStatus;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;
import de.dedede.model.persistence.util.Pagination;

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
	 * Enters new user data into the persistent data store. The enclosed email
	 * address must not be associated with an existing user in the data store.
	 * Otherwise, an exception is thrown.
	 *
	 * @param userDto A DTO container with the new user data.
	 * @author Ivan Charviakou
	 */
	public static void createUser(UserDto userDto) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement createStmt = conn
					.prepareStatement("INSERT INTO Users (emailAddress, passwordHashSalt, passwordHash, name, "
							+ "surname, postalCode, city, street, houseNumber, token, tokenCreation, userLendPeriod, "
							+ "lendStatus, verificationStatus, userRole) VALUES "
							+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS INTERVAL), "
							+ "CAST(? AS userlendstatus), CAST(? AS userverificationstatus), "
							+ "CAST(? AS userrole));", Statement.RETURN_GENERATED_KEYS);
			populateStatement(createStmt, userDto);
			int numAffectedRows = createStmt.executeUpdate();
			if (numAffectedRows > 0) {
				attemptToInsertGeneratedKey(userDto, createStmt);
			}
			conn.commit();
		} catch (SQLException e) {
			String msg = "Database error occurred while creating user entity with id: " + userDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static void attemptToInsertGeneratedKey(UserDto userDto, Statement stmt) throws SQLException {
		ResultSet resultSet = stmt.getGeneratedKeys();
		if (resultSet.next()) {
			userDto.setId(Math.toIntExact(resultSet.getLong(1)));
		}
	}

	private static PGInterval toPGInterval(Duration duration) {
		PGInterval result = new PGInterval();
		if (duration == null) {
			result.setSeconds(0);
		} else {
			result.setSeconds(duration.getSeconds());
		}
		return result;
	}

	public static UserDto readUserByToken(UserDto userDto) throws UserDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			UserDto result = readUserByTokenHelper(conn, userDto);
			conn.commit();
			if (result != null) {
				return result;
			} else {
				String msg = "A medium does not exist with token: " + userDto.getToken().getContent();
				Logger.severe(msg);
				throw new UserDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while reading application entity with id: " + userDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	public static boolean userExists(UserDto userDto) {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			return userExistsByEmail(conn, userDto);
		} catch (SQLException e) {
			String msg = "Database error occurred while reading user entities";
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static boolean userExistsByEmail(Connection conn, UserDto userDto) throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE WHEN (SELECT COUNT(userid) FROM Users WHERE emailaddress = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setString(1, userDto.getEmailAddress());
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
	}

	private static UserDto readUserByTokenHelper(Connection conn, UserDto userDto) throws SQLException {
		PreparedStatement readStmt = conn.prepareStatement("SELECT userid, emailaddress, passwordhashsalt, "
				+ "passwordhash, surname, name, postalcode, city, street, "
				+ "housenumber, token, tokencreation, userlendperiod, " + "lendstatus, verificationstatus, userrole "
				+ "FROM Users " + "WHERE token = ?;");
		readStmt.setString(1, userDto.getToken().getContent());
		ResultSet resultSet = readStmt.executeQuery();
		if (resultSet.next()) {
			populateUserDto(resultSet, userDto);
			userDto.setId(Math.toIntExact(resultSet.getLong(1)));
			return userDto;
		} else {
			return null;
		}
	}

	/**
	 * Used to enable or disable all registered users lending rights.
	 * 
	 * @param status either en- or disable lending.
	 * @return the number of rows affected by the update.
	 * @author Jonas Picker
	 */
	public static int setLendStatusOnRegisteredUsers(UserLendStatus status) {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		int affected = 0;
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE users " 
							+ "SET lendstatus = CAST(? AS userlendstatus) "
							+ "WHERE userrole = CAST('REGISTERED' AS userrole);"
							);
			String statusString = status.toString();
			stmt.setString(1, statusString);
			affected = stmt.executeUpdate();
			conn.commit();
			stmt.close();
		} catch (SQLException e) {
			String message = "A db communication error occured while " 
								+ "trying to disable user lend status, "
								+ "attempting rollback..";
			Logger.severe(message);
			try {
				conn.rollback();
			} catch (SQLException e2) {
				String msg1 = "Failed to rollback database transaction";
				Logger.severe(msg1);
				throw new LostConnectionException(msg1);
			}
			throw new LostConnectionException(message, e);
		} finally {
			instance.releaseConnection(conn);
		}
		return affected;
	}

	/**
	 * Checks if the user email exists and then if the user is allowed to lend
	 * copies.
	 * 
	 * @param email the users email in a dto.
	 * @return the status in an enum capsule.
	 * @author Jonas Picker
	 */
	public static LendingProcessUserStatus validateUserLending(UserDto email) {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		LendingProcessUserStatus status = null;
		try {
			if (userEntityWithEmailExists(conn, email)) {
				UserDto userId = new UserDto();
				userId.setId(getUserIdByEmail(conn, email));
				UserDto userData = readUserForProfileHelper(conn, userId);
				if (userData.getUserLendStatus() == UserLendStatus.DISABLED) {
					status = LendingProcessUserStatus.USER_CANNOT_LEND;
				}
			} else {
				status = LendingProcessUserStatus.EMAIL_NOT_FOUND;
			}
		} catch (SQLException e) {
			String message = "Error while validating userEmail for lending, "
					+ "attempting rollback..";
			Logger.development(message);
			try {
				conn.rollback();
			} catch (SQLException e2) {
				String msg1 = "Failed to rollback database transaction";
				Logger.severe(msg1);
				throw new LostConnectionException(msg1);
			}
			throw new LostConnectionException(message, e);
		} catch (UserDoesNotExistException impossible) {
			Logger.development("An unexpected error occured during user lending"
					+ " validation, the user was deleted during transaction");
		} finally {
			instance.releaseConnection(conn);
		}
		return status;
	}

	/**
	 * Reads an existing user by id and retrieves his token or sets a newly
	 * generated one if it expired
	 * 
	 * @param user  in a userDto
	 * @param token the new token to be set if the old one expired
	 * @return The token that was set or retrieved as String
	 * @throws LostConnectionException, MaxConnectionsException
	 * 
	 * @author Jonas Picker
	 */
	public static TokenDto setOrRetrieveUserToken(UserDto user, TokenDto token) 
			throws UserDoesNotExistException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (userTokenIsNull(conn, user) || userTokenExpired(conn, user)) {
				PreparedStatement updateToken = conn.prepareStatement(
						"UPDATE users SET tokenCreation = CURRENT_TIMESTAMP," 
								+ " token = ? WHERE userid = ?;");
				updateToken.setString(1, token.getContent());
				updateToken.setInt(2, user.getId());
				int changed = updateToken.executeUpdate();
				Logger.development(changed + " usertoken was newly generated");
				conn.commit(); // Ivan
				updateToken.close();
				token.setCreationTime(LocalDateTime.now());
				
				return token;
			} else {
				PreparedStatement getToken = conn.prepareStatement(
							"SELECT token, tokenCreation " 
								+ "FROM users WHERE userid = ?;");
				getToken.setInt(1, user.getId());
				ResultSet rs = getToken.executeQuery();
				rs.next();
				String content = rs.getString(1);
				LocalDateTime creation = rs.getTimestamp(2).toLocalDateTime();
				conn.commit(); // Ivan
				getToken.close();
				TokenDto result = new TokenDto();
				result.setContent(content);
				result.setCreationTime(creation);
				
				return result;
			}
		} catch (SQLException e) {
			String message = "SQLException while checking or " 
						+ "setting valid user token, attempting rollback..";
			Logger.development(message);
			try {
				conn.rollback();
			} catch (SQLException e2) {
				String msg1 = "Failed to rollback database transaction";
				Logger.severe(msg1);
				throw new LostConnectionException(msg1);
			}
			throw new LostConnectionException(message, e);
		} finally {
			instance.releaseConnection(conn);
		}
	}

	// reads an existing user by id and checks if his token expired but will
	// falsely return false if token is null
	/* @author Jonas Picker */
	private static boolean userTokenExpired(Connection conn, UserDto userId) 
			throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE WHEN (CAST((SELECT tokenCreation FROM users WHERE"
					+ " userid = ?) AS TIMESTAMP) + INTERVAL '30 minutes')"
					+ " < (CURRENT_TIMESTAMP) THEN true ELSE false END " 
					+ "AS tokenExpired;");
		checkingStmt.setInt(1, userId.getId());
		ResultSet rs = checkingStmt.executeQuery();
		conn.commit();
		rs.next();
		boolean result = rs.getBoolean(1);
		checkingStmt.close();
		
		return result;
	}

	// reads an existing user by id and checks if his token is null
	/* @author Jonas Picker */
	private static boolean userTokenIsNull(Connection conn, UserDto userId) 
			throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE WHEN (SELECT tokenCreation FROM users WHERE "
						+ "userid = ?) IS NULL THEN true "
						+ "ELSE false END AS tokenIsNull;");
		checkingStmt.setInt(1, userId.getId());
		ResultSet rs = checkingStmt.executeQuery();
		conn.commit();
		rs.next();
		boolean result = rs.getBoolean(1);
		checkingStmt.close();
		
		return result;
	}

	/**
	 * Fetches user data associated with a given email. The email address must 
	 * be associated with a user in the data store. Otherwise, an exception is 
	 * thrown.
	 *
	 * @param userDto A DTO container with the email address that identifies the
	 *                user to be fetched.
	 * @return A DTO container with the fetched user.
	 * @throws UserDoesNotExistException Is thrown when the enclosed email 
	 * 									 address isn't associated with an 
	 * 									 existing data entry.
	 * 
	 * @author Jonas Picker, but re-uses @author Sergei Pravdins's Code
	 */
	public static UserDto readUserByEmail(UserDto userDto) 
			throws UserDoesNotExistException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		int id = getUserIdByEmail(conn, userDto);
		userDto.setId(id);
		try {
			userDto.setId(id);
			UserDto completeUser = readUserForProfileHelper(conn, userDto);
			
			return completeUser;
		} catch (SQLException e) {
			String message = "Database error occurred while reading user entity" 
								+ " with id: " + userDto.getId();
			Logger.severe(message);
			try {
				conn.rollback();
			} catch (SQLException e2) {
				String msg1 = "Failed to rollback database transaction";
				Logger.severe(msg1);
				throw new LostConnectionException(msg1);
			}
			throw new LostConnectionException(message, e);
		} finally {
			instance.releaseConnection(conn);
		}
	}
	
	/**
	 * Checks if a user exists in the database and returns his id, public helper
	 * method, since outside access is required.
	 * 
	 * @param conn      the connection the operations are performed on
	 * @param userEmail the container for the users email in a dto.
	 * @return the user id for the corresponding email
	 * @throws UserDoesNotExistException if the user email wasn't found
	 * @author Jonas Picker
	 */
	public static int getUserIdByEmail(Connection conn, UserDto userEmail) 
			throws UserDoesNotExistException {
		try {
			PreparedStatement readUserId = conn.prepareStatement(
					"SELECT userId FROM users WHERE emailAddress = ?;"
					);
			readUserId.setString(1, userEmail.getEmailAddress());
			ResultSet rs = readUserId.executeQuery();
			rs.next();
			
			return rs.getInt(1);
		} catch (SQLException e) {
			Logger.development("UserId couldn't be retrieved for this email.");
			throw new UserDoesNotExistException(
					"Specified email " + userEmail.getEmailAddress() 
					+ " doesn't seem to match any user entry", e);
		}
	}

	/**
	 * Search for users in the system.
	 * 
	 * @param userSearch The search parameters.
	 * @param pagination The container of current and total page number.
	 * @return The list of users found by the search.
	 */
	public static List<UserDto> searchUsers(UserSearchDto userSearch, PaginationDto<UserSearchColumn> pagination) {

		// @Task split search term by space

		final var connection = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);

		try {

			final var statementBody = """
					from
						users u
					where
						(  position(lower(?) in lower(u.emailaddress)) > 0
						or position(lower(?) in lower(u.name)) > 0
						or position(lower(?) in lower(u.surname)) > 0)
							and
						u.lendstatus = ?::userlendstatus
					""";

			{
				final var countStatement = connection
						.prepareStatement("select count(distinct u.userid) " + statementBody);
				var parameterIndex = 0;

				while (parameterIndex < 3) {
					countStatement.setString(parameterIndex += 1, userSearch.getSearchTerm());
				}

				countStatement.setString(parameterIndex += 1, userSearch.getLendStatus().name());

				final var resultSet = countStatement.executeQuery();
				resultSet.next();

				Pagination.update(pagination, resultSet.getInt(1));
			}

			final var itemsStatement = connection.prepareStatement("""
					select distinct
						u.userid, u.emailaddress, u.name, u.surname, u.userrole
					%s
					%s
					offset ?
					limit ?
					""".formatted(statementBody,
					Pagination.translateSortingInfoToSQL(pagination, column -> switch (column) {
					case EMAIL_ADDRESS -> "u.emailaddress";
					case FIRST_NAME -> "u.name";
					case LAST_NAME -> "u.surname";
					case ROLE -> "u.userrole";
					})));
			var parameterIndex = 0;

			while (parameterIndex < 3) {
				itemsStatement.setString(parameterIndex += 1, userSearch.getSearchTerm());
			}

			itemsStatement.setString(parameterIndex += 1, userSearch.getLendStatus().name());

			itemsStatement.setInt(parameterIndex += 1, Pagination.calculatePageOffset(pagination));
			itemsStatement.setInt(parameterIndex += 1, Pagination.getEntriesPerPage());

			final var resultSet = itemsStatement.executeQuery();
			final var results = new ArrayList<UserDto>();

			while (resultSet.next()) {

				final var user = new UserDto();

				user.setId(resultSet.getInt(1));
				user.setEmailAddress(resultSet.getString(2));
				user.setFirstName(resultSet.getString(3));
				user.setLastName(resultSet.getString(4));
				user.setRole(UserRole.valueOf(resultSet.getString(5)));

				results.add(user);
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

			final var message = "Database error occurred while searching for users: " + exeption.getMessage();
			Logger.severe(message);

			throw new LostConnectionException(message, exeption);

		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}

	}

	/**
	 * Fetches user data associated with a given ID. The ID must be existed in the
	 * data store. Otherwise, an exception is thrown.
	 *
	 * @param userDto A DTO container with the ID that identifies the user to be
	 *                fetched.
	 * @return A DTO container with the fetched user.
	 * @throws UserDoesNotExistException Is thrown when the enclosed ID does not
	 *                                   exist in the data store.
	 */
	public static UserDto readUserForProfile(UserDto userDto) throws UserDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			return readUserForProfileHelper(conn, userDto);
		} catch (SQLException e) {
			String message = "Database error occurred while reading user entity with id: " + userDto.getId();
			Logger.severe(message);
			throw new LostConnectionException(message, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	/**
	 * Overwrites user data associated with a given email. The email address must be
	 * associated with a user in the data store. Otherwise, an exception is thrown.
	 *
	 * @param userDto A DTO container with the overwriting user data.
	 * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed email
	 * 		address isn't associated with an existing data entry.
	 * @author Ivan Charviakou
	 */
	public static void updateUser(UserDto userDto) throws EntityInstanceDoesNotExistException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			PreparedStatement updateStmt = conn.prepareStatement("UPDATE Users "
					+ "SET emailaddress = ?, passwordhashsalt = ?, passwordhash = ?, name = ?, surname = ?, "
					+ "postalcode = ?, city = ?, street = ?, housenumber = ?, token = ?, tokencreation = ?, "
					+ "userlendperiod = CAST(? AS INTERVAL), lendstatus = CAST(? AS userlendstatus), "
					+ "verificationstatus = CAST(? AS userverificationstatus), userrole = CAST(? AS userrole) "
					+ "WHERE userid = ?;");
			populateStatement(updateStmt, userDto);
			updateStmt.setLong(16, userDto.getId());
			int numAffectedRows = updateStmt.executeUpdate();
			conn.commit();
			if (numAffectedRows == 0) {
				String msg = String.format("No entity with the id: %d exists", userDto.getId());
				Logger.severe(msg);
				throw new EntityInstanceDoesNotExistException(msg);
			}
		} catch (SQLException e) {
			String msg = "Database error occurred while updating user entity with id: " + userDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static void populateStatement(PreparedStatement stmt, UserDto userDto) throws SQLException {
		stmt.setString(1, userDto.getEmailAddress());
		if (!userDto.getPasswordHash().isEmpty()) {
			stmt.setString(2, userDto.getPasswordSalt());
			stmt.setString(3, userDto.getPasswordHash());
		}
		stmt.setString(4, userDto.getLastName());
		stmt.setString(5, userDto.getFirstName());
		stmt.setString(6, String.valueOf(userDto.getZipCode()));
		stmt.setString(7, userDto.getCity());
		stmt.setString(8, userDto.getStreet());
		stmt.setString(9, userDto.getStreetNumber());
		TokenDto tokenDto = userDto.getToken();
		stmt.setString(10, (tokenDto == null ? null : tokenDto.getContent()));
		stmt.setTimestamp(11, (tokenDto == null || tokenDto.getCreationTime() == null ? null
				: Timestamp.valueOf(tokenDto.getCreationTime())));
		stmt.setObject(12, toPGInterval(userDto.getLendingPeriod()));
		stmt.setString(13, userDto.getUserLendStatus().toString());
		stmt.setString(14, userDto.getUserVerificationStatus().name());
		stmt.setString(15, userDto.getRole().name());
	}

	/**
	 * Deletes user data associated with a given email address. The email address
	 * must be associated with a user in the data store. Otherwise, an exception is
	 * thrown.
	 *
	 * @param userDto A DTO container with an email address identifying the user to
	 *                be deleted.
	 * @return A DTO container with data from the deleted user.
	 * @throws UserDoesNotExistException Is thrown when the enclosed email address
	 *                                   isn't associated with an existing data
	 *                                   entry.
	 */
	public static UserDto deleteUser(UserDto userDto)
			throws UserDoesNotExistException, LostConnectionException, MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			if (userEntityExists(conn, userDto)) {
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

	public static boolean userEntityWithEmailExists(UserDto userDto) throws LostConnectionException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(ACQUIRING_CONNECTION_PERIOD);
		try {
			return userEntityWithEmailExists(conn, userDto);
		} catch (SQLException e) {
			String msg = "Database error occurred while reading user entity with id: " + userDto.getId();
			Logger.severe(msg);
			throw new LostConnectionException(msg, e);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	private static boolean userEntityWithEmailExists(Connection conn, UserDto userDto) throws SQLException {
		PreparedStatement checkingStmt = conn.prepareStatement(
				"SELECT CASE " + "WHEN (SELECT COUNT(userid) FROM Users WHERE emailAddress = ?) > 0 THEN true "
						+ "ELSE false " + "END AS entityExists;");
		checkingStmt.setString(1, userDto.getEmailAddress());
		ResultSet resultSet = checkingStmt.executeQuery();
		resultSet.next();
		return resultSet.getBoolean(1);
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
		PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM Users " + "WHERE userid = ?;");
		deleteStmt.setInt(1, Math.toIntExact(userDto.getId()));
		deleteStmt.executeUpdate();
		conn.commit();
	}

	/**
	 * @author Sergei Pravdin
	 */
	public static UserDto readUserForProfileHelper(Connection conn, UserDto userDto)
			throws SQLException, UserDoesNotExistException {
		PreparedStatement readStmt = conn.prepareStatement("SELECT userid, emailaddress, passwordhashsalt,"
				+ " passwordhash, surname, name, postalcode, city, street,"
				+ " housenumber, token, tokencreation, userlendperiod," + "lendstatus, verificationstatus, userrole "
				+ "FROM Users " + "WHERE userid = ?");
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
		long lendingPeriod = Math.round(((PGInterval) resultSet.getObject(13)).getSeconds());
		userDto.setLendingPeriod(Duration.ofSeconds(lendingPeriod));
		UserLendStatus userLendStatus = UserLendStatus.valueOf(resultSet.getString(14));
		userDto.setUserLendStatus(userLendStatus);
		UserVerificationStatus verificationStatus = UserVerificationStatus.valueOf(resultSet.getString(15));
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
		Timestamp timestamp = resultSet.getTimestamp(12);
		LocalDateTime localDateTime = (timestamp == null ? null : timestamp.toLocalDateTime());
		tokenDto.setCreationTime(localDateTime);
		return tokenDto;
	}

}
