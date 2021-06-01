package dao;

import dao.query.statements.*;
import models.Location;
import models.User;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Named("sqlUserDatabase")
@ApplicationScoped
public class SqlUserDb {

    private static final String TABLE_NAME = "public.users";

    private static final String ID_COLUMN = "id";
    private static final String USERNAME_COLUMN = "username";
    private static final String PASSWORD_COLUMN = "password";
    private static final String FIRST_NAME_COLUMN = "firstName";
    private static final String LAST_NAME_COLUMN = "lastName";
    private static final String BIRTHDAY_COLUMN = "birthday";
    private static final String CITY_COLUMN = "city";
    private static final String STREET_COLUMN ="street";
    private static final String HOUSE_NUMBER_COLUMN = "houseNumber";

    @Inject
    private DbConnectionProvider connectionDb;

    @PostConstruct
    public void init() throws SQLException {
        createTables();
       // add3Users(); <-this is for hard coded data
    }

    @PreDestroy
    public void closeConnection() {
    	connectionDb.closeConnection();
    }

    private void createTables() throws SQLException {
        Connection connection = connectionDb.getConnection();
        System.out.println("Creating Table");

        Statement stmt = connection.createStatement();

        stmt.execute(new CreateTable(TABLE_NAME)
                .integerCol(ID_COLUMN).primaryKey().notNull().createColumn()
                .varcharCol(USERNAME_COLUMN, 128).notNull().createColumn()
                .varcharCol(PASSWORD_COLUMN, 256).notNull().createColumn()
                .varcharCol(FIRST_NAME_COLUMN, 128).notNull().createColumn()
                .varcharCol(LAST_NAME_COLUMN, 128).notNull().createColumn()
                .dateCol(BIRTHDAY_COLUMN).createColumn()
                .varcharCol(CITY_COLUMN, 128).createColumn()
                .varcharCol(STREET_COLUMN, 128).createColumn()
                .varcharCol(HOUSE_NUMBER_COLUMN, 128).createColumn()
                .createStatement());

        connection.commit();
        connectionDb.releaseConnection();
    }

    public Optional<User> getUser(String username, String password) {
        User user;

        Connection connection = connectionDb.getConnection();

        try {
            user = getUserSQL(username, password, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            user = null;
        }

        connectionDb.releaseConnection();

        return Optional.ofNullable(user);
    }

    private User getUserSQL(String username, String password, Connection connection) throws SQLException {
        System.out.println("Looking for user");
        User user = null;

        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery(new Select()
                .selectAll()
                .from(TABLE_NAME)
                .where(USERNAME_COLUMN, "=", username)
                .createWhere().createStatement());

        if (rst.next()) {
            String dbPassword = rst.getString(PASSWORD_COLUMN);

            if (dbPassword.equals(password)) {
                int id = rst.getInt(ID_COLUMN);

                user = new User(id);

                user.setUsername(rst.getString(USERNAME_COLUMN));
                user.setLastName(rst.getString(LAST_NAME_COLUMN));
                user.setFirstName(rst.getString(FIRST_NAME_COLUMN));
                user.setBirthday(rst.getDate(BIRTHDAY_COLUMN));

                Location location = new Location();

                location.setCity(rst.getString(CITY_COLUMN));
                location.setStreet(rst.getString(STREET_COLUMN));
                location.setHouseNumber(rst.getString(HOUSE_NUMBER_COLUMN));
                user.setLocation(location);
            } else {
                return null;
            }
        }

        return user;
    }

    public boolean patchUser(User user) {
        System.out.println("Updating user");
        boolean success;

        Connection connection = connectionDb.getConnection();

        try {
            success = patchUser(user, connection);
        } catch (SQLException s) {
            s.printStackTrace();
            success = false;
        }

        connectionDb.releaseConnection();

        return success;
    }

    private boolean patchUser(User user, Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();

        if (!userNameAlreadyTakenSQL(user, connection)) {
            Update update = new Update(TABLE_NAME)
                    .column(USERNAME_COLUMN, user.getUsername())
                    .column(FIRST_NAME_COLUMN, user.getFirstName())
                    .column(LAST_NAME_COLUMN, user.getLastName())
                    .column(BIRTHDAY_COLUMN, parseDateToString(user.getBirthday()))
                    .column(CITY_COLUMN, user.getLocation().getCity())
                    .column(STREET_COLUMN, user.getLocation().getStreet())
                    .column(HOUSE_NUMBER_COLUMN, user.getLocation().getHouseNumber())
                    .where(ID_COLUMN, "=", user.getId()).createWhere();

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                update.column(PASSWORD_COLUMN, user.getPassword());
            }

            stmt.executeUpdate(update.createStatement());

            connection.commit();
            return true;
        } else {
            return false;
        }
    }

