package ro.brutariabaiasprie.evidentaproductie.Domain;

import java.sql.Timestamp;

public class Record {
    private int id;
    private Product product;
    private double quantity;
    private Timestamp dateTimeInserted;
    private int userIdInserted;
    private Timestamp dateTimeModified;
    private int userIdModified;
    private int orderId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getDateTimeInserted() {
        return dateTimeInserted;
    }

    public void setDateTimeInserted(Timestamp dateTimeInserted) {
        this.dateTimeInserted = dateTimeInserted;
    }

    public int getUserIdInserted() {
        return userIdInserted;
    }

    public void setUserIdInserted(int userIdInserted) {
        this.userIdInserted = userIdInserted;
    }

    public Timestamp getDateTimeModified() {
        return dateTimeModified;
    }

    public void setDateTimeModified(Timestamp dateTimeModified) {
        this.dateTimeModified = dateTimeModified;
    }

    public int getUserIdModified() {
        return userIdModified;
    }

    public void setUserIdModified(int userIdModified) {
        this.userIdModified = userIdModified;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
