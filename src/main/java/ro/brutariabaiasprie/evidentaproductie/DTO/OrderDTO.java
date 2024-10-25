package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class OrderDTO {
    private int ID;
    private int PRODUCT_ID;
    private double quantity;
    private Timestamp dateAndTimeInserted;

    public OrderDTO(int ID, int PRODUCT_ID, double quantity, Timestamp dateAndTimeInserted) {
        this.ID = ID;
        this.PRODUCT_ID = PRODUCT_ID;
        this.quantity = quantity;
        this.dateAndTimeInserted = dateAndTimeInserted;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPRODUCT_ID() {
        return PRODUCT_ID;
    }

    public void setPRODUCT_ID(int PRODUCT_ID) {
        this.PRODUCT_ID = PRODUCT_ID;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getDateAndTimeInserted() {
        return dateAndTimeInserted;
    }

    public void setDateAndTimeInserted(Timestamp dateAndTimeInserted) {
        this.dateAndTimeInserted = dateAndTimeInserted;
    }
}
