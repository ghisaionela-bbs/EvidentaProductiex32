package ro.brutariabaiasprie.evidentaproductie.DTO;

public class OrderItemDTO {
    private int ID_PRODUS;
    private String productName;
    private String unitMeasurement;
    private double quantity;

    public OrderItemDTO(int ID_PRODUS, String productName, String unitMeasurement, double quantity) {
        this.ID_PRODUS = ID_PRODUS;
        this.productName = productName;
        this.unitMeasurement = unitMeasurement;
        this.quantity = quantity;
    }

    public int getID_PRODUS() {
        return ID_PRODUS;
    }

    public void setID_PRODUS(int ID_PRODUS) {
        this.ID_PRODUS = ID_PRODUS;
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
}
