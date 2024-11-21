package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.Calendar;

public class OrderModel {
    private Order order;
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public void loadProducts() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String whereCond = "";
            if(user.getID_GROUP() != 0) {
                whereCond += "WHERE gp.ID = ? ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT p.ID, p.denumire, p.UM, p.ID_GRUPA, gp.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
                    whereCond +
                    "ORDER BY p.UM, p.denumire";

            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_GROUP() != 0) {
                statement.setInt(1, user.getID_ROLE());
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
                        group
                );
                products.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addOrder() {
        try {
            Connection connection = DBConnectionService.getConnection();

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            String sql = "INSERT INTO COMENZI " +
                    "(ID_PRODUS, cantitate, datasiora_i, ID_UTILIZATOR_I, datasiora_m, ID_UTILIZATOR_M, inchisa) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, order.getProduct().getId());
            statement.setDouble(2, order.getQuantity());
            statement.setTimestamp(3, timestamp);
            statement.setInt(4, user.getID());
            statement.setNull(5, Types.TIMESTAMP);
            statement.setNull(6, Types.INTEGER);
            statement.setBoolean(7, false);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrder(boolean withDissociation) {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "UPDATE COMENZI SET " +
                    "ID_PRODUS = ?, " +
                    "cantitate = ?, " +
                    "datasiora_i = ?, " +
                    "ID_UTILIZATOR_I = ?, " +
                    "datasiora_m = ?, " +
                    "ID_UTILIZATOR_M = ?, " +
                    "inchisa = ? " +
                    "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order.getProduct().getId());
            statement.setDouble(2, order.getQuantity());
            statement.setTimestamp(3, order.getDateTimeInserted());
            statement.setInt(4, order.getUserIdInserted());
            if(order.getDateTimeModified() == null) {
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(5, order.getDateTimeModified());
            }
            if(order.getUserIdModified() == 0) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, order.getUserIdModified());
            }
            statement.setBoolean(7, order.isClosed());
            statement.setInt(8, order.getId());
            statement.executeUpdate();

            if(withDissociation) {
                String sqlRecordUpdate = "UPDATE REALIZARI SET ID_COMANDA = ? WHERE ID_COMANDA = ?";
                PreparedStatement recordUpdateStatement = connection.prepareStatement(sqlRecordUpdate);
                recordUpdateStatement.setNull(1, Types.INTEGER);
                recordUpdateStatement.setNull(2, Types.INTEGER);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteOrder() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "DELETE FROM COMENZI WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasRecords() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT TOP 1 * FROM REALIZARI WHERE ID_COMANDA = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order.getId());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
