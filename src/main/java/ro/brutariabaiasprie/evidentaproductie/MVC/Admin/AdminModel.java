package ro.brutariabaiasprie.evidentaproductie.MVC.Admin;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminModel {
    private ObservableList<ProductDTO> products;

    public AdminModel() {
        this.products = FXCollections.observableArrayList();
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

            String sql = "SELECT * FROM [dbo].[PRODUSE] ORDER BY [denumire]";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            products.clear();
            while(resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                ProductDTO productDTO = new ProductDTO(ID, name, unitMeasurement);
                products.add(productDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
