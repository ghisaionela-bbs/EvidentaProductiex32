package ro.brutariabaiasprie.evidentaproductie.Domain;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private String unitMeasurement;
    private Group group;

    public Product() {
        this.id = 0;
        this.name = "";
        this.unitMeasurement = "";
        this.group = null;
    }


    public Product(int id, String name, String unitMeasurement, Group group) {
        this.id = id;
        this.name = name;
        this.unitMeasurement = unitMeasurement;
        this.group = group;
    }

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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Objects.equals(name, product.name) && Objects.equals(unitMeasurement, product.unitMeasurement) && Objects.equals(group, product.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unitMeasurement, group);
    }
}
