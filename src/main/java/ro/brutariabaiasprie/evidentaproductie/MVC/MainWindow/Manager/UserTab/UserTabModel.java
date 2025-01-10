package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Manager.UserTab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTabModel {
    private final ObservableList<User> users = FXCollections.observableArrayList();

    public void loadUsers() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM UTILIZATORI";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            users.clear();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("ID"));
                user.setUsername(resultSet.getString("nume_utilizator"));
                user.setPassword(resultSet.getString("parola"));
                user.setRoleId(resultSet.getInt("ID_ROL"));
                user.setGroupId(resultSet.getInt("ID_GRUPA"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
