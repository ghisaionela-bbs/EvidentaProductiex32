package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class OrderResultsDTO {
    private int ORDER_ID;
    private Timestamp orderDateAndTime;
    private String productName;
    private double quantity;
    private String unitMeasurement;

    public OrderResultsDTO(int ORDER_ID, Timestamp orderDateAndTime, String productName, double quantity, String unitMeasurement) {
        this.ORDER_ID = ORDER_ID;
        this.orderDateAndTime = orderDateAndTime;
        this.productName = productName;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
    }

    public int getORDER_ID() {
        return ORDER_ID;
    }

    public void setORDER_ID(int ORDER_ID) {
        this.ORDER_ID = ORDER_ID;
    }

    public Timestamp getOrderDateAndTime() {
        return orderDateAndTime;
    }

    public void setOrderDateAndTime(Timestamp orderDateAndTime) {
        this.orderDateAndTime = orderDateAndTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }
}
