package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Stack;

public class DatabaseConnectionController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    Connection connection;
    Boolean is_connected = false;
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

    public void setController(Stage stage) {
        btnConnect.requestFocus();
        this.stage = stage;
        errorLabel.setVisible(false);
        dbConnUrl.setText((String) ConfigApp.configuration.get("DBURL"));
        dbConnUser.setText((String) ConfigApp.configuration.get("DBUSER"));
        dbConnPass.setText((String) ConfigApp.configuration.get("DBPASS"));
    }

    @FXML
    public void handleBtnConnectionOnAction(ActionEvent event) {
        try {
            connection = DriverManager.getConnection(dbConnUrl.getText(), dbConnUser.getText(), dbConnPass.getText());
            is_connected = true;

            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("product-list-view.fxml"));
            root = fxmlLoader.load();
            ProductListController productListController = fxmlLoader.getController();
            productListController.connection = connection;
            productListController.setController(stage);
            productListController.loadListView();
//            scene = new Scene(root);
//            stage.setScene(scene);
            stage.getScene().setRoot(root);
            stage.show();

        } catch (SQLException e) {
            errorLabel.setVisible(true);
            FileSystemView view = FileSystemView.getFileSystemView();
            File file = view.getHomeDirectory();
            String desktopPath = file.getPath();
            try {
                File errorFile = new File(desktopPath + "\\EvidentaProductieErrorLog.txt");
                errorFile.createNewFile();
                FileWriter errorWriter = new FileWriter(desktopPath + "\\EvidentaProductieErrorLog.txt");
                errorWriter.write(e.toString());
                errorWriter.close();
            } catch (IOException ioException) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
