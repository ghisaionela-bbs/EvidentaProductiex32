package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product;

import ro.brutariabaiasprie.evidentaproductie.DTO.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductModel {
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {

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
    }
}
