package ro.brutariabaiasprie.evidentaproductie;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import java.time.LocalDate;

public class ExcelExportController {
    Boolean export = false;

    @FXML
    DatePicker datePickFrom;

    @FXML
    DatePicker datePickTo;

    @FXML
    Button btnOk;

    @FXML
    Button btnCancel;

    @FXML
    Button btnResetDatePickFrom;

    @FXML
    Button btnResetDatePickTo;

    @FXML
    public void initialize() {
        btnOk.requestFocus();
        datePickFrom.setValue(LocalDate.now());
        datePickTo.setValue(LocalDate.now());
    }

    @FXML
    public void handleBtnOkOnAction() {
        export = true;
        Stage stage  = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleBtnCancelOnAction() {
        export = false;
        Stage stage  = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleBtnResetDatePickFromOnAction() {
        datePickFrom.setValue(null);
    }

    @FXML
    public void handleBtnResetDatePickToOnAction() {
        datePickTo.setValue(null);
    }
}
