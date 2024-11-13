package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group;

import ro.brutariabaiasprie.evidentaproductie.DTO.GroupDTO;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.GroupNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

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

            String sqlDel = "DELETE FROM GRUPE WHERE ID = ?";
            PreparedStatement statementDel = connection.prepareStatement(sqlDel);
            statementDel.setInt(1, group.getId());

            String sqlUpdate1 = "UPDATE UTILIZATORI SET ID_GRUPA = ? WHERE ID_GRUPA = ?";
            PreparedStatement statementUpdate1 = connection.prepareStatement(sqlUpdate1);
            statementUpdate1.setNull(1, Types.INTEGER);
            statementUpdate1.setInt(2, group.getId());

            String sqlUpdate2 = "UPDATE PRODUSE SET ID_GRUPA = ? WHERE ID_GRUPA = ?";
            PreparedStatement statementUpdate2 = connection.prepareStatement(sqlUpdate2);
            statementUpdate2.setNull(1, Types.INTEGER);
            statementUpdate2.setInt(2, group.getId());

            statementDel.executeUpdate();
            statementUpdate1.executeUpdate();
            statementUpdate2.executeUpdate();
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
}
