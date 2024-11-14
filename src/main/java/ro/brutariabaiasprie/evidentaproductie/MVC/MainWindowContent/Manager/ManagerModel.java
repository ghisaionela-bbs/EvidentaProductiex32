package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class ManagerModel {
    private final User CONNECTED_USER;
    ObservableList<Product> products = FXCollections.observableArrayList();
    ObservableList<Group> groups = FXCollections.observableArrayList();
    ObservableList<Order> orders = FXCollections.observableArrayList();

    public ManagerModel() {
        CONNECTED_USER = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
    }

    public ObservableList<Order> getOrders() {
        return orders;
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }


    public User getCONNECTED_USER() {
        return CONNECTED_USER;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public void setProducts(ObservableList<Product> products) {
        this.products = products;
    }

    public void loadProducts() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String whereCond = "";
            if(user.getID_GROUP() != 0 && user.getID_ROLE() != 1) {
                whereCond = "WHERE p.ID_GRUPA = ? ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "g.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE g ON p.ID_GRUPA = g.ID " +
                    whereCond +
                    "ORDER BY p.um, p.denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_GROUP() != 0 && user.getID_ROLE() != 1) {
                statement.setInt(1, user.getID_GROUP());
            }
            ResultSet resultSet = statement.executeQuery();

            products.clear();
            while(resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId,
                            resultSet.getString("denumire_grupa"));
                }
                Product product = new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getString("um"),
                        group
                );
                product.setGroup(group);
                products.add(product);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadOrders() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "g.ID AS ID_GRUPA, " +
                    "g.denumire AS denumire_grupa, " +
                    "c.cantitate, " +
                    "SUM(COALESCE(r.cantitate, 0.00)) AS realizat, " +
                    "c.cantitate - SUM(COALESCE(r.cantitate, 0.00)) AS rest, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "FROM COMENZI c " +
                    "LEFT JOIN PRODUSE p ON p.ID = c.ID_PRODUS " +
                    "LEFT JOIN REALIZARI r ON r.ID_COMANDA = c.ID " +
                    "LEFT JOIN GRUPE g ON g.ID = p.ID_GRUPA " +
                    "GROUP BY c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "g.ID, " +
                    "g.denumire, " +
                    "c.cantitate, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "ORDER BY c.datasiora_i ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            orders.clear();
            while (resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId, resultSet.getString("denumire_grupa"));
                }

                Order order = new Order();
                order.setId(resultSet.getInt("ID"));
                order.setProduct(new Product(
                        resultSet.getInt("ID_PRODUS"),
                        resultSet.getString("denumire"),
                        resultSet.getString("um"),
                        group
                ));
                order.setQuantity(resultSet.getDouble("cantitate"));
                order.setCompleted(resultSet.getDouble("realizat"));
                order.setRemainder(resultSet.getDouble("rest"));
                order.setDateTimeInserted(resultSet.getTimestamp("datasiora_i"));
                order.setUserIdInserted(resultSet.getInt("ID_UTILIZATOR_I"));
                order.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
                order.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
                order.setClosed(resultSet.getBoolean("inchisa"));
                orders.add(order);
            }

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

//    public void loadOrders() {
//        try{
//            Connection connection = DBConnectionService.getConnection();
//            String sql = "SELECT " +
//                    "    NULL AS ID_COMANDA, " +
//                    "    NULL AS ID_ITEM_COMANDA, " +
//                    "    NULL AS datasiora_i, " +
//                    "    p.ID AS ID_PRODUS, " +
//                    "    p.denumire, " +
//                    "    p.um, " +
//                    "    NULL AS cantitate, " +
//                    "    SUM(ip.cantitate) AS realizat, " +
//                    "    0.00 - SUM(ip.cantitate) AS rest " +
//                    "FROM REALIZARI r " +
//                    "LEFT JOIN PRODUSE p ON p.ID = ip.ID_PRODUS " +
//                    "WHERE ip.ID_COMANDA IS NULL " +
//                    "GROUP BY p.ID, p.denumire, p.um " +
//                    "UNION " +
//                    "SELECT c.ID AS ID_COMANDA,  " +
//                    "    ic.ID AS ID_ITEM_COMANDA, " +
//                    "    c.datasiora_i, " +
//                    "    p.ID AS ID_PRODUS, " +
//                    "    p.denumire, " +
//                    "    p.um, " +
//                    "    ic.cantitate, " +
//                    "SUM(COALESCE(ip.cantitate, 0.00)) AS realizat, " +
//                    "    ic.cantitate - SUM((COALESCE(ip.cantitate, 0.00))) AS rest " +
//                    "FROM COMENZI AS c " +
//                    "LEFT JOIN ITEME_COMENZI AS ic ON c.ID = ic.ID_COMANDA " +
//                    "LEFT JOIN PRODUSE AS p ON p.ID = ic.ID_PRODUS " +
//                    "LEFT JOIN INREGISTRARI_PRODUSE AS ip ON ip.ID_COMANDA = c.ID AND ic.ID_PRODUS = ip.ID_PRODUS " +
//                    "GROUP BY c.ID, ic.ID, c.datasiora_i, p.ID, p.denumire, p.um, ic.cantitate " +
//                    "ORDER BY datasiora_i ASC";
//
//            PreparedStatement statement = connection.prepareStatement(sql);
//            ResultSet resultSet = statement.executeQuery();
//            orders.clear();
//
//            while (resultSet.next()) {
//                int ORDER_ID = resultSet.getInt("ID_COMANDA");
//                int ORDER_ITEM_ID = resultSet.getInt("ID_ITEM_COMANDA");
//                Timestamp orderDateAndTimeInserted = resultSet.getTimestamp("datasiora_i");
//                int PRODUCT_ID = resultSet.getInt("ID_PRODUS");
//                String productName = resultSet.getString("denumire");
//                String unitMeasurement = resultSet.getString("um");
//                double quantity = resultSet.getDouble("cantitate");
//                double completed = resultSet.getDouble("realizat");
//                double remainer = resultSet.getDouble("rest");
//                OrderItemReportDTO orderItem = new OrderItemReportDTO(
//                        ORDER_ID, ORDER_ITEM_ID, orderDateAndTimeInserted,
//                        PRODUCT_ID, productName, unitMeasurement, quantity, completed, remainer
//                );
//                orders.add(orderItem);
//            }


//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


}
