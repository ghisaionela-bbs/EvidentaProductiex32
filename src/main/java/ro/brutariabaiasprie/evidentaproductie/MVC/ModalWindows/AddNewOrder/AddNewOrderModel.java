package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewOrder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddNewOrderModel {
    private final ObservableList<ProductDTO> products;
    private final ObservableList<OrderItemDTO> orderItems;

    public AddNewOrderModel(){
        products = FXCollections.observableArrayList();
        orderItems = FXCollections.observableArrayList();
    }

    public ObservableList<ProductDTO> getProducts() {
        return products;
    }

    public ObservableList<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void addItem(OrderItemDTO orderItem) {
        orderItems.add(orderItem);
    }

    public void removeItem(OrderItemDTO orderItem) {
        orderItems.remove(orderItem);
    }

    public void updateItem(int index, OrderItemDTO orderItem) {
        orderItems.set(index, orderItem);
    }

    public void loadProducts() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT [ID], [denumire], [um] FROM [dbo].[PRODUSE] ORDER BY [denumire] ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            products.clear();
            while (resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String productName = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                ProductDTO product = new ProductDTO(ID, productName, unitMeasurement);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
