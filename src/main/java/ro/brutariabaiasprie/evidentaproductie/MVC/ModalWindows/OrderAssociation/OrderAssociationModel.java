package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.OrderNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class OrderAssociationModel {
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObjectProperty<Product> product;
    private Order order;

    public OrderAssociationModel(Product product) {
        this.product = new SimpleObjectProperty<>(product);
    }

    public ObservableList<Order> getOrders() {
        return orders;
    }

    public Product getProduct() {
        return product.get();
    }

    public ObjectProperty<Product> productProperty() {
        return product;
    }

    public void setProduct(Product product) {
        this.product.set(product);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void loadSearchResults() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String sql = "SELECT " +
                    "c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "g.ID AS ID_GRUPA, " +
                    "g.denumire  AS denumire_grupa, " +
                    "c.cantitate, " +
                    "SUM(COALESCE(r.cantitate, 0.00)) AS realizat, " +
                    "c.cantitate - SUM(COALESCE(r.cantitate, 0.00)) AS rest, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa  " +
                    "FROM COMENZI c LEFT JOIN PRODUSE p ON p.ID = c.ID_PRODUS " +
                    "LEFT JOIN REALIZARI r ON r.ID_COMANDA = c.ID " +
                    "LEFT JOIN GRUPE g ON g.ID = p.ID_GRUPA " +
                    "WHERE p.ID = ? " +
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
                    "ORDER BY c.datasiora_i ASC ";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, product.get().getId());
            ResultSet resultSet = statement.executeQuery();

            orders.clear();
            while(resultSet.next()){
                orders.add(getOrderFromResultset(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Order getOrderFromResultset(ResultSet resultSet) throws SQLException {
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
        return order;
    }

}
