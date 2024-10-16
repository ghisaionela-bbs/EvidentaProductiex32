package ro.brutariabaiasprie.evidentaproductiex32.DTO;

import java.sql.Timestamp;

public class ProductRecordDTO {
    private int id;
    private String name;
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

    public ProductRecordDTO(int id, String name, double quantity, Timestamp dateAndTime) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.dateAndTime = dateAndTime;
    }
}
