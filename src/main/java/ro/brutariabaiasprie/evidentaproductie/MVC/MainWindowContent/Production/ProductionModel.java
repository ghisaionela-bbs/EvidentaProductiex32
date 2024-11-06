package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Production;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.*;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.OrderNotFound;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.OrderItemNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.Calendar;

public class ProductionModel {
    private final ObservableList<ProductRecordDTO> productRecords;
    private final ObservableList<ProductDTO> products;
    private final ObservableList<OrderResultsDTO> orderSearchResults;
//    private final ObjectProperty<OrderDTO> associatedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<ProductDTO> selectedProduct = new SimpleObjectProperty<>();
//    private final IntegerProperty associatedOrder = new SimpleIntegerProperty(-1);
    private final ObjectProperty<OrderDTO> associatedOrder = new SimpleObjectProperty<>();

    public ProductionModel() {
        this.productRecords = FXCollections.observableArrayList();
        this.products = FXCollections.observableArrayList();
        this.orderSearchResults = FXCollections.observableArrayList();
    }

    public ObservableList<ProductRecordDTO> getProductRecords() {
        return productRecords;
    }

    public ObservableList<ProductDTO> getProducts() {
        return products;
    }

    public ObservableList<OrderResultsDTO> getOrderSearchResults() {
        return orderSearchResults;
    }

    public OrderDTO getAssociatedOrder() {
        return associatedOrder.get();
    }

    public ObjectProperty<OrderDTO> associatedOrderProperty() {
        return associatedOrder;
    }

    public void setAssociatedOrder(OrderDTO associatedOrder) {
        this.associatedOrder.set(associatedOrder);
    }

    public ProductDTO getSelectedProduct() {
        return selectedProduct.get();
    }

    public ObjectProperty<ProductDTO> selectedProductProperty() {
        return selectedProduct;
    }

    public void setSelectedProduct(ProductDTO selectedProduct) {
        this.selectedProduct.set(selectedProduct);
    }

    public void loadProductRecords() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String whereCond = "";
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            if(user.getID_ROLE() != 0 && user.getID_ROLE() != 1) {
                whereCond = "WHERE ip.ID_UTILIZATOR_I = ? ";
            }

            String sql = "SELECT ip.ID, p.ID AS PRODUCT_ID, p.denumire, p.um, ip.cantitate, ip.datasiora_i, ip.ID_UTILIZATOR_I, ip.ID_COMANDA " +
                    "FROM [dbo].[INREGISTRARI_PRODUSE] AS ip " +
                    "JOIN [dbo].[PRODUSE] AS p ON ip.ID_PRODUS = p.ID " +
                    whereCond +
                    "ORDER BY ip.datasiora_i DESC";

            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_ROLE() != 0 && user.getID_ROLE() != 1) {
                statement.setInt(1, user.getID());
            }
            ResultSet resultSet = statement.executeQuery();

            productRecords.clear();
            while(resultSet.next()) {
                int recordID = resultSet.getInt("ID");
                int productID = resultSet.getInt("PRODUCT_ID");
                String name = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                double quantity = resultSet.getDouble("cantitate");
                Timestamp dateAndTime = resultSet.getTimestamp("datasiora_i");
                int userID = resultSet.getInt("ID_UTILIZATOR_I");
                Integer orderID = resultSet.getInt("ID_COMANDA");
                if (resultSet.wasNull()) {
                    orderID = null;
                }
                ProductRecordDTO productRecordDTO = new ProductRecordDTO(recordID, productID, name, unitMeasurement,
                                                                            quantity, dateAndTime,
                                                                            userID, orderID);
                productRecords.add(productRecordDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadProducts() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String sql = "SELECT * FROM [dbo].[PRODUSE]";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            products.clear();
            while(resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                ProductDTO productDTO = new ProductDTO(ID, name, unitMeasurement);
                products.add(productDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProductRecordToDB(double quantity) {
        try {
            Connection connection = DBConnectionService.getConnection();

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            String sql = "";
            if(associatedOrder.get() != null) {
                sql = "INSERT INTO [dbo].[INREGISTRARI_PRODUSE] (ID_PRODUS, cantitate, datasiora_i, ID_UTILIZATOR_I, ID_COMANDA) VALUES (?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO [dbo].[INREGISTRARI_PRODUSE] (ID_PRODUS, cantitate, datasiora_i, ID_UTILIZATOR_I) VALUES (?, ?, ?, ?)";
            }

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, selectedProduct.get().getID());
            statement.setDouble(2, quantity);
            statement.setTimestamp(3, timestamp);
            statement.setInt(4, user.getID());
            if(associatedOrder.get() != null) {
                statement.setInt(5, associatedOrder.get().getID());
            }
            statement.executeUpdate();

            ResultSet key = statement.getGeneratedKeys();
            key.next();
            int ID = key.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void searchForOrders(ProductDTO product) throws SQLException, OrderNotFound {
        Connection connection = DBConnectionService.getConnection();

        String sql = "SELECT " +
                "c.ID AS ORDER_ID, " +
                "ic.ID AS ORDER_ITEM_ID, " +
                "p.ID AS PRODUCT_ID, " +
                "c.datasiora_i, " +
                "p.denumire, " +
                "ic.cantitate, " +
                "p.um " +
                "FROM [dbo].[COMENZI] AS c " +
                "LEFT JOIN [dbo].[ITEME_COMENZI] AS ic ON c.ID = ic.ID_COMANDA " +
                "LEFT JOIN [dbo].[PRODUSE] AS p ON p.ID = ic.ID_PRODUS " +
                "WHERE ic.ID_PRODUS = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, product.getID());
        ResultSet resultSet = statement.executeQuery();

        orderSearchResults.clear();
        if(!resultSet.next()){
            throw new OrderNotFound("No order was found for order:" + product.getID());
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



}
