package ro.brutariabaiasprie.evidentaproductie.DatabaseObjects;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OUser {
    SimpleIntegerProperty idProperty = new SimpleIntegerProperty();
    SimpleStringProperty usernameProperty = new SimpleStringProperty();
    SimpleStringProperty passwordProperty = new SimpleStringProperty();
    SimpleIntegerProperty roleIdProperty = new SimpleIntegerProperty();

    public int getIdProperty() {
        return idProperty.get();
    }

    public SimpleIntegerProperty idPropertyProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty.set(idProperty);
    }

    public String getUsernameProperty() {
        return usernameProperty.get();
    }

    public SimpleStringProperty usernamePropertyProperty() {
        return usernameProperty;
    }

    public void setUsernameProperty(String usernameProperty) {
        this.usernameProperty.set(usernameProperty);
    }

    public String getPasswordProperty() {
        return passwordProperty.get();
    }

    public SimpleStringProperty passwordPropertyProperty() {
        return passwordProperty;
    }

    public void setPasswordProperty(String passwordProperty) {
        this.passwordProperty.set(passwordProperty);
    }

    public int getRoleIdProperty() {
        return roleIdProperty.get();
    }

    public SimpleIntegerProperty roleIdPropertyProperty() {
        return roleIdProperty;
    }

    public void setRoleIdProperty(int roleIdProperty) {
        this.roleIdProperty.set(roleIdProperty);
    }
}
