package ro.brutariabaiasprie.evidentaproductie.Exceptions;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound(String message) {
        super(message);
    }
}
