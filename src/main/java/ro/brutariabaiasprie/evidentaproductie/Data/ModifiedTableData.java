package ro.brutariabaiasprie.evidentaproductie.Data;

import java.sql.Timestamp;

public class ModifiedTableData {
    private final int rowId;
    private final String operation_type;
    private final Timestamp change_time;

    public ModifiedTableData(int rowId, String operation_type, Timestamp change_time) {
        this.rowId = rowId;
        this.operation_type = operation_type;
        this.change_time = change_time;
    }

    public int getRowId() {
        return rowId;
    }

    public String getOperation_type() {
        return operation_type;
    }

    public Timestamp getChange_time() {
        return change_time;
    }
}
