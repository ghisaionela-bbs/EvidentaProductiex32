package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewProduct;

import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddNewProductModel {
    public void addProduct(String productName, String unitMeasurement) {
        try {
            Connection connection = DBConnectionService.getConnection();

            String sql = "INSERT INTO [dbo].[PRODUSE] (denumire, um) VALUES (?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, productName);
            statement.setString(2, unitMeasurement);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
