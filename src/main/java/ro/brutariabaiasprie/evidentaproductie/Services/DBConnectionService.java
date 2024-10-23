package ro.brutariabaiasprie.evidentaproductie.Services;

import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The database connection service
 */
public class DBConnectionService {
    private static Connection connection;

    /**
     * Returns the connection
     * @return connection or null if connection is not created
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Creates the connection to the database
     * @param url - string for the url of the database
     * @param username - string for the username of the database
     * @param password - string for the password of the database
     * @throws SQLException when there is an error
     */
    public static void connectToDatabase(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Closes the connection if there exists one and it is open
     */
    public static void close() {
        if(connection != null) {
            try {
                if(!connection.isClosed()){
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
