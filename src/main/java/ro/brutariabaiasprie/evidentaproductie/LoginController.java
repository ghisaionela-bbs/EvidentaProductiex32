package ro.brutariabaiasprie.evidentaproductie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    Connection connection;
    Stage stage;
    private Parent root;

    @FXML
    TextField txtFldUsername;

    @FXML
    TextField txtFldPasswod;

    @FXML
    Label lblErrorMessage;

    public void setController(Stage stage) {
        this.stage = stage;
        lblErrorMessage.setVisible(false);
    }

    @FXML
    public void handleBtnLoginOnAction() {
        String username = txtFldUsername.getText().trim();
        String password = txtFldPasswod.getText().trim();

        try {
            String sqlUser = "SELECT * FROM UTILIZATORI WHERE nume_utilizator=? AND parola=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUser);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst() ) {
                lblErrorMessage.setVisible(true);
                return;
            }

            resultSet.next();
            int ID = resultSet.getInt("ID");
            int ID_ROLE = resultSet.getInt("ID_ROL");
            User user = new User();
            user.setID(ID);
            user.setID_ROLE(ID_ROLE);
            user.setUsername(username);
            user.setPassword(password);

//            User user = new User();
//            user.setID(resultSet.getInt("ID"));
//            user.setUsername(resultSet.getString("nume_utilizator"));
//            user.setPassword(resultSet.getString("parola"));
//            user.setID_ROLE(resultSet.getInt("ID_ROL"));

            ConfigApp.setConfig(CONFIG_KEY.APPUSER.name(), user);
            ConfigApp.write_config();

            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("product-list-view.fxml"));
            root = fxmlLoader.load();
            ProductListController productListController = fxmlLoader.getController();
            productListController.connection = connection;
            productListController.setController(stage);
            productListController.loadListView();
            stage.getScene().setRoot(root);
            stage.show();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
