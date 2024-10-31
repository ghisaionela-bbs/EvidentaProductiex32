package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.QuantityPrompt;

import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;

public class QuantityPromptModel {
    private final ProductDTO product;
    private double quantity;

    public QuantityPromptModel(ProductDTO product) {
        this.product = product;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

}
