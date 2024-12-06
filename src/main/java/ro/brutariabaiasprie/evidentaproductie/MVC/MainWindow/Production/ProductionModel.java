package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Production;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderResultsDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.OrderNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;

import java.sql.*;
import java.util.Calendar;

public class ProductionModel {
    private final ObservableList<Record> records = FXCollections.observableArrayList();

    public ObservableList<Record> getRecords() {
        return records;
    }

    public void loadRecords() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String whereCond = "";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    whereCond += "WHERE g.ID = ? ";
                    break;
                case OPERATOR:
                    whereCond += "WHERE r.ID_UTILIZATOR_I = ? ";
                    break;
                case UNAUTHORIZED:
                    whereCond += "WHERE 1=0 ";
            }

            String sql = " SELECT TOP 100 " +
                    "r.ID, " +
                    "r.ID_COMANDA, " +
                    "r.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "g.denumire AS denumire_grupa, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "r.cantitate, " +
                    "r.datasiora_i, " +
                    "r.ID_UTILIZATOR_I, " +
                    "r.datasiora_m, " +
                    "r.ID_UTILIZATOR_M " +
                    "FROM REALIZARI r " +
                    "LEFT JOIN COMENZI c ON c.ID = r.ID_COMANDA " +
                    "LEFT JOIN PRODUSE p ON p.ID = r.ID_PRODUS " +
                    "LEFT JOIN GRUPE_PRODUSE g ON g.ID = p.ID_GRUPA " +
                    "LEFT JOIN GRUPE_PRODUSE subg ON subg.ID = p.ID_SUBGRUPA_PRODUSE " +
                    whereCond +
                    "ORDER BY r.datasiora_i DESC ";

            PreparedStatement statement = connection.prepareStatement(sql);
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    statement.setInt(1, ConfigApp.getUser().getGroupId());
                    break;
                case OPERATOR:
                    statement.setInt(1, ConfigApp.getUser().getId());
                    break;
                case UNAUTHORIZED:
                    break;
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
                product.setSubgroupId(resultSet.getInt("ID_SUBGRUPA_PRODUSE"));

                Record record = new Record();
                record.setId(resultSet.getInt("ID"));
                record.setProduct(product);
                record.setQuantity(resultSet.getDouble("cantitate"));
                record.setDateTimeInserted(resultSet.getTimestamp("datasiora_i"));
                record.setUserIdInserted(resultSet.getInt("ID_UTILIZATOR_I"));
                record.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
                record.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
                record.setOrderId(resultSet.getInt("ID_COMANDA"));
                records.add(record);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private final ObservableList<Product> products;
    private final ObservableList<OrderResultsDTO> orderSearchResults;
    private final ObjectProperty<Product> selectedProduct = new SimpleObjectProperty<>();
    private final ObjectProperty<Order> associatedOrder = new SimpleObjectProperty<>();

    public ProductionModel() {
        this.products = FXCollections.observableArrayList();
        this.orderSearchResults = FXCollections.observableArrayList();
    }

    public ObservableList<Product> getProducts() {
        return products;
    }


    public Order getAssociatedOrder() {
        return associatedOrder.get();
    }

    public ObjectProperty<Order> associatedOrderProperty() {
        return associatedOrder;
    }

    public void setAssociatedOrder(Order associatedOrder) {
        this.associatedOrder.set(associatedOrder);
    }

    public Product getSelectedProduct() {
        return selectedProduct.get();
    }

    public ObjectProperty<Product> selectedProductProperty() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct.set(selectedProduct);
    }

    public void loadProducts() {
        try {
            String whereCond = "";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    whereCond += "WHERE gp.ID = ? ";
                    break;
                case OPERATOR:
                    whereCond += "WHERE gp.ID = ? AND subg.ID = ? ";
                    break;
                case UNAUTHORIZED:
                    whereCond += "WHERE 1=0 ";
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
                    "LEFT JOIN GRUPE_PRODUSE subg ON subg.ID = p.ID_SUBGRUPA_PRODUSE " +
                    whereCond +
                    "ORDER BY p.um, p.denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    statement.setInt(1, ConfigApp.getUser().getGroupId());
                    break;
                case OPERATOR:
                    statement.setInt(1, ConfigApp.getUser().getGroupId());
                    statement.setInt(2, ConfigApp.getUser().getSubgroupId());
                    break;
                case UNAUTHORIZED:
                    break;
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
                        resultSet.getDouble("sarja"),
                        resultSet.getString("um"),
                        group,
                        resultSet.getInt("ID_SUBGRUPA_PRODUSE")
                );
                product.setGroup(group);
                products.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProductRecordToDB(double quantity) {
        try {
            Connection connection = DBConnectionService.getConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            String sql = "";
            if(associatedOrder.get() != null) {
                sql = "INSERT INTO REALIZARI (ID_PRODUS, cantitate, datasiora_i, ID_UTILIZATOR_I, ID_COMANDA) VALUES (?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO REALIZARI (ID_PRODUS, cantitate, datasiora_i, ID_UTILIZATOR_I) VALUES (?, ?, ?, ?)";
            }

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, selectedProduct.get().getId());
            statement.setDouble(2, quantity);
            statement.setTimestamp(3, timestamp);
            statement.setInt(4, ConfigApp.getUser().getId());
            statement.setInt(5, associatedOrder.get().getId());
            statement.executeUpdate();
            ResultSet key = statement.getGeneratedKeys();
            key.next();
            int ID = key.getInt(1);

            String selectSql = "SELECT SUM(COALESCE(r.cantitate, 0.00)) AS realizat, c.cantitate from COMENZI AS c " +
                    "LEFT JOIN REALIZARI AS r ON r.ID_COMANDA = c.ID " +
                    "WHERE c.ID = ? " +
                    "GROUP BY c.cantitate ";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, associatedOrder.get().getId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            double completed = resultSet.getDouble("realizat");
            double orderQuantity = resultSet.getDouble("cantitate");

            if(completed >= orderQuantity) {
                String closeOrderSql = "UPDATE COMENZI SET inchisa = 1 WHERE ID = ?";
                PreparedStatement closeOrderStatement = connection.prepareStatement(closeOrderSql);
                closeOrderStatement.setInt(1, associatedOrder.get().getId());
                closeOrderStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void searchForOrders(Product product) throws SQLException, OrderNotFound {
        Connection connection = DBConnectionService.getConnection();

        String sql = "SELECT " +
                "c.ID AS ORDER_ID, " +
                "p.ID AS PRODUCT_ID, " +
                "c.cantitate " +
                "c.datasiora_i, " +
                "p.denumire, " +
                "p.um " +
                "FROM COMENZI AS c " +
                "LEFT JOIN PRODUSE AS p ON p.ID = c.ID_PRODUS " +
                "WHERE c.ID_PRODUS = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, product.getId());
        ResultSet resultSet = statement.executeQuery();

        orderSearchResults.clear();
        if(!resultSet.next()){
            throw new OrderNotFound("No order was found for order:" + product.getId());
        } else {
            do {
                orderSearchResults.add(getOrderResultFromResultset(resultSet));
            } while (resultSet.next());
        }
    }

    private OrderResultsDTO getOrderResultFromResultset(ResultSet resultSet) throws SQLException {
        int ORDER_ID = resultSet.getInt("ORDER_ID");
        Timestamp orderDateAndTime = resultSet.getTimestamp("datasiora_i");
        String productName = resultSet.getString("denumire");
        double quantity = resultSet.getDouble("cantitate");
        String unitMeasurement = resultSet.getString("um");
        return new OrderResultsDTO(ORDER_ID, orderDateAndTime, productName, quantity, unitMeasurement);
    }

    public void loadSelectedProduct() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT TOP 1 " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
                    "WHERE p.ID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, selectedProduct.get().getId());
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            Group group = null;
            int groupId = resultSet.getInt("ID_GRUPA");
            if(!resultSet.wasNull()) {
                group = new Group(groupId,
                        resultSet.getString("denumire_grupa"));
            }
            Product product = new Product(
                    selectedProduct.get().getId(),
                    resultSet.getString("denumire"),
                    resultSet.getDouble("sarja"),
                    resultSet.getString("um"),
                    group,
                    resultSet.getInt("ID_SUBGRUPA_PRODUSE")
            );
            product.setGroup(group);
            this.selectedProduct.set(product);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAssociatedOrder() {
        try {
            if(associatedOrder.get() == null) {
                return;
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.sarja, " +
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
                    "WHERE c.ID = ? " +
                    "GROUP BY c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "g.ID, " +
                    "g.denumire, " +
                    "c.cantitate, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, this.associatedOrder.get().getId());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

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
            order.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
            order.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
            order.setClosed(resultSet.getBoolean("inchisa"));

            this.setAssociatedOrder(order);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
