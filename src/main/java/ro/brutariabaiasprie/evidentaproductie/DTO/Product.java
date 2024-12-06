package ro.brutariabaiasprie.evidentaproductie.DTO;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty batchValue = new SimpleDoubleProperty(0.00);
    private final StringProperty unitMeasurement = new SimpleStringProperty();
    private final IntegerProperty groupId = new SimpleIntegerProperty();
    private final StringProperty groupName = new SimpleStringProperty();

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getBatchValue() {
        return batchValue.get();
    }

    public DoubleProperty batchValueProperty() {
        return batchValue;
    }

    public void setBatchValue(double batchValue) {
        this.batchValue.set(batchValue);
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

    public int getGroupId() {
        return groupId.get();
    }

    public IntegerProperty groupIdProperty() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId.set(groupId);
    }

    public String getGroupName() {
        return groupName.get();
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }
}
