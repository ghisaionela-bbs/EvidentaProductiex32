package ro.brutariabaiasprie.evidentaproductie.DTO;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class Order {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final SimpleStringProperty productName = new SimpleStringProperty();
    private final SimpleStringProperty unitMeasurement = new SimpleStringProperty();
    private final DoubleProperty quantity = new SimpleDoubleProperty();
    private final DoubleProperty completed = new SimpleDoubleProperty();
    private final DoubleProperty remainder = new SimpleDoubleProperty();
    private final ObjectProperty<Timestamp> insertedDateTime = new SimpleObjectProperty<>();
    private final IntegerProperty insertedIdUser = new SimpleIntegerProperty();
    private final ObjectProperty<Timestamp> modifiedDateTime = new SimpleObjectProperty<>();
    private final IntegerProperty modifiedIdUser = new SimpleIntegerProperty();
    private final BooleanProperty closed = new SimpleBooleanProperty();

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getProductId() {
        return productId.get();
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }

    public String getProductName() {
        return productName.get();
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public String getUnitMeasurement() {
        return unitMeasurement.get();
    }

    public SimpleStringProperty unitMeasurementProperty() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement.set(unitMeasurement);
    }

    public double getQuantity() {
        return quantity.get();
    }

    public DoubleProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public double getCompleted() {
        return completed.get();
    }

    public DoubleProperty completedProperty() {
        return completed;
    }

    public void setCompleted(double completed) {
        this.completed.set(completed);
    }

    public double getRemainder() {
        return remainder.get();
    }

    public DoubleProperty remainderProperty() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder.set(remainder);
    }

    public Timestamp getInsertedDateTime() {
        return insertedDateTime.get();
    }

    public ObjectProperty<Timestamp> insertedDateTimeProperty() {
        return insertedDateTime;
    }

    public void setInsertedDateTime(Timestamp insertedDateTime) {
        this.insertedDateTime.set(insertedDateTime);
    }

    public int getInsertedIdUser() {
        return insertedIdUser.get();
    }

    public IntegerProperty insertedIdUserProperty() {
        return insertedIdUser;
    }

    public void setInsertedIdUser(int insertedIdUser) {
        this.insertedIdUser.set(insertedIdUser);
    }

    public Timestamp getModifiedDateTime() {
        return modifiedDateTime.get();
    }

    public ObjectProperty<Timestamp> modifiedDateTimeProperty() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(Timestamp modifiedDateTime) {
        this.modifiedDateTime.set(modifiedDateTime);
    }

    public int getModifiedIdUser() {
        return modifiedIdUser.get();
    }

    public IntegerProperty modifiedIdUserProperty() {
        return modifiedIdUser;
    }

    public void setModifiedIdUser(int modifiedIdUser) {
        this.modifiedIdUser.set(modifiedIdUser);
    }

    public boolean isClosed() {
        return closed.get();
    }

    public BooleanProperty closedProperty() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed.set(closed);
    }
}
