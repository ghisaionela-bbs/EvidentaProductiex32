package ro.brutariabaiasprie.evidentaproductie.Services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import ro.brutariabaiasprie.evidentaproductie.Data.ModifiedTableData;

import java.sql.*;

/**
 * The database connection service
 */
public class DBConnectionService {
    private static Connection connection;
    private static ObservableMap<String, ModifiedTableData> modifiedTables;
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
    public static ObservableMap<String, ModifiedTableData> getModifiedTables() {
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
                    checkOrderCounter(connection);
                    Thread.sleep(1000); // Poll every second
                }
            }
        };
        Thread syncThread = new Thread(syncTask);
        syncThread.start();
    }

    private static void checkOrderCounter(Connection connection) throws SQLException {
        String sql = "SELECT (CASE " +
                "WHEN CAST(timestamp_value AS DATE) < CAST(GETDATE() AS DATE) THEN 1 " +
                "ELSE 0 " +
                "END) AS is_outdated " +
                "FROM utils " +
                "WHERE property_name = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "order_counter_last_updated");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int is_outdated = resultSet.getInt("is_outdated");
            if(is_outdated == 1) {
                try(PreparedStatement updateStatement1 = connection.prepareStatement("UPDATE utils SET " +
                        "int_value = 0" +
                        "WHERE property_name = ?")) {
                    updateStatement1.setString(1,"order_daily_counter");
                    updateStatement1.executeUpdate();
                }
                try(PreparedStatement updateStatement2 = connection.prepareStatement("UPDATE utils SET " +
                        "timestamp_value = GETDATE()" +
                        "WHERE property_name = ?")) {
                    updateStatement2.setString(1,"order_counter_last_updated");
                    updateStatement2.executeUpdate();
                }
            }
        }
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
                modifiedTables.put(table_name, new ModifiedTableData(rowId, operationType, changeTime));

                // Update last checked time to the most recent change time
                if (changeTime.after(lastCheckedTime)) {
                    lastCheckedTime = changeTime;
                }
            }
        }
    }

    /***
     * Removes notification entries that were created one hour ago
     * @param connection the database connection
     * @throws SQLException when there is a sql exception
     */
    private static void cleanupNotifications(Connection connection) throws SQLException {
        String cleanupQuery = "DELETE FROM change_notifications WHERE change_time < ?";
        try (PreparedStatement statement = connection.prepareStatement(cleanupQuery)) {
            Timestamp cutoffTime = new Timestamp(System.currentTimeMillis() - 3600000); // 1 hour ago
            statement.setTimestamp(1, cutoffTime);
            statement.executeUpdate();
        }
    }

    /***
     * Verifies the database
     * Checks first if the "APP_INFO" table exists in the database.
     * If it does not exist then it will create all the tables and the triggers.
     * Else it verifies if the database is compatible with the app version.
     * @throws SQLException when there is a sql exception
     */
    public static void verifyDatabase() throws SQLException {
        // Check if app info table exists
        try (PreparedStatement statement = connection.prepareStatement(
                "IF (NOT EXISTS " +
                "(SELECT object_id FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[APP_INFO]') and type = 'U')) " +
                "SELECT 1 " +
                "ELSE " +
                "SELECT 0 ")) {
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            // If it doesn't exist generate tables
            if(resultSet.getBoolean(1)) {
                generateTables();
            }
            // Verify database version
            else {
                verifyTables();
            }
        }
    }

    /***
     * Verifies the database tables to be compatible with the app version
     * @throws SQLException when there is a sql exception
     */
    private static void verifyTables() throws SQLException {
        // Verify database app compatibility
        try(PreparedStatement statement = connection.prepareStatement("SELECT valoare FROM [dbo].[APP_INFO] WHERE proprietate = ?")){
            statement.setString(1, "version");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String version = resultSet.getString("valoare");
//            if(!version.equals(AppProperties.properties.getProperty("version"))) {
//                throw new VersionCompatibility("App version is not compatible with database version");
//            }
        }
    }

    /***
     * Generates the necessary tables for the current app version
     * @throws SQLException when there is a sql exception
     */
    public static void generateTables() throws SQLException {
        // Setting up the app properties table for compatibility
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[APP_INFO](" +
                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
                "[proprietate] [nvarchar](50) NOT NULL, " +
                "[valoare] [nvarchar](100) NOT NULL)")) {
            statement.execute();
        }
        // Setting up the version compatible with the app
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO [dbo].[APP_INFO] " +
                "(proprietate, valoare) VALUES (?, ?)")) {
            statement.setString(1,"version");
//            statement.setString(2, AppProperties.get("version"));
            statement.setString(2, "1.0.0");
            statement.executeUpdate();
        }

        // Setting up the table for storing different types of data
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[utils](" +
                "[property_name] [nvarchar](50) NOT NULL, " +
                "[string_value] [nvarchar](100), " +
                "[int_value] [int], " +
                "[double_value] [numeric](38, 2), " +
                "[timestamp_value] [datetime] " +
                ")")) {
            statement.execute();
        }
        // Adding the rows necessary for tracking the number of the last order
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO [dbo].[utils] " +
                "(property_name, int_value) VALUES (?, ?)")) {
            statement.setString(1, "order_daily_counter");
            statement.setInt(2, 0);
            statement.execute();
        }
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO [dbo].[utils] " +
                "(property_name, timestamp_value) VALUES (?, GETDATE())")) {
            statement.setString(1, "order_counter_last_updated");
            statement.execute();
        }

        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[change_notifications]( " +
                "[id] [int] IDENTITY(1,1) NOT NULL, " +
                "[table_name] [nvarchar](50) NOT NULL, " +
                "[operation_type] [nvarchar](10) NOT NULL, " +
                "[change_time] [datetime] NOT NULL DEFAULT (getdate()), " +
                "[row_id] [int] NOT NULL) ")) {
            statement.execute();
        }
