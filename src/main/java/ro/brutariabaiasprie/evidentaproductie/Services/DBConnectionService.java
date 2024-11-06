package ro.brutariabaiasprie.evidentaproductie.Services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;

import java.sql.*;

/**
 * The database connection service
 */
public class DBConnectionService {
    private static Connection connection;
    private static ObservableMap<String, Timestamp> modifiedTables;
    /**
     * Returns the connection
     * @return connection or null if connection is not created
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Returns a list of tables that were modified in the last second
     * @return ObservableList<String> modifiedTables
     */
    public static ObservableMap<String, Timestamp> getModifiedTables() {
        return modifiedTables;
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
        modifiedTables = FXCollections.observableHashMap();
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

    // Last checked timestamp
    private static Timestamp lastCheckedTime = new Timestamp(System.currentTimeMillis() - 60000); // 1 minute ago

    public static void startSync() {
        Task<Void> syncTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    checkForNotifications(connection);
                    cleanupNotifications(connection);
                    Thread.sleep(500); // Poll every second
                }
            }
        };
        Thread syncThread = new Thread(syncTask);
        syncThread.start();
    }

    private static void checkForNotifications(Connection connection) throws SQLException {
        String query = "SELECT id, table_name, operation_type, change_time, row_id FROM change_notifications WHERE change_time > ? ORDER BY change_time";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, lastCheckedTime);
            ResultSet resultSet = statement.executeQuery();

            modifiedTables.clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String table_name = resultSet.getString("table_name");
                String operationType = resultSet.getString("operation_type");
                Timestamp changeTime = resultSet.getTimestamp("change_time");
                int rowId = resultSet.getInt("row_id");

                // Process the change event
                modifiedTables.put(table_name, changeTime);

                // Update last checked time to the most recent change time
                if (changeTime.after(lastCheckedTime)) {
                    lastCheckedTime = changeTime;
                }
            }
        }
    }

    private static void cleanupNotifications(Connection connection) throws SQLException {
        String cleanupQuery = "DELETE FROM change_notifications WHERE change_time < ?";
        try (PreparedStatement statement = connection.prepareStatement(cleanupQuery)) {
            Timestamp cutoffTime = new Timestamp(System.currentTimeMillis() - 3600000); // 1 hour ago
            statement.setTimestamp(1, cutoffTime);
            statement.executeUpdate();
        }
    }

}