  /* private void add3Users() throws SQLException {
    	
        Connection connection = connectionDb.getConnection();
        Statement stmt = connection.createStatement();

        String insert1 = new Insert(TABLE_NAME)
                .column(ID_COLUMN, 1)
                .column(USERNAME_COLUMN, "madi")
                .column(PASSWORD_COLUMN, ("123456"))
                .column(FIRST_NAME_COLUMN, "Madi")
                .column(LAST_NAME_COLUMN, "Schreiner")
                .column(CITY_COLUMN, "Passau")
                .column(STREET_COLUMN, "New street")
                .column(HOUSE_NUMBER_COLUMN, 1)
                .column(BIRTHDAY_COLUMN, parseDateToString(new Date()))
                .createStatement();

        String insert2 = new Insert(TABLE_NAME)
                .column(ID_COLUMN, 2)
                .column(USERNAME_COLUMN, "fadi")
                .column(PASSWORD_COLUMN,("123asd"))
                .column(FIRST_NAME_COLUMN, "Fadi")
                .column(LAST_NAME_COLUMN, "Müller")
                .column(CITY_COLUMN, "Londen")
                .column(STREET_COLUMN, "Old street")
                .column(HOUSE_NUMBER_COLUMN, 1)
                .column(BIRTHDAY_COLUMN, parseDateToString(new Date()))
                .createStatement();
        
        String insert3 = new Insert(TABLE_NAME)
                .column(ID_COLUMN, 3)
                .column(USERNAME_COLUMN, "sandi")
                .column(PASSWORD_COLUMN,("123"))
                .column(FIRST_NAME_COLUMN, "Fadi")
                .column(LAST_NAME_COLUMN, "Müller")
                .column(CITY_COLUMN, "Londen")
                .column(STREET_COLUMN, "Old street")
                .column(HOUSE_NUMBER_COLUMN, 1)
                .column(BIRTHDAY_COLUMN, parseDateToString(new Date()))
                .createStatement();


               stmt.execute(insert1);
               stmt.execute(insert2);
               stmt.execute(insert3);

        connection.commit();
        connectionDb.releaseConnection();
}
*/
    public boolean userNameAlreadyTaken(User user) {
        System.out.println("Checking usernames");
        boolean taken;

        Connection connection = connectionDb.getConnection();

        try {
            taken = userNameAlreadyTakenSQL(user, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            taken = false;
        }

        connectionDb.releaseConnection();

        return taken;
    }

    private boolean userNameAlreadyTakenSQL(User user, Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery(new Select()
                .selectAll()
                .from(TABLE_NAME)
                .where(USERNAME_COLUMN, "=", user.getUsername())
                .and(ID_COLUMN, "<>", user.getId())
                .createWhere().createStatement());

        return rst.next();
    }

    public boolean updateUser(User user) {
        if (userNameAlreadyTaken(user)) {
            return false;
        } else {
            return patchUser(user);
        }
    }
    
    private static String parseDateToString(Date d) {
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }
}