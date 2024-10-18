package ro.brutariabaiasprie.evidentaproductie.MVC.DBConn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DBConnModel {
    private final PresentationModel presentationModel = new PresentationModel();
    private String domainObject = "Connection not established";

    void connectToDatabase(BiConsumer<Long, Long> progressUpdater) {
        try {
            DBConnectionService.connectToDatabase(presentationModel.getUrl(), presentationModel.getUsername(), presentationModel.getPassword());
            ConfigApp.setConfig(CONFIG_KEY.DBURL.name(), presentationModel.getUrl());
            ConfigApp.setConfig(CONFIG_KEY.DBUSER.name(), presentationModel.getUsername());
            ConfigApp.setConfig(CONFIG_KEY.DBPASS.name(), presentationModel.getPassword());
            ConfigApp.write_config();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void integrateComplicatedResults() {
        presentationModel.setTheResult(domainObject);
    }

    public PresentationModel getPresentationModel() {
        return presentationModel;
    }

    public static class PresentationModel {
        private final StringProperty url = new SimpleStringProperty("");
        private final StringProperty username = new SimpleStringProperty("");
        private final StringProperty password = new SimpleStringProperty("");
        private final StringProperty theResult = new SimpleStringProperty("");

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

        public void setTheResult(String theResult) {
            this.theResult.set(theResult);
        }
    }


}
