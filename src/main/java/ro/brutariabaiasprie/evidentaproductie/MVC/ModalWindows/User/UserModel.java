package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Data.ACCESS_LEVEL;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.UserRole;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class UserModel {
    private User user;
    private final ObservableList<UserRole> roles = FXCollections.observableArrayList();
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final ObservableList<Group> subgroups = FXCollections.observableArrayList();

    public UserModel() {
        for(ACCESS_LEVEL accessLevel : ACCESS_LEVEL.values()) {
            if(accessLevel != ACCESS_LEVEL.UNAUTHORIZED) {
                roles.add(new UserRole(accessLevel));
            }
        }
        subgroups.add(null);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public ObservableList<UserRole> getRoles() {
        return roles;
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }

    public ObservableList<Group> getSubgroups() {
        return subgroups;
    }

    public void loadGroups() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE ID_GRUPA_PARINTE IS NULL ORDER BY denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            groups.clear();
            groups.add(null);
            while (resultSet.next()) {
                Group group = new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getInt("ID_GRUPA_PARINTE")
                );
                groups.add(group);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSubgroups(Group group) {
        try {
            subgroups.clear();
            if(group == null) {
                return;
            }
            if(group.getId() == 0) {
                return;
            }
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE ID_GRUPA_PARINTE = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, group.getId());
            ResultSet resultSet = statement.executeQuery();

            subgroups.add(new Group());
            while(resultSet.next()) {
                Group subgroup = new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getInt("ID_GRUPA_PARINTE")
                );
                subgroups.add(subgroup);
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
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "UPDATE UTILIZATORI SET " +
                    "nume_utilizator = ?, " +
                    "parola = ?, " +
                    "ID_ROL = ?, " +
                    "ID_GRUPA = ?, " +
                    "ID_SUBGRUPA_PRODUSE = ? " +
                    "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getRoleId());
            if (user.getGroupId() == 0) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, user.getGroupId());
            }
            if (user.getSubgroupId() == 0) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, user.getSubgroupId());
            }
            statement.setInt(6, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO UTILIZATORI (nume_utilizator, parola, ID_ROL, ID_GRUPA, ID_SUBGRUPA_PRODUSE)" +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getRoleId());
            if (user.getGroupId() == 0) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, user.getGroupId());
            }
            if (user.getSubgroupId() == 0) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, user.getSubgroupId());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Group getGroup(int groupId) {
        if (groupId == 0) {
            return null;
        }
        for (Group group : groups) {
            if(group != null) {
                if (group.getId() == groupId) {
                    return group;
                }
            }
        }
        return null;
    }

    public Group getSubgroup(int subgroupId) {
        if (subgroupId == 0) {
            return null;
        }
        for (Group group : subgroups) {
            if(group != null) {
                if (group.getId() == subgroupId) {
                    return group;
                }
            }
        }
        return null;
    }

}
