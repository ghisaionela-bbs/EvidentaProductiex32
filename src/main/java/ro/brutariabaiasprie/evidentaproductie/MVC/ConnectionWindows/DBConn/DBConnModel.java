package ro.brutariabaiasprie.evidentaproductie.MVC.ConnectionWindows.DBConn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.BiConsumer;

/**
 * The model for the Database connection scene
 */
public class DBConnModel {
    private final StringProperty url = new SimpleStringProperty("");
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");

    /**
     * @return url of the database connection model
     */
    public String getUrl() {
        return url.get();
    }

    /**
     * @return url StringProperty of the connection model
     */
    public StringProperty urlProperty() {
        return url;
    }

    /**
     * Sets the string of the url StringProperty model
     * @param url - the string to set
     */
    public void setUrl(String url) {
        this.url.set(url);
    }

    /**
     * @return username of the database connection model
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * @return username StringProperty of the connection model
     */
    public StringProperty usernameProperty() {
        return username;
    }

    /**
     * Sets the string of the username StringProperty model
     * @param username - the string to set
     */
    public void setUsername(String username) {
        this.username.set(username);
    }

    /**
     * @return password of the database connection model
     */
    public String getPassword() {
        return this.password.get();
    }

    /**
     * @return password StringProperty of the database connection model
     */
    public StringProperty passwordProperty() {
        return this.password;
    }

    /**
     * Sets the string of the password StringProperty model
     * @param password - the string to set
     */
    public void setPassword(String password) {
        this.password.set(password);
    }

    /**
     * Method for setting all credentials of the database connection in the model
     * @param url - the string for url to set
     * @param username - the string for username to set
     * @param password - the password for username to set
     */
    public void setConnectionCredentials(String url, String username, String password) {
        this.url.set(url);
        this.username.set(username);
        this.password.set(password);
    }

    /**
     * Connects to the database using the credentials set in the model
     * If successful, updates the configuration with the credentials
     */
    void connectToDatabase() throws IOException {
        try {
            DBConnectionService.connectToDatabase(getUrl(), getUsername(), getPassword());
            ConfigApp.setConfig(CONFIG_KEY.DBURL.name(), getUrl());
            ConfigApp.setConfig(CONFIG_KEY.DBUSER.name(), getUsername());
            ConfigApp.setConfig(CONFIG_KEY.DBPASS.name(), getPassword());
            ConfigApp.write_config();
        } catch (SQLException e) {
            File myObj = new File("errorLog.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter("errorLog.txt");
            myWriter.write(e.toString());
            myWriter.close();
        }
    }
}
