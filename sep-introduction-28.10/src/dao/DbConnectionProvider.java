package dao;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

@ApplicationScoped
public class DbConnectionProvider {

    private Connection connection;

    private boolean connectionLocked;

    private final ResourceBundle bundle = ResourceBundle.getBundle("resources/database");

    @PostConstruct
    public void init() {
        try {
            Class.forName(bundle.getString("databaseDriver"));
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
        }

        openConnection();

        Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnection));
    }

    public synchronized Connection getConnection() {
        while (connectionLocked || connection == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                closeConnection();
                return null;
            }
        }

        connectionLocked = true;

        return connection;
    }

    public synchronized void releaseConnection() {
        try {
            connection.rollback();
        } catch (SQLException exception) {
            closeConnection();
        }

        connectionLocked = false;
        notifyAll();
    }

    private synchronized void openConnection() {
        Properties props = new Properties();
        props.setProperty("user", bundle.getString("databaseUser"));
        props.setProperty("password", bundle.getString("databasePassword"));
        props.setProperty("ssl", "true");   // necessary

        props.setProperty("sslfactory",
                "org.postgresql.ssl.DefaultJavaSSLFactory");

        try {
            System.out.println("Opening Database Connection");
            connection = DriverManager.getConnection("jdbc:postgresql://"
                    + bundle.getString("databaseHost")
                    + ":" + bundle.getString("databasePort")
                    + "/" + bundle.getString("databaseName"), props);
            connection.setAutoCommit(false);
            System.out.println("Opened Database Connection");
        } catch(SQLException e) {
            e.printStackTrace();
            closeConnection();
        } finally {
            notifyAll();
        }
    }

    public void closeConnection() {
        try {
            if(connection != null) {
                connection.close();
            }
            System.out.println("Database connection closed");
        } catch(SQLException e) {
            System.err.println(
                    "Exception while closing Database Connection:");
            e.printStackTrace();
        }
        connection = null;
    }
}
