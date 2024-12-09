package ro.brutariabaiasprie.evidentaproductie.DTO;


import ro.brutariabaiasprie.evidentaproductie.Domain.Product;

public class ProductionProductDTO {
    private Product product;
    private int orderCount;

    public ProductionProductDTO(Product product, int orderCount) {
        this.product = product;
        this.orderCount = orderCount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