//        // Setting up the users groups table
//        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[GRUPE_UTILIZATORI](  " +
//                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
//                "[denumire] [nvarchar](50) NOT NULL)")) {
//            statement.execute();
//        }
        // Setting up the users groups table triggers
//        createTableTriggers("GRUPE_UTILIZATORI");

        // Setting up the products groups table
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[GRUPE_PRODUSE](  " +
                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
                "[denumire] [nvarchar](50) NOT NULL, " +
                "[ID_GRUPA_PARINTE] [int] NULL)")) {
            statement.execute();
        }
        // Setting up the products groups table triggers
        createTableTriggers("GRUPE_PRODUSE");

        // Setting up the products table
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[PRODUSE](" +
                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
                "[denumire] [nvarchar](255) NOT NULL, " +
                "[sarja] [decimal](38, 2) NOT NULL DEFAULT 0.00," +
                "[um] [nvarchar](5) NOT NULL, " +
                "[ID_GRUPA] [int] NULL," +
                "[ID_SUBGRUPA_PRODUSE] [int] NULL)")) {
            statement.execute();
        }
        // Setting up the products table triggers
        createTableTriggers("PRODUSE");

        // Setting up the users table
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[UTILIZATORI](" +
                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
                "[ID_ROL] [int] NOT NULL, " +
                "[nume_utilizator] [varchar](255) NOT NULL, " +
                "[parola] [varchar](255) NOT NULL, " +
                "[ID_GRUPA] [int] NULL," +
                "[ID_SUBGRUPA_PRODUSE] [int] NULL)")) {
            statement.execute();
        }
        // Setting up the users table triggers
        createTableTriggers("UTILIZATORI");

        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[GRUPE_UTILIZATORI](" +
                "[ID_GRUPA] [int]," +
                "[ID_UTILIZATOR])")) {
        }
        createTableTriggers("GRUPE_UTILIZATORI");

        // Setting up the orders table
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[COMENZI](  " +
                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
                "[contor] INT NOT NULL, " +
                "[data_programata] [datetime] NOT NULL DEFAULT GETDATE(), " +
                "[ID_PRODUS] [int] NOT NULL," +
                "[cantitate] [decimal](38, 2) NOT NULL," +
                "[datasiora_i] [datetime] NOT NULL," +
                "[ID_UTILIZATOR_I] [int] NOT NULL," +
                "[datasiora_m] [datetime] NULL," +
                "[ID_UTILIZATOR_M] [int] NULL," +
                "[inchisa] [bit] NOT NULL DEFAULT ((0)) )")) {
            statement.execute();
        }
        // Setting up the orders table triggers
        createTableTriggers("COMENZI");
        // Setting up the trigger that will increment the order daily counter
        try(PreparedStatement statement = connection.prepareStatement("EXEC('CREATE TRIGGER [dbo].[trg_order_counter_update] ON [dbo].[COMENZI] AFTER INSERT " +
                "AS " +
                "BEGIN " +
                "DECLARE @order_counter INT " +
                "SET @order_counter = (SELECT int_value + 1 AS value FROM utils WHERE property_name = ''order_daily_counter'') " +
                "UPDATE utils SET int_value = @order_counter WHERE property_name = ''order_daily_counter'' " +
                "END')")) {
            statement.execute();
        }

        // Setting up the records table
        try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE [dbo].[REALIZARI]( " +
                "[ID] [int] IDENTITY(1,1) NOT NULL, " +
                "[ID_PRODUS] [int] NOT NULL, " +
                "[cantitate] [decimal](38, 2) NOT NULL, " +
                "[datasiora_i] [datetime] NOT NULL, " +
                "[ID_UTILIZATOR_I] [int] NOT NULL, " +
                "[datasiora_m] [datetime] NULL, " +
                "[ID_UTILIZATOR_M] [int] NULL, " +
                "[ID_COMANDA] [int] NULL)")) {
            statement.execute();
        }
        // Setting up the records table triggers
        createTableTriggers("REALIZARI");

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO [dbo].[UTILIZATORI] " +
                "(nume_utilizator, parola, ID_ROL, ID_GRUPA)" +
                "VALUES (?, ?, ?, ?)" )) {
            statement.setString(1, "director");
            statement.setString(2, "director");
            statement.setInt(3, 1);
            statement.setNull(4, Types.INTEGER);
            statement.execute();
        }

    }

    /***
     * Creates "AFTER INSERT", "AFTER UPDATE" and "AFTER DELETE" triggers for the table with the given table name
     * @param tableName the name of the table that the triggers will be created for
     * @throws SQLException when there is a sql exception
     */
    private static void createTableTriggers(String tableName) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                createTriggerSql("trg_" + tableName + "_after_insert", tableName, "INSERT"))) {
            statement.execute();
        }
        try(PreparedStatement statement = connection.prepareStatement(
                createTriggerSql("trg_" + tableName + "_after_update", tableName, "UPDATE"))) {
            statement.execute();
        }
        try(PreparedStatement statement = connection.prepareStatement(
                createTriggerSql("trg_" + tableName + "_after_delete", tableName, "DELETE"))) {
            statement.execute();
        }
    }

    /***
     * Creates a sql query for creating a trigger depending on the given operationType,
     * with the given name for the given table
     * @param triggerName the name of the trigger
     * @param tableName the name of the table the triggers will be created for
     * @param operationType the type of operation that will trigger the trigger after it was executed
     *                      can be "DELETE", "UPDATE" or "INSERT"
     * @return the sql query for creating the trigger
     */
    private static String createTriggerSql(String triggerName, String tableName, String operationType) {
        String triggerSql = "EXEC('CREATE TRIGGER [dbo].[" + triggerName + "] " +
                "ON [dbo].[" + tableName + "] AFTER " + operationType + " " +
                "AS " +
                "BEGIN " +
                "INSERT INTO change_notifications (table_name, operation_type, row_id) " +
                "SELECT ''" + tableName + "'', ''" + operationType + "'', id ";
        switch (operationType) {
            case "INSERT":
            case "UPDATE":
                triggerSql += "FROM inserted ";
                break;
            case "DELETE":
                triggerSql += "FROM deleted ";
                break;
        }
        triggerSql += "END')";
        return triggerSql;
    }
}
