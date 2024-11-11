package ro.brutariabaiasprie.evidentaproductie.DTO;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class Order {
    private IntegerProperty idProperty = new SimpleIntegerProperty();
    private ObjectProperty<Timestamp> insertedDateTimeProperty = new SimpleObjectProperty<>();
    private IntegerProperty insertedIdUser = new SimpleIntegerProperty();
    private ObjectProperty<Timestamp> modifiedDateTimeProperty = new SimpleObjectProperty<>();
    private IntegerProperty modifiedIdUser = new SimpleIntegerProperty();
    private BooleanProperty closedProperty = new SimpleBooleanProperty();

    public Order(IntegerProperty idProperty,
                 ObjectProperty<Timestamp> insertedDateTimeProperty,
                 IntegerProperty insertedIdUser,
                 ObjectProperty<Timestamp> modifiedDateTimeProperty,
                 IntegerProperty modifiedIdUser,
                 BooleanProperty closedProperty) {
        this.idProperty = idProperty;
        this.insertedDateTimeProperty = insertedDateTimeProperty;
        this.insertedIdUser = insertedIdUser;
        this.modifiedDateTimeProperty = modifiedDateTimeProperty;
        this.modifiedIdUser = modifiedIdUser;
        this.closedProperty = closedProperty;
    }

    public int getIdProperty() {
        return idProperty.get();
    }

    public IntegerProperty idPropertyProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty.set(idProperty);
    }

    public Timestamp getInsertedDateTimeProperty() {
        return insertedDateTimeProperty.get();
    }

    public ObjectProperty<Timestamp> insertedDateTimePropertyProperty() {
        return insertedDateTimeProperty;
    }

    public void setInsertedDateTimeProperty(Timestamp insertedDateTimeProperty) {
        this.insertedDateTimeProperty.set(insertedDateTimeProperty);
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

    public Timestamp getModifiedDateTimeProperty() {
        return modifiedDateTimeProperty.get();
    }

    public ObjectProperty<Timestamp> modifiedDateTimePropertyProperty() {
        return modifiedDateTimeProperty;
    }

    public void setModifiedDateTimeProperty(Timestamp modifiedDateTimeProperty) {
        this.modifiedDateTimeProperty.set(modifiedDateTimeProperty);
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

    public boolean isClosedProperty() {
        return closedProperty.get();
    }

    public BooleanProperty closedPropertyProperty() {
        return closedProperty;
    }

    public void setClosedProperty(boolean closedProperty) {
        this.closedProperty.set(closedProperty);
    }
}
