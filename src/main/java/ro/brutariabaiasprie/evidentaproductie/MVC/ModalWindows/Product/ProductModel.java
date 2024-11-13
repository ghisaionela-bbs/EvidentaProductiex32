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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ObservableList<Group> getGroups() {
        return groups;
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
                    "ID_GRUPA = ? " +
                    "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setString(2, product.getUnitMeasurement());
            if(product.getGroup() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, product.getGroup().getId());
            }
            statement.setInt(4, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProduct() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO PRODUSE (denumire, um, ID_GRUPA) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setString(2, product.getUnitMeasurement());
            if(product.getGroup() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, product.getGroup().getId());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGroups() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            groups.clear();
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
}
