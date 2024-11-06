package ro.brutariabaiasprie.evidentaproductie.DatabaseObjects;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class OProductGroup {
    SimpleIntegerProperty idProperty = new SimpleIntegerProperty();
    SimpleStringProperty nameProperty = new SimpleStringProperty();

    public int getIdProperty() {
        return idProperty.get();
    }

    public SimpleIntegerProperty idPropertyProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty.set(idProperty);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OProductGroup that = (OProductGroup) o;
        return Objects.equals(idProperty.get(), that.idProperty.get()) && Objects.equals(nameProperty.get(), that.nameProperty.get());
    }


}
