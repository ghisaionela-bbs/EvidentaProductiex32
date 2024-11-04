package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductTableSelection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductTableSelectionModel {
    private ObservableList<ProductDTO> products;

    public ProductTableSelectionModel() {
        products = FXCollections.observableArrayList();
    }

    public ObservableList<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(ObservableList<ProductDTO> products) {
        this.products = products;
    }

    public void loadProducts() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM [dbo].[PRODUSE]";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            products.clear();
            while (resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String productName = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                products.add(new ProductDTO(ID, productName, unitMeasurement));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
