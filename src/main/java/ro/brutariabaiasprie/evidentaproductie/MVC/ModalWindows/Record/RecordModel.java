package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Record;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.Calendar;

public class RecordModel {
    private Record record;
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public ObservableList<Order> getOrders() {
        return orders;
    }

    public void updateRecord() {
        try {
            Connection connection = DBConnectionService.getConnection();

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            String sql = "UPDATE REALIZARI SET " +
                    "ID_PRODUS = ?, " +
                    "cantitate = ?, " +
                    "datasiora_i = ?, " +
                    "ID_UTILIZATOR_I = ?, " +
                    "datasiora_m = ?, " +
                    "ID_UTILIZATOR_M = ?, " +
                    "ID_COMANDA = ? " +
                    "WHERE ID = ? ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, record.getProduct().getId());
            statement.setDouble(2, record.getQuantity());
            statement.setTimestamp(3, record.getDateTimeInserted());
            statement.setInt(4, record.getUserIdInserted());
            statement.setTimestamp(5, timestamp);
            statement.setInt(6, user.getId());
            if(record.getOrderId() == 0) {
                statement.setNull(7, Types.INTEGER);
            } else {
                statement.setInt(7, record.getOrderId());
            }
            statement.setInt(8, record.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadProducts() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String whereCond = "";
            if(user.getGroupId() != 0) {
                whereCond += "WHERE g.ID = ? ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT p.ID, p.denumire, p.UM, p.ID_GRUPA, p.ID_SUBGRUPA_PRODUSE, g.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE g ON p.ID_GRUPA = g.ID " +
                    whereCond +
                    "ORDER BY p.UM, p.denumire";

            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getGroupId() != 0) {
                statement.setInt(1, user.getRoleId());
            }
            ResultSet resultSet = statement.executeQuery();

            products.clear();
            while(resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId, resultSet.getString("denumire_grupa"));
                }
                Product product = new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getString("um"),
                        group,
                        resultSet.getInt("ID_SUBGRUPA_PRODUSE")
                );
                products.add(product);
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
            if(user.getRoleId() != 1 && user.getRoleId() != 2) {
                whereCond += " AND g.ID = ? ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "g.ID AS ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
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
                    "LEFT JOIN GRUPE_PRODUSE g ON g.ID = p.ID_GRUPA " +
                    "WHERE 1=1 AND p.ID = c.ID_PRODUS " +
                    whereCond +
                    "GROUP BY c.ID, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
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
                    "ORDER BY c.datasiora_i ASC ";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getRoleId() != 1 && user.getRoleId() != 2) {
                statement.setInt(1, user.getGroupId());
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
                        group,
                        resultSet.getInt("ID_SUBGRUPA_PRODUSE")
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


}
