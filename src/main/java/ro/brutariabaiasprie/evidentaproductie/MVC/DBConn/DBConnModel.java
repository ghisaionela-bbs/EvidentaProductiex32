package ro.brutariabaiasprie.evidentaproductie.MVC.DBConn;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DBConnModel {
    void connectToDatabase(BiConsumer<Long, Long> progressUpdater) {
        try {
            DBConnectionService.connectToDatabase(getUrl(), getUsername(), getPassword());
            ConfigApp.setConfig(CONFIG_KEY.DBURL.name(), getUrl());
            ConfigApp.setConfig(CONFIG_KEY.DBUSER.name(), getUsername());
            ConfigApp.setConfig(CONFIG_KEY.DBPASS.name(), getPassword());
            ConfigApp.write_config();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//
//    void integrateComplicatedResults() {
//        presentationModel.setConnSuccess(domainObject);
//    }
    private final StringProperty url = new SimpleStringProperty("");
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return this.password.get();
    }

    public StringProperty passwordProperty() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setConnectionCredentials(String url, String username, String password) {
        this.url.set(url);
        this.username.set(username);
        this.password.set(password);
    }

}
