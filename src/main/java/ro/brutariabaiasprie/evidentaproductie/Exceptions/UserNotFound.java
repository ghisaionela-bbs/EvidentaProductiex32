package ro.brutariabaiasprie.evidentaproductie.Exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String message) {
        super(message);
    }
}
