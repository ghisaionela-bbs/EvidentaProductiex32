package ro.brutariabaiasprie.evidentaproductie.DatabaseObjects;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OProduct {
    SimpleIntegerProperty idProperty = new SimpleIntegerProperty();
    SimpleStringProperty nameProperty = new SimpleStringProperty();
    SimpleStringProperty unitMeasurementProperty = new SimpleStringProperty();
    SimpleIntegerProperty groupIdProperty = new SimpleIntegerProperty();

    public int getGroupIdProperty() {
        return groupIdProperty.get();
    }

    public SimpleIntegerProperty groupIdPropertyProperty() {
        return groupIdProperty;
    }

    public void setGroupIdProperty(int groupIdProperty) {
        this.groupIdProperty.set(groupIdProperty);
    }

    public String getUnitMeasurementProperty() {
        return unitMeasurementProperty.get();
    }

    public SimpleStringProperty unitMeasurementPropertyProperty() {
        return unitMeasurementProperty;
    }

    public void setUnitMeasurementProperty(String unitMeasurementProperty) {
        this.unitMeasurementProperty.set(unitMeasurementProperty);
    }

    public String getNameProperty() {
        return nameProperty.get();
    }

    public SimpleStringProperty namePropertyProperty() {
        return nameProperty;
    }

    public void setNameProperty(String nameProperty) {
        this.nameProperty.set(nameProperty);
    }

    public int getIdProperty() {
        return idProperty.get();
    }

    public SimpleIntegerProperty idPropertyProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty.set(idProperty);
    }
}
