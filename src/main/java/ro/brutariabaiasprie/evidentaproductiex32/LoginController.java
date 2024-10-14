package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    TextField txtFldUsername;

    @FXML
    TextField txtFldPasswod;

    public void handleBtnLoginOnAction() {
        String username = txtFldUsername.getText();
        String password = txtFldPasswod.getText();
    }

}
