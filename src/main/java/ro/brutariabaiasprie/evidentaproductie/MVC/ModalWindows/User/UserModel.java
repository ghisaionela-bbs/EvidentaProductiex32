package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
    private User user;
    private final ObservableList<Group> groups = FXCollections.observableArrayList();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void loadGroups() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            groups.clear();
            groups.add(null);
            while (resultSet.next()) {
                Group group = new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire")
                );
                groups.add(group);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "DELETE FROM UTILIZATORI WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }
}
