package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.EditProductRecord;

import javafx.beans.property.*;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.OrderItemNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditProductRecordModel {
    private final ObjectProperty<ProductRecordDTO> productRecord = new SimpleObjectProperty<>();
    private final StringProperty productName = new SimpleStringProperty();
    private final StringProperty unitMeasurement = new SimpleStringProperty();
    private final SimpleObjectProperty<Integer> orderID = new SimpleObjectProperty<>();
    private double quantity;

    private ProductDTO product;

    public EditProductRecordModel(ProductRecordDTO productRecord) {
        this.productRecord.set(productRecord);
        this.productName.set(productRecord.getName());
        this.unitMeasurement.set(productRecord.getUnitMeasurement());
        this.orderID.set(productRecord.getORDER_ID());
        product = new ProductDTO(productRecord.getPRODUCT_ID(), productRecord.getName(), productRecord.getUnitMeasurement());
    }

    public ProductRecordDTO getProductRecord() {
        return productRecord.get();
    }

    public ObjectProperty<ProductRecordDTO> productRecordProperty() {
        return productRecord;
    }

    public void setProductRecord(ProductRecordDTO productRecord) {
        this.productRecord.set(productRecord);
    }

    public String getProductName() {
        return productName.get();
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public String getUnitMeasurement() {
        return unitMeasurement.get();
    }

    public StringProperty unitMeasurementProperty() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement.set(unitMeasurement);
    }

    public ProductDTO getProduct() {
        return product;
    }

    public Integer getOrderID() {
        return orderID.get();
    }

    public SimpleObjectProperty<Integer> orderIDProperty() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID.set(orderID);
        this.productRecord.get().setORDER_ID(orderID);
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
        this.productRecord.get().setPRODUCT_ID(product.getID());
        this.productRecord.get().setName(product.getName());
        this.productRecord.get().setUnitMeasurement(product.getUnitMeasurement());
        this.productName.set(product.getName());
        this.unitMeasurement.set(product.getUnitMeasurement());
    }

    public boolean checkOrderItems(ProductDTO product) {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT ic.ID_PRODUS FROM [dbo].[ITEME_COMENZI] AS ic\n" +
                    "WHERE ic.ID_PRODUS = ? AND ic.ID_COMANDA = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, product.getID());
            statement.setInt(2, productRecord.get().getORDER_ID());
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                throw new OrderItemNotFound("Produsul " + product.getName() + " nu a fost comandat in comanda: " + productRecord.get().getORDER_ID());
            }
            return true;
        } catch (OrderItemNotFound e) {
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProductRecord(double quantity) {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "UPDATE [dbo].[INREGISTRARI_PRODUSE] SET " +
                    "ID_PRODUS = ?, " +
                    "cantitate = ?, " +
                    "datasiora_m = ?, " +
                    "ID_UTILIZATOR_M = ?, " +
                    "ID_COMANDA = ? " +
                    "WHERE ID = ?";

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, product.getID());
            statement.setDouble(2, quantity);
            statement.setTimestamp(3, timestamp);
            statement.setInt(4, user.getID());
            statement.setObject(5, orderID.get(), java.sql.Types.INTEGER);
            statement.setInt(6, productRecord.get().getID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
