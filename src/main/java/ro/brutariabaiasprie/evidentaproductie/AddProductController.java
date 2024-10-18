package ro.brutariabaiasprie.evidentaproductie;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Controllers.IController;

public class AddProductController implements IController {
    @FXML
    TextField txtFldProductName;

    @FXML
    Button btnAddProduct;

    @FXML
    Label lblError;

    public void setController() {
        lblError.setVisible(false);
    }

    @FXML
    public void handleBtnAddProductOnAction() {
        if (check_input()) {
            Stage stage = (Stage) btnAddProduct.getScene().getWindow();
            stage.close();
        }
    }

    public boolean check_input() {
        String productName = txtFldProductName.getText();
        if (productName.isEmpty()) {
            lblError.setText("Campul 'Denumire' nu poate fi gol!");
            lblError.setVisible(true);
            return false;
        }
        return true;
    }
}
