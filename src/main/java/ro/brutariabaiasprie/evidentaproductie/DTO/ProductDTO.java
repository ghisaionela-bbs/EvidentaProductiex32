package ro.brutariabaiasprie.evidentaproductie.DTO;

public class ProductDTO {
    int id;
    String name;
    String unitMeasurement;

    public ProductDTO(int id, String name, String unitMeasurement) {
        this.id = id;
        this.name = name;
        this.unitMeasurement = unitMeasurement;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnitMeasurement() { return unitMeasurement; }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}
