package tests;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGInterval;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sergei Pravdin
 */
public class DeleteUserTest {

    private static final long ACQUIRING_CONNECTION_PERIOD = 5000;
    private static final int testID = 99;
    private static final int testID2 = 88;

    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException,
            InvalidConfigurationException, LostConnectionException, MaxConnectionsException {
        PreTest.setUp();
        Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
        try {
            PreparedStatement createStmt = conn.prepareStatement(
                    "INSERT INTO Users (userid, emailaddress, passwordhashsalt," +
                            " passwordhash, name, surname, postalcode, city, street," +
                            " housenumber, token, tokencreation, userlendperiod," +
                            "lendstatus, verificationstatus, userrole) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,CAST (? AS userlendstatus)," +
                            "CAST (? AS userverificationstatus), CAST (? AS userrole))",
                    Statement.RETURN_GENERATED_KEYS
            );
            populateStmt(createStmt);
            createStmt.executeUpdate();
            conn.commit();
        } catch (SQLException e){
            String msg = "Database error occurred while creating testUser";
//			Logger.severe(msg);
            throw new LostConnectionException(msg, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(conn);
        }
    }

    private static void populateStmt(PreparedStatement createStmt)
            throws SQLException {
        createStmt.setInt(1, testID);
        createStmt.setString(2, "testtest");
        createStmt.setString(3, "testtesttest");
        createStmt.setString(4, "testtesttesttest");
        createStmt.setString(5, "aa");
        createStmt.setString(6, "asdasd");
        createStmt.setString(7, "asdasd");
        createStmt.setString(8,"asdasd");
        createStmt.setString(9,"1a");
        createStmt.setString(10, "asdas");
        createStmt.setString(11, "asdad");
        createStmt.setTimestamp(12, null);
        PGInterval interval = new PGInterval();
        interval.setSeconds(30000);
        createStmt.setObject(13, interval);
        createStmt.setString(14, "ENABLED");
        createStmt.setString(15, "VERIFIED");
        createStmt.setString(16, "REGISTERED");
    }

    private static boolean userEntityExists(UserDto userDto) throws SQLException,
            LostConnectionException, MaxConnectionsException {
        Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
        PreparedStatement checkingStmt = conn.prepareStatement(
                "SELECT CASE " + "WHEN (SELECT COUNT(userid) " +
                        "FROM Users WHERE userid = ?) > 0 THEN true "
                        + "ELSE false " + "END AS entityExists;");
        checkingStmt.setInt(1, userDto.getId());
        ResultSet resultSet = checkingStmt.executeQuery();
        resultSet.next();
        conn.commit();
        ConnectionPool.getInstance().releaseConnection(conn);
        return resultSet.getBoolean(1);
    }

    @AfterAll
    public static void tearDown() throws LostConnectionException, MaxConnectionsException,
            SQLException {
        Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
        PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM Users " +
                        "WHERE userid = ?;"
        );
        deleteStmt.setInt(1, testID);
        deleteStmt.executeUpdate();
        conn.commit();
        ConnectionPool.getInstance().releaseConnection(conn);
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void deleteUserTest() throws LostConnectionException, UserDoesNotExistException,
            MaxConnectionsException, SQLException {
        UserDto userDto = new UserDto();
        userDto.setId(testID);
        UserDao.deleteUser(userDto);
        Assertions.assertFalse(userEntityExists(userDto));
    }

    @Test
    public void deleteNonExistentUser() {
        UserDto userDto = new UserDto();
        userDto.setId(testID2);
        Exception exception = assertThrows(UserDoesNotExistException.class, () -> {
            UserDao.deleteUser(userDto);
        });
        String expectedMessage = String.format("No user entity with the id: %d exists",
                userDto.getId());
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}