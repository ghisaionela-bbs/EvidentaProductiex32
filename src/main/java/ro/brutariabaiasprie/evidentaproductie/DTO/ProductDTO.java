package ro.brutariabaiasprie.evidentaproductie.DTO;

public class ProductDTO {
    int ID;
    String name;
    String unitMeasurement;

    public ProductDTO(int ID, String name, String unitMeasurement) {
        this.ID = ID;
        this.name = name;
        this.unitMeasurement = unitMeasurement;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getUnitMeasurement() { return unitMeasurement; }

    @Override
    public String toString() {
        return ID + ": " + name;
    }
}
