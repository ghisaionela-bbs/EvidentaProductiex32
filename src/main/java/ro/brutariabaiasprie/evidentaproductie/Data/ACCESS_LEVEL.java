package ro.brutariabaiasprie.evidentaproductie.Data;

public enum ACCESS_LEVEL {
    UNAUTHORIZED(0),
    ADMINISTRATOR(1),
    DIRECTOR(2),
    MANAGER(3),
    OPERATOR(4);

    private int value;
    private ACCESS_LEVEL(int value) {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
