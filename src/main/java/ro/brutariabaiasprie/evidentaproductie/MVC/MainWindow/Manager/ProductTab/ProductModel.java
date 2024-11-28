package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Manager.ProductTab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductModel {
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    private void loadProducts() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM PRODUSE";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
