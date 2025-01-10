package ro.brutariabaiasprie.evidentaproductie.Domain;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private double batchValue;
    private String unitMeasurement;
    private Group group;
    private int subgroupId;

    public Product() {
        this.id = 0;
        this.name = "";
        this.batchValue = 0.00;
        this.unitMeasurement = "";
        this.group = null;
        this.subgroupId = 0;
    }

    public Product(int id, String name, double batchValue, String unitMeasurement, Group group, int subgroupId) {
        this.id = id;
        this.name = name;
        this.batchValue = batchValue;
        this.unitMeasurement = unitMeasurement;
        this.group = group;
        this.subgroupId = subgroupId;
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

    public double getBatchValue() {
        return batchValue;
    }

    public void setBatchValue(double batchValue) {
        this.batchValue = batchValue;
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

    public int getSubgroupId() {
        return subgroupId;
    }

    public void setSubgroupId(int subgroupId) {
        this.subgroupId = subgroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && subgroupId == product.subgroupId && Objects.equals(name, product.name) && Objects.equals(unitMeasurement, product.unitMeasurement) && Objects.equals(group, product.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unitMeasurement, group, subgroupId);
    }
}
