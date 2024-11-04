package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class OrderItemReportDTO {
    private int ORDER_ID;
    private int ORDER_ITEM_ID;
    private int PRODUCT_ID;
    private Timestamp orderDateAndTimeInserted;
    private String productName;
    private String unitMeasurement;
    private double quantity;
    private double completed;
    private double remainder;

    public OrderItemReportDTO(int ORDER_ID, int ORDER_ITEM_ID, Timestamp orderDateAndTimeInserted,
                              int PRODUCT_ID, String productName, String unitMeasurement, double quantity,
                              double completed, double remainder) {
        this.ORDER_ID = ORDER_ID;
        this.ORDER_ITEM_ID = ORDER_ITEM_ID;
        this.orderDateAndTimeInserted = orderDateAndTimeInserted;
        this.PRODUCT_ID = PRODUCT_ID;
        this.productName = productName;
        this.unitMeasurement = unitMeasurement;
        this.quantity = quantity;
        this.completed = completed;
        this.remainder = remainder;
    }

    public int getORDER_ID() {
        return ORDER_ID;
    }

    public void setORDER_ID(int ORDER_ID) {
        this.ORDER_ID = ORDER_ID;
    }

    public int getORDER_ITEM_ID() {
        return ORDER_ITEM_ID;
    }

    public void setORDER_ITEM_ID(int ORDER_ITEM_ID) {
        this.ORDER_ITEM_ID = ORDER_ITEM_ID;
    }

    public Timestamp getOrderDateAndTimeInserted() {
        return orderDateAndTimeInserted;
    }

    public void setOrderDateAndTimeInserted(Timestamp orderDateAndTimeInserted) {
        this.orderDateAndTimeInserted = orderDateAndTimeInserted;
    }

    public int getPRODUCT_ID() {
        return PRODUCT_ID;
    }

    public void setPRODUCT_ID(int PRODUCT_ID) {
        this.PRODUCT_ID = PRODUCT_ID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCompleted() {
        return completed;
    }

    public void setCompleted(double completed) {
        this.completed = completed;
    }

    public double getRemainder() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder = remainder;
    }
}
