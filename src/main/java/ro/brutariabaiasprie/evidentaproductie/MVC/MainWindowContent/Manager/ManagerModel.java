package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class ManagerModel {
    private final User CONNECTED_USER;
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObservableList<Record> records = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();

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

    public ObservableList<Record> getRecords() {
        return records;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void loadProducts() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
//            String whereCond = "";
//            if(user.getID_GROUP() != 0 && user.getID_ROLE() != 1) {
//                whereCond = "WHERE p.ID_GRUPA = ? ";
//            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "gp.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
//                    whereCond +
                    "ORDER BY p.um, p.denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
//            if(user.getID_GROUP() != 0 && user.getID_ROLE() != 1) {
//                statement.setInt(1, user.getID_GROUP());
//            }
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

    public void loadRecords() {
        try {
            Connection connection = DBConnectionService.getConnection();

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String whereCond = "";
            if(user.getID_ROLE() != 1 && user.getID_ROLE() != 2) {
                whereCond = " AND r.ID_UTILIZATOR_I = ? ";
            }

            String sql = " SELECT TOP 100 " +
                    "r.ID, " +
                    "r.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "gp.denumire AS denumire_grupa, " +
                    "r.cantitate, " +
                    "r.datasiora_i, " +
                    "r.ID_UTILIZATOR_I, " +
                    "r.datasiora_m, " +
                    "r.ID_UTILIZATOR_M " +
                    "FROM REALIZARI r " +
                    "LEFT JOIN PRODUSE p ON p.ID = r.ID_PRODUS " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON gp.ID = p.ID_GRUPA " +
                    "WHERE (r.ID_COMANDA IS NULL OR r.ID_COMANDA = 0) " +
                    whereCond +
                    "ORDER BY r.datasiora_i DESC ";

            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_ROLE() != 1 && user.getID_ROLE() != 2) {
                statement.setInt(1, user.getID());
            }
            ResultSet resultSet = statement.executeQuery();
            records.clear();
            while (resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId, resultSet.getString("denumire_grupa"));
                }
                Product product = new Product();
                product.setId(resultSet.getInt("ID_PRODUS"));
                product.setName(resultSet.getString("denumire"));
                product.setUnitMeasurement(resultSet.getString("um"));
                product.setGroup(group);

                ro.brutariabaiasprie.evidentaproductie.Domain.Record record = new Record();
                record.setId(resultSet.getInt("ID"));
                record.setProduct(product);
                record.setQuantity(resultSet.getDouble("cantitate"));
                record.setDateTimeInserted(resultSet.getTimestamp("datasiora_i"));
                record.setUserIdInserted(resultSet.getInt("ID_UTILIZATOR_I"));
                record.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
                record.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
                records.add(record);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadOrders() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            // if the user is not an adminstrator filter by the group of the user
            String whereCond = "";
            if(user.getID_ROLE() != 1 && user.getID_ROLE() != 2) {
                whereCond += "WHERE gp.ID = ? ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "gp.ID AS ID_GRUPA, " +
                    "gp.denumire AS denumire_grupa, " +
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
                    "LEFT JOIN GRUPE_PRODUSE gp ON gp.ID = p.ID_GRUPA " +
                    whereCond +
                    "GROUP BY c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "gp.ID, " +
                    "gp.denumire, " +
                    "c.cantitate, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "ORDER BY c.datasiora_i ASC ";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_ROLE() != 1 && user.getID_ROLE() != 2) {
                statement.setInt(1, user.getID_GROUP());
            }

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
            String sql = "SELECT * FROM GRUPE_PRODUSE";
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

    public void loadUsers() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM UTILIZATORI";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            users.clear();
            while (resultSet.next()) {
                User user = new User();
                user.setID(resultSet.getInt("ID"));
                user.setID_ROLE(resultSet.getInt("ID_ROL"));
                user.setUsername(resultSet.getString("nume_utilizator"));
                user.setPassword(resultSet.getString("parola"));
                user.setID_ROLE(resultSet.getInt("ID_GRUPA"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
