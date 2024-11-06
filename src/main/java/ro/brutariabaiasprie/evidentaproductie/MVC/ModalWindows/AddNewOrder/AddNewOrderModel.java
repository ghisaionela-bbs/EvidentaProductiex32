package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewOrder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.Calendar;

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

    public void placeOrder() {
        try {
            Connection connection = DBConnectionService.getConnection();

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

            String orderSql = "INSERT INTO [dbo].[COMENZI] (datasiora_i, ID_UTILIZATOR_I) VALUES (?, ?)";
            PreparedStatement orderStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStatement.setTimestamp(1, timestamp);
            orderStatement.setInt(2, user.getID());
            orderStatement.executeUpdate();

            ResultSet key = orderStatement.getGeneratedKeys();
            key.next();
            int ORDER_ID = key.getInt(1);

            String itemsSql = "INSERT INTO [dbo].[ITEME_COMENZI] (ID_PRODUS, ID_COMANDA, cantitate) VALUES (?, ?, ?)";
            PreparedStatement itemsStatement = connection.prepareStatement(itemsSql);
            for(OrderItemDTO orderItem : orderItems) {
                itemsStatement.setInt(1, orderItem.getID_PRODUS());
                itemsStatement.setInt(2, ORDER_ID);
                itemsStatement.setDouble(3, orderItem.getQuantity());
                itemsStatement.addBatch();
            }
            itemsStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
