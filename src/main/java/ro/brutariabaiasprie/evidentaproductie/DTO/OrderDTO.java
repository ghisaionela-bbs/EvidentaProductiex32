package ro.brutariabaiasprie.evidentaproductie.DTO;

public class OrderDTO {
    private int ID;
    private int PRODUCT_ID;
    private String productName;
    private double quantity;
    private String unitMeasurement;
    private double completed;
    private double remainder;

    public OrderDTO(int ID, int PRODUCT_ID, String productName, double quantity, String unitMeasurement, double completed, double remainder) {
        this.ID = ID;
        this.PRODUCT_ID = PRODUCT_ID;
        this.productName = productName;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.completed = completed;
        this.remainder = remainder;
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

    @Override
    public String toString() {
        return "OrderDTO{" +
                "ID=" + ID +
                ", PRODUCT_ID=" + PRODUCT_ID +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitMeasurement='" + unitMeasurement + '\'' +
                ", completed=" + completed +
                ", remainder=" + remainder +
                '}';
    }
}
