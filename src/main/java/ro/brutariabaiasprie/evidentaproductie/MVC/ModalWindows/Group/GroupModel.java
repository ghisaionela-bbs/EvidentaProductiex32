package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group;

import ro.brutariabaiasprie.evidentaproductie.DTO.Group;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.GroupNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupModel {
    private Group group;

    public void setGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void addGroup(String name) {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO GRUPE (denumire) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteGroup() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "DELETE FROM GRUPE WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,group.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGroup() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "UPDATE GRUPE SET denumire = ? WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, group.getName());
            statement.setInt(2, getGroup().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGroup() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, group.getId());
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) {
                throw new GroupNotFound(String.format("Grupul cu ID-ul '%d' nu a fost gasit.", group.getId()));
            }
            group.setName(resultSet.getString("denumire"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
