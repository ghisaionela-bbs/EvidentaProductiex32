package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemReportDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class ManagerModel {
    private final User CONNECTED_USER;
    ObservableList<ProductDTO> products;
    ObservableList<OrderItemReportDTO> orders;

    public ManagerModel() {
        CONNECTED_USER = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
        this.products = FXCollections.observableArrayList();
        this.orders = FXCollections.observableArrayList();
    }

    public User getCONNECTED_USER() {
        return CONNECTED_USER;
    }

    public ObservableList<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(ObservableList<ProductDTO> products) {
        this.products = products;
    }

    public void loadProducts() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String selectCond = "";
            String whereCond = "";
            if(user.getID_ROLE() != 1) {
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM PRODUSE";
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

    public ObservableList<OrderItemReportDTO> getOrders() {
        return orders;
    }

    public void setOrders(ObservableList<OrderItemReportDTO> orders) {
        this.orders = orders;
    }

    public void loadOrders() {
        try{
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "    NULL AS ID_COMANDA, " +
                    "    NULL AS ID_ITEM_COMANDA, " +
                    "    NULL AS datasiora_i, " +
                    "    p.ID AS ID_PRODUS, " +
                    "    p.denumire, " +
                    "    p.um, " +
                    "    NULL AS cantitate, " +
                    "    SUM(ip.cantitate) AS realizat, " +
                    "    0.00 - SUM(ip.cantitate) AS rest " +
                    "FROM REALIZARI r " +
                    "LEFT JOIN PRODUSE p ON p.ID = ip.ID_PRODUS " +
                    "WHERE ip.ID_COMANDA IS NULL " +
                    "GROUP BY p.ID, p.denumire, p.um " +
                    "UNION " +
                    "SELECT c.ID AS ID_COMANDA,  " +
                    "    ic.ID AS ID_ITEM_COMANDA, " +
                    "    c.datasiora_i, " +
                    "    p.ID AS ID_PRODUS, " +
                    "    p.denumire, " +
                    "    p.um, " +
                    "    ic.cantitate, " +
                    "SUM(COALESCE(ip.cantitate, 0.00)) AS realizat, " +
                    "    ic.cantitate - SUM((COALESCE(ip.cantitate, 0.00))) AS rest " +
                    "FROM COMENZI AS c " +
                    "LEFT JOIN ITEME_COMENZI AS ic ON c.ID = ic.ID_COMANDA " +
                    "LEFT JOIN PRODUSE AS p ON p.ID = ic.ID_PRODUS " +
                    "LEFT JOIN INREGISTRARI_PRODUSE AS ip ON ip.ID_COMANDA = c.ID AND ic.ID_PRODUS = ip.ID_PRODUS " +
                    "GROUP BY c.ID, ic.ID, c.datasiora_i, p.ID, p.denumire, p.um, ic.cantitate " +
                    "ORDER BY datasiora_i ASC";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            orders.clear();

            while (resultSet.next()) {
                int ORDER_ID = resultSet.getInt("ID_COMANDA");
                int ORDER_ITEM_ID = resultSet.getInt("ID_ITEM_COMANDA");
                Timestamp orderDateAndTimeInserted = resultSet.getTimestamp("datasiora_i");
                int PRODUCT_ID = resultSet.getInt("ID_PRODUS");
                String productName = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                double quantity = resultSet.getDouble("cantitate");
                double completed = resultSet.getDouble("realizat");
                double remainer = resultSet.getDouble("rest");
                OrderItemReportDTO orderItem = new OrderItemReportDTO(
                        ORDER_ID, ORDER_ITEM_ID, orderDateAndTimeInserted,
                        PRODUCT_ID, productName, unitMeasurement, quantity, completed, remainer
                );
                orders.add(orderItem);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
