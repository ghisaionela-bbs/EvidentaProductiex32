package ro.brutariabaiasprie.evidentaproductie.Exceptions;

public class OrderItemNotFound extends RuntimeException {
    public OrderItemNotFound(String message) {
        super(message);
    }
}
