package ro.brutariabaiasprie.evidentaproductie.DTO;

import java.sql.Timestamp;

public class OrderDTO {
    private int ID;
    private Timestamp dateAndTimeInserted;
    private int USER_INSERTER_ID;

    public OrderDTO(int ID, Timestamp dateAndTimeInserted, int USER_INSERTER_ID) {
        this.ID = ID;
        this.dateAndTimeInserted = dateAndTimeInserted;
        this.USER_INSERTER_ID = USER_INSERTER_ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Timestamp getDateAndTimeInserted() {
        return dateAndTimeInserted;
    }

    public void setDateAndTimeInserted(Timestamp dateAndTimeInserted) {
        this.dateAndTimeInserted = dateAndTimeInserted;
    }

    public int getUSER_INSERTER_ID() {
        return USER_INSERTER_ID;
    }

    public void setUSER_INSERTER_ID(int USER_INSERTER_ID) {
        this.USER_INSERTER_ID = USER_INSERTER_ID;
    }
}
