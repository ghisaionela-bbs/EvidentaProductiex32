package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
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

    public void loadSearchResults(boolean showClosedOrders) {
        try {
            Connection connection = DBConnectionService.getConnection();

            String whereCond = "";
            if(!showClosedOrders) {
                whereCond = " AND c.inchisa = 0 ";
            }

            String sql = "SELECT " +
                    "c.ID, " +
                    "c.contor, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "g.ID AS ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "g.denumire  AS denumire_grupa, " +
                    "c.cantitate, " +
                    "SUM(COALESCE(r.cantitate, 0.00)) AS realizat, " +
                    "c.cantitate - SUM(COALESCE(r.cantitate, 0.00)) AS rest, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.data_programata, " +
                    "c.inchisa  " +
                    "FROM COMENZI c LEFT JOIN PRODUSE p ON p.ID = c.ID_PRODUS " +
                    "LEFT JOIN REALIZARI r ON r.ID_COMANDA = c.ID " +
                    "LEFT JOIN GRUPE_PRODUSE g ON g.ID = p.ID_GRUPA " +
                    "WHERE p.ID = ? AND c.inchisa = 0 " +
                    whereCond +
                    "GROUP BY c.ID, " +
                    "c.contor, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "g.ID, " +
                    "g.denumire, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "c.cantitate, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.data_programata, " +
                    "c.inchisa " +
                    "ORDER BY c.datasiora_i ASC ";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, product.get().getId());
            ResultSet resultSet = statement.executeQuery();

            orders.clear();
            while(resultSet.next()){
                orders.add(getOrderFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Order getOrderFromResultSet(ResultSet resultSet) throws SQLException {
        Group group = null;
        int groupId = resultSet.getInt("ID_GRUPA");
        if(!resultSet.wasNull()) {
            group = new Group(groupId, resultSet.getString("denumire_grupa"));
        }

        Order order = new Order();
        order.setId(resultSet.getInt("ID"));
        order.setCounter(resultSet.getInt("contor"));
        order.setProduct(new Product(
                resultSet.getInt("ID_PRODUS"),
                resultSet.getString("denumire"),
                resultSet.getDouble("sarja"),
                resultSet.getString("um"),
                group,
                resultSet.getInt("ID_SUBGRUPA_PRODUSE")
        ));
        order.setQuantity(resultSet.getDouble("cantitate"));
        order.setCompleted(resultSet.getDouble("realizat"));
        order.setRemainder(resultSet.getDouble("rest"));
        order.setDateTimeInserted(resultSet.getTimestamp("datasiora_i"));
        order.setUserIdInserted(resultSet.getInt("ID_UTILIZATOR_I"));
        order.setDateScheduled(resultSet.getTimestamp("data_programata"));
        order.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
        order.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
        order.setClosed(resultSet.getBoolean("inchisa"));
        return order;
    }

}
