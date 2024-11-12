package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.Order;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class OrderModel {
    private Order order;
    private final ObservableList<ProductDTO> products = FXCollections.observableArrayList();

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ObservableList<ProductDTO> getProductList() {
        return products;
    }

    public void loadProducts() {
        try {
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String whereCond = "";
            if(user.getID_GROUP() != null) {
                whereCond += "WHERE g.ID = ?";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                        "p.ID, " +
                        "p.denumire, " +
                        "p.um, " +
                        "p.ID_GRUPA, " +
                        "g.denumire " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE g ON p.ID_GRUPA = g.ID " +
                    whereCond;

            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_GROUP() != null) {
                statement.setInt(1, user.getID_ROLE());
            }
            ResultSet resultSet = statement.executeQuery();

            products.clear();
            while(resultSet.next()) {
                ProductDTO productDTO;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addOrder() {
        //TODO
    }

    public void updateOrder() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "UPDATE COMENZI SET " +
                    "ID_PRODUS = ?, " +
                    "cantitate = ?, " +
                    "datasiora_i = ?, " +
                    "ID_UTILIZATOR_I = ?, " +
                    "datasiora_m = ?, " +
                    "inchisa = ? " +
                    "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order.getProductId());
            statement.setDouble(2, order.getQuantity());
            statement.setTimestamp(3, order.getInsertedDateTime());
            statement.setInt(4, order.getInsertedIdUser());
            if(order.modifiedDateTimeProperty() == null) {
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(5, order.getModifiedDateTime());
            }
            if(order.modifiedIdUserProperty() == null) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, order.getModifiedIdUser());
            }
            statement.setBoolean(7, order.isClosed());
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
}
