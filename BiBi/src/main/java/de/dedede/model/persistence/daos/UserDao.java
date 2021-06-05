package de.dedede.model.persistence.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.Logger;

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
     * @throws EntityInstanceDoesNotExistException Is thrown when the enclosed
     *                                             email address isn't associated with an existing data entry.
     */
    public static UserDto deleteUser(UserDto userDto)
            throws EntityInstanceDoesNotExistException {
        return null;
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
                "select * from users where emailaddress = ?;"
        );
        readStmt.setString(1, userDto.getEmailAddress());
        ResultSet resultSet = readStmt.executeQuery();
        if (resultSet.next()){
            userDto.setId(resultSet.getInt(1));
            userDto.setEmailAddress(resultSet.getString(2));
            userDto.setPasswordSalt(resultSet.getString(3));
            userDto.setPasswordHash(resultSet.getString(4));
            // TODO: füge fehlende attribute hinzu
            conn.commit();
            return userDto;
        } else {
            conn.commit();
            return null;
        }
    }

}
