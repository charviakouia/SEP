package tests;

import de.dedede.model.data.dtos.UserDto;
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

import java.sql.SQLException;

/**
 * @author Sergei Pravdin
 */
public class ReadUserForProfileTest {

    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException,
            InvalidConfigurationException, LostConnectionException, MaxConnectionsException {
        PreTest.setUp();
    }

    @AfterAll
    public static void tearDown() throws LostConnectionException, MaxConnectionsException {
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void readUserForProfile() throws UserDoesNotExistException {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        Assertions.assertEquals("defaultAdmin", UserDao.readUserForProfile(userDto).getFirstName());
    }
}