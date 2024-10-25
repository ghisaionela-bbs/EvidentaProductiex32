package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class ProductRecordDTO {
    private int ID;
    private String name;
    private String unitMeasurement;
    private double quantity;
    private Timestamp dateAndTimeInserted;

    public ProductRecordDTO(int ID, String name, String unitMeasurement, double quantity, Timestamp dateAndTimeInserted) {
        this.ID = ID;
        this.name = name;
        this.unitMeasurement = unitMeasurement;
        this.quantity = quantity;
        this.dateAndTimeInserted = dateAndTimeInserted;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public Timestamp getDateAndTimeInserted() {
        return dateAndTimeInserted;
    }

    public void setDateAndTimeInserted(Timestamp dateAndTimeInserted) {
        this.dateAndTimeInserted = dateAndTimeInserted;
    }

    @Override
    public String toString() {
        return "ProductRecordDTO{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", unitMeasurement='" + unitMeasurement + '\'' +
                ", quantity=" + quantity +
                ", dateAndTimeInserted=" + dateAndTimeInserted +
                '}';
    }
}
