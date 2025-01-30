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
//            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
//            String whereCond = "";
//            if(user.getGroupId() != 0) {
//                whereCond += "WHERE gp.ID = ? ";
//            }
            String whereCond = "";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    whereCond += "WHERE gp.ID = ? ";
                    break;
                case OPERATOR:
                case UNAUTHORIZED:
                    whereCond += "WHERE 1=0 ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.UM, " +
                    "p.ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
                    whereCond +
                    "ORDER BY p.UM, p.denumire";

            PreparedStatement statement = connection.prepareStatement(sql);
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    statement.setInt(1, ConfigApp.getUser().getGroupId());
                    break;
                case OPERATOR:
                case UNAUTHORIZED:
                    break;
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
                        resultSet.getDouble("sarja"),
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

    public void addOrder() {
        try {
            Connection connection = DBConnectionService.getConnection();

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            String sql = "INSERT INTO COMENZI " +
                    "(data_programata, " +
                    "ID_PRODUS, " +
                    "cantitate, " +
                    "datasiora_i, " +
                    "ID_UTILIZATOR_I, " +
                    "datasiora_m, " +
                    "ID_UTILIZATOR_M, " +
                    "inchisa, " +
                    "contor) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?," +
                    "(SELECT int_value + 1 FROM utils WHERE property_name = ?))";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setTimestamp(1, order.getDateScheduled());
            statement.setInt(2, order.getProduct().getId());
            statement.setDouble(3, order.getQuantity());
            statement.setTimestamp(4, timestamp);
            statement.setInt(5, user.getId());
            statement.setNull(6, Types.TIMESTAMP);
            statement.setNull(7, Types.INTEGER);
            statement.setBoolean(8, false);
            statement.setString(9, "order_daily_counter");
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
                    "data_programata = ?, " +
                    "cantitate = ?, " +
                    "datasiora_i = ?, " +
                    "ID_UTILIZATOR_I = ?, " +
                    "datasiora_m = ?, " +
                    "ID_UTILIZATOR_M = ?, " +
                    "inchisa = ? " +
                    "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            int paramCount = 1;
            statement.setInt(paramCount, order.getProduct().getId());
            paramCount += 1;
            statement.setTimestamp(paramCount, order.getDateScheduled());
            paramCount += 1;
            statement.setDouble(paramCount, order.getQuantity());
            paramCount += 1;
            statement.setTimestamp(paramCount, order.getDateTimeInserted());
            paramCount += 1;
            statement.setInt(paramCount, order.getUserIdInserted());
            paramCount += 1;
            statement.setTimestamp(paramCount, timestamp);
            paramCount += 1;
//            if(order.getDateTimeModified() == null) {
//                statement.setNull(paramCount, Types.TIMESTAMP);
//                paramCount += 1;
//            } else {
//            statement.setTimestamp(paramCount, order.getDateTimeModified());
//            paramCount += 1;
//            }
            statement.setInt(paramCount, ConfigApp.getUser().getId());
            paramCount += 1;
//            if(order.getUserIdModified() == 0) {
//                statement.setNull(paramCount, Types.INTEGER);
//                paramCount += 1;
//            } else {
//                statement.setInt(paramCount, order.getUserIdModified());
//                paramCount += 1;
//            }
            statement.setBoolean(paramCount, order.isClosed());
            paramCount += 1;
            statement.setInt(paramCount, order.getId());
            paramCount += 1;
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
