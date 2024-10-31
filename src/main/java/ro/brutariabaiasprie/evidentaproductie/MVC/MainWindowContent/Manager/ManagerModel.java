package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.Calendar;

public class ManagerModel {
    ObservableList<ProductDTO> products;
    ObservableList<OrderDTO> orders;

    public ManagerModel() {
        this.products = FXCollections.observableArrayList();
        this.orders = FXCollections.observableArrayList();
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
            while(resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                ProductDTO product = new ProductDTO(ID, name, unitMeasurement);
                products.add(product);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(ObservableList<OrderDTO> orders) {
        this.orders = orders;
    }

    public void loadOrders() {
        try{
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "c.cantitate, " +
                    "p.um, " +
                    "SUM(COALESCE(ip.cantitate, 0.00)) AS realizat, " +
                    "c.cantitate - SUM(COALESCE(ip.cantitate, 0.00)) AS rest " +
                    "FROM [dbo].[COMENZI] AS c " +
                    "LEFT JOIN [dbo].[PRODUSE] AS p ON c.ID_PRODUS = p.ID " +
                    "LEFT JOIN [dbo].[INREGISTRARI_PRODUSE] AS ip ON c.ID_PRODUS = ip.ID_COMANDA " +
                    "WHERE CAST(c.datasiora_i AS DATE) = CAST(? AS DATE) " +
                    "GROUP BY c.ID, c.ID_PRODUS, p.denumire, c.cantitate, p.um";
//            String sql = "SELECT " +
//                    "p.ID AS ID_PRODUS, " +
//                    "p.denumire, " +
//                    "p.um, " +
//                    "c.ID AS ID_COMANDA, " +
//                    "COALESCE(c.cantitate, 0.00) AS cantitate, " +
//                    "SUM(ip.cantitate) AS realizat, " +
//                    "COALESCE(c.cantitate, 0.00) - SUM(ip.cantitate) AS rest " +
//                    "FROM " +
//                    "PRODUSE AS p " +
//                    "LEFT JOIN " +
//                    "COMENZI AS c ON p.ID = c.ID_PRODUS " +
//                    "LEFT JOIN " +
//                    "INREGISTRARI_PRODUSE AS ip ON p.ID = ip.ID_PRODUS " +
//                    "WHERE CAST(c.datasiora_i AS DATE) = CAST(? AS DATE) " +
//                    "GROUP BY p.ID, p.denumire, p.um, c.cantitate, c.ID " +
//                    "UNION " +
//                    "SELECT " +
//                    "p.ID AS ID_PRODUS, " +
//                    "p.denumire, " +
//                    "p.um, " +
//                    "-1 AS ID_COMANDA, " +
//                    "0.00 AS cantitate, " +
//                    "( " +
//                    "SELECT COALESCE( " +
//                    "SUM(ip.cantitate), " +
//                    "0.00) " +
//                    "FROM INREGISTRARI_PRODUSE AS ip WHERE ip.ID_PRODUS = p.ID AND CAST(? AS DATE) = CAST(ip.datasiora_i AS DATE) " +
//                    ") AS realizat, " +
//                    "0.00 - ( " +
//                    "SELECT COALESCE( " +
//                    "SUM(ip.cantitate), " +
//                    "0.00) " +
//                    "FROM INREGISTRARI_PRODUSE AS ip WHERE ip.ID_PRODUS = p.ID AND CAST(? AS DATE) = CAST(ip.datasiora_i AS DATE) " +
//                    ") AS rest " +
//                    "FROM " +
//                    "PRODUSE AS p " +
//                    "LEFT JOIN " +
//                    "COMENZI AS c ON p.ID = c.ID_PRODUS " +
//                    "LEFT JOIN " +
//                    "INREGISTRARI_PRODUSE AS ip ON p.ID = ip.ID_PRODUS " +
//                    "WHERE c.ID_PRODUS IS NULL " +
//                    "ORDER BY cantitate DESC, p.denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            statement.setTimestamp(1, timestamp);
//            statement.setTimestamp(2, timestamp);
//            statement.setTimestamp(3, timestamp);

            orders.clear();
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                int ID = resultSet.getInt("ID");
                int ID_PRODUS = resultSet.getInt("ID_PRODUS");
                String productName = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                double quantity = resultSet.getDouble("cantitate");
                double completed = resultSet.getDouble("realizat");
                double remainder = resultSet.getDouble("rest");

                OrderDTO order = new OrderDTO(ID, ID_PRODUS, productName, quantity, unitMeasurement,completed, remainder);
                orders.add(order);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
