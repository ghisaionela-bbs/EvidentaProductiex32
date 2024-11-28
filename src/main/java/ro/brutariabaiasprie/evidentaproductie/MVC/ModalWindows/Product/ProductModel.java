package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

/***
 * The ProductModel used by the ProductController
 */
public class ProductModel {
    private Product product;
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final ObservableList<Group> subgroups = FXCollections.observableArrayList();

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }

    public ObservableList<Group> getSubgroups() {
        return subgroups;
    }

    public void deleteProduct() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "DELETE FROM PRODUSE WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProduct() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "UPDATE PRODUSE SET " +
                    "denumire = ?, " +
                    "um = ?, " +
                    "ID_GRUPA = ?, " +
                    "ID_SUBGRUPA_PRODUSE = ? " +
                    "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setString(2, product.getUnitMeasurement());
            if(product.getGroup() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, product.getGroup().getId());
            }
            if(product.getSubgroupId() == 0) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, product.getSubgroupId());
            }
            statement.setInt(5, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProduct() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO PRODUSE (denumire, um, ID_GRUPA, ID_SUBGRUPA_PRODUSE) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setString(2, product.getUnitMeasurement());
            if(product.getGroup() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, product.getGroup().getId());
            }
            if(product.getSubgroupId() == 0) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, product.getSubgroupId());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGroups() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE ID_GRUPA_PARINTE IS NULL ORDER BY denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            groups.clear();
            groups.add(new Group());
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
