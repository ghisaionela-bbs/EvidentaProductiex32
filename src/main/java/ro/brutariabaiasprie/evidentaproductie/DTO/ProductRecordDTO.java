package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class ProductRecordDTO {
    private int id;
    private String name;
    private String unitMeasurement;
    private double quantity;
    private Timestamp dateAndTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Timestamp getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public ProductRecordDTO(int id, String name, String unitMeasurement, double quantity, Timestamp dateAndTime) {
        this.id = id;
        this.name = name;
        this.unitMeasurement = unitMeasurement;
        this.quantity = quantity;
        this.dateAndTime = dateAndTime;
    }

    @Override
    public String toString() {
        return "ProductRecordDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitMeasurement='" + unitMeasurement + '\'' +
                ", quantity=" + quantity +
                ", dateAndTime=" + dateAndTime +
                '}';
    }
}
