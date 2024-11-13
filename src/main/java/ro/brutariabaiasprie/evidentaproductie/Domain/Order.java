package ro.brutariabaiasprie.evidentaproductie.Domain;

import ro.brutariabaiasprie.evidentaproductie.Data.User;

import java.sql.Timestamp;

public class Order {
    private int id;
    private Product product;
    private Timestamp dateTimeInserted;
    private User userInserted;
    private Timestamp dateTimeModified;
    private User userModified;

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

    public Timestamp getDateTimeInserted() {
        return dateTimeInserted;
    }

    public void setDateTimeInserted(Timestamp dateTimeInserted) {
        this.dateTimeInserted = dateTimeInserted;
    }

    public User getUserInserted() {
        return userInserted;
    }

    public void setUserInserted(User userInserted) {
        this.userInserted = userInserted;
    }

    public Timestamp getDateTimeModified() {
        return dateTimeModified;
    }

    public void setDateTimeModified(Timestamp dateTimeModified) {
        this.dateTimeModified = dateTimeModified;
    }

    public User getUserModified() {
        return userModified;
    }

    public void setUserModified(User userModified) {
        this.userModified = userModified;
    }
}
