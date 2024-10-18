package ro.brutariabaiasprie.evidentaproductie.DTO;

public class ProductDTO {
    int id;
    String name;

    public ProductDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}
