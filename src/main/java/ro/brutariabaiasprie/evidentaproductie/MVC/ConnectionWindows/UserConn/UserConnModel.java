package ro.brutariabaiasprie.evidentaproductie.MVC.ConnectionWindows.UserConn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ro.brutariabaiasprie.evidentaproductie.Data.ACCESS_LEVEL;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.UserRole;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.UserNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserConnModel {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

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
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void findUser(String username, String password) throws UserNotFound{
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM UTILIZATORI WHERE nume_utilizator = ? AND parola = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                int ID = resultSet.getInt("ID");
                int ID_ROLE = resultSet.getInt("ID_ROL");
                int ID_GROUP = resultSet.getInt("ID_GRUPA");
                int subgroupId = resultSet.getInt("ID_SUBGRUPA_PRODUSE");
                User user = new User();
                user.setId(ID);
                user.setRoleId(ID_ROLE);
                user.setUsername(username);
                user.setPassword(password);
                user.setGroupId(ID_GROUP);
                user.setSubgroupId(subgroupId);

                ConfigApp.setConfig(CONFIG_KEY.USER.name(), user);
                ConfigApp.setConfig(CONFIG_KEY.USER_ROLE.name(), new UserRole(ACCESS_LEVEL.values()[ID_ROLE]));

                ConfigApp.setConfig(CONFIG_KEY.APPUSER.name(), user);
                ConfigApp.write_config();

            } else {
                throw new UserNotFound(String.format("The user with password '%s' and username '%s' was not found", username, password));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
