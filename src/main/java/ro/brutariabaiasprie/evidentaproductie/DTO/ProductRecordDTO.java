package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class ProductRecordDTO {
    private int ID;
    private int PRODUCT_ID;
    private String name;
    private String unitMeasurement;
    private double quantity;
    private Timestamp dateAndTimeInserted;
    private int USER_INSERTER_ID;
    private Integer ORDER_ID;

    public ProductRecordDTO(int ID, int PRODUCT_ID, String name, String unitMeasurement,
                            double quantity, Timestamp dateAndTimeInserted,
                            int USER_INSERTER_ID, Integer ORDER_ID) {
        this.ID = ID;
        this.PRODUCT_ID = PRODUCT_ID;
        this.name = name;
        this.unitMeasurement = unitMeasurement;
        this.quantity = quantity;
        this.dateAndTimeInserted = dateAndTimeInserted;
        this.USER_INSERTER_ID = USER_INSERTER_ID;
        this.ORDER_ID = ORDER_ID;
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

    public int getUSER_INSERTER_ID() {
        return USER_INSERTER_ID;
    }

    public void setUSER_INSERTER_ID(int USER_INSERTER_ID) {
        this.USER_INSERTER_ID = USER_INSERTER_ID;
    }

    public Integer getORDER_ID() {
        return ORDER_ID;
    }

    public void setORDER_ID(Integer ORDER_ID) {
        this.ORDER_ID = ORDER_ID;
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
