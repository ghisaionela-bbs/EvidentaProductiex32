package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductiex32.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductiex32.Data.ConfigApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class DatabaseConnectionController {
    private Stage stage;
    private Parent root;
    Connection connection;
//    String url = "jdbc:sqlserver://192.168.3.145;databaseName=DB_EVIDENTA_PRODUCTIE;encrypt=false;";
//    String username = "sa";
//    String password = "sqlserverstatia51";
    @FXML
    TextField dbConnUrl;

    @FXML
    TextField dbConnUser;

    @FXML
    TextField dbConnPass;

    @FXML
    Label errorLabel;

    @FXML
    Button btnConnect;

    @FXML
    VBox vBoxConnConfig;

    @FXML
    ToggleButton tglBtnEditConnection;

    public void setController(Stage stage) {
        this.stage = stage;
        btnConnect.requestFocus();
        errorLabel.setVisible(false);
        tglBtnEditConnection.setSelected(false);
        dbConnUrl.setDisable(true);
        dbConnUser.setDisable(true);
        dbConnPass.setDisable(true);
        dbConnUrl.setText((String) ConfigApp.configuration.get(CONFIG_KEY.DBURL.name()));
        dbConnUser.setText((String) ConfigApp.configuration.get(CONFIG_KEY.DBUSER.name()));
        dbConnPass.setText((String) ConfigApp.configuration.get(CONFIG_KEY.DBPASS.name()));
    }

    @FXML
    public void handleBtnConnectionOnAction(ActionEvent event) {
        connectToDataBase();
    }

    public void connectToDataBase() {
        try {
            String url = dbConnUrl.getText();
            String username = dbConnUser.getText();
            String password = dbConnPass.getText();

            connection = DriverManager.getConnection(url, username, password);

            boolean updateConn = false;
            if(!url.equals(ConfigApp.configuration.get(CONFIG_KEY.DBURL.name()))){
                ConfigApp.setConfig(CONFIG_KEY.DBURL.name(), url);
                updateConn = true;
            }
            if(!username.equals(ConfigApp.configuration.get(CONFIG_KEY.DBUSER.name()))){
                ConfigApp.setConfig(CONFIG_KEY.DBUSER.name(), username);
                updateConn = true;
            }
            if(!password.equals(ConfigApp.configuration.get(CONFIG_KEY.DBPASS.name()))){
                ConfigApp.setConfig(CONFIG_KEY.DBPASS.name(), password);
                updateConn = true;
            }
            if(updateConn) {
                ConfigApp.write_config();
            }

            //Go to login screen if user was not logged before
            if(ConfigApp.getConfig(CONFIG_KEY.APPUSER.name()) == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("login-view.fxml"));
                root = fxmlLoader.load();
                LoginController loginController = fxmlLoader.getController();
                loginController.connection = connection;
                loginController.setController(stage);
                stage.getScene().setRoot(root);
                stage.show();
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("product-list-view.fxml"));
                root = fxmlLoader.load();
                ProductListController productListController = fxmlLoader.getController();
                productListController.connection = connection;
                productListController.setController(stage);
                productListController.loadListView();
                stage.getScene().setRoot(root);
                stage.show();
            }

        } catch (SQLException e) {
            errorLabel.setVisible(true);
            tglBtnEditConnection.setSelected(true);
            dbConnUrl.setDisable(false);
            dbConnUser.setDisable(false);
            dbConnPass.setDisable(false);

            try {
                String path = (String) ConfigApp.configuration.get(CONFIG_KEY.ERRLOG_PATH.name());
                File errLogFile = new File(path);
                errLogFile.createNewFile();
                FileWriter errorWriter = new FileWriter(path);
                errorWriter.write(e.toString());
                errorWriter.close();
            } catch (IOException ioException) {
                System.out.println("An error occurred at writing the file.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleTglBtnEditConnectionOnAction() {
        boolean isEditable = !tglBtnEditConnection.isSelected();
        dbConnUrl.setDisable(isEditable);
        dbConnUser.setDisable(isEditable);
        dbConnPass.setDisable(isEditable);
    }

//    public void connectToDataBase() {
//        try {
//            connection = DriverManager.getConnection(dbConnUrl.getText(), dbConnUser.getText(), dbConnPass.getText());
//            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("login-view.fxml"));
//            root = fxmlLoader.load();
//            ProductListController productListController = fxmlLoader.getController();
//            productListController.connection = connection;
//            productListController.setController(stage);
//            productListController.loadListView();
////            scene = new Scene(root);
////            stage.setScene(scene);
//            stage.getScene().setRoot(root);
//            stage.show();
//
//        } catch (SQLException e) {
//            errorLabel.setVisible(true);
//            dbConnUrl.setDisable(false);
//            dbConnUser.setDisable(false);
//            dbConnPass.setDisable(false);
//
//            try {
//                String path = (String) ConfigApp.configuration.get("ERRLOG_PATH");
//                File errLogFile = new File(path);
//                errLogFile.createNewFile();
//                FileWriter errorWriter = new FileWriter(path);
//                errorWriter.write(e.toString());
//                errorWriter.close();
//            } catch (IOException ioException) {
//                System.out.println("An error occurred at writing the file.");
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
