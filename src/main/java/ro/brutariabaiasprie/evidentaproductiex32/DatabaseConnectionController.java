package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.SimpleFormatter;

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
        dbConnUrl.setText((String) ConfigApp.configuration.get("DBURL"));
        dbConnUser.setText((String) ConfigApp.configuration.get("DBUSER"));
        dbConnPass.setText((String) ConfigApp.configuration.get("DBPASS"));
    }

    @FXML
    public void handleBtnConnectionOnAction(ActionEvent event) {
        connectToDataBase();
    }

    public void connectToDataBase() {
        try {
            connection = DriverManager.getConnection(dbConnUrl.getText(), dbConnUser.getText(), dbConnPass.getText());
            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("login-view.fxml"));
            root = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.connection = connection;
            loginController.setController(stage);
            stage.getScene().setRoot(root);
            stage.show();

        } catch (SQLException e) {
            errorLabel.setVisible(true);
            tglBtnEditConnection.setSelected(true);
            dbConnUrl.setDisable(false);
            dbConnUser.setDisable(false);
            dbConnPass.setDisable(false);

            try {
                String path = (String) ConfigApp.configuration.get("ERRLOG_PATH");
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
