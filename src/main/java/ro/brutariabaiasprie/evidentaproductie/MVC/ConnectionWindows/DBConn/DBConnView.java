package ro.brutariabaiasprie.evidentaproductie.MVC.ConnectionWindows.DBConn;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * The view for the database connection scene
 */
public class DBConnView extends Parent implements Builder<Region> {
//    private final DBConnModel model;
    public final StringProperty connectionStatus = new SimpleStringProperty();
    private final Consumer<Runnable> actionHandler;

    private final VBox root = new VBox(8);

    private TextField txtFldUrl = new TextField();
    private TextField txtFldUsername = new TextField();
    private TextField txtFldPassword = new TextField();
    private Button btnConn = new Button();
    private final Label lblConnectionStatus = new Label();
    private GridPane inputBox = new GridPane();
    private Stage stage;

    /**
     * Constructs a DBConnView instance
     * @param actionHandler - consumer for the connection action
     */
    public DBConnView(Stage stage, Consumer<Runnable> actionHandler) {
        this.actionHandler = actionHandler;
        this.stage = stage;
    }

    /**
     * Builds the view
     * @return the view
     */
    @Override
    public Region build() {
        root.getChildren().addAll(createInputBox(), createConnStatusLabel(),createConnButton());
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("vbox-conn");
        return root;
    }

    private Node createInputBox() {
        Label lblUrl = new Label("Url:");
        txtFldUrl = new TextField();
        Button urlKeyboardButton = new Button();
        urlKeyboardButton.setGraphic(new FontIcon("mdi2k-keyboard"));
        urlKeyboardButton.getStyleClass().add("filled-button");
        urlKeyboardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Runtime.getRuntime().exec("C:/windows/sysnative/cmd /c C:/Windows/system32/osk.exe");
                    txtFldUrl.requestFocus();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Label lblUsername = new Label("Utilizator:");
        txtFldUsername = new TextField();
        Button userNameKeyboardButton = new Button();
        userNameKeyboardButton.setGraphic(new FontIcon("mdi2k-keyboard"));
        userNameKeyboardButton.getStyleClass().add("filled-button");
        userNameKeyboardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Runtime.getRuntime().exec("C:/windows/sysnative/cmd /c C:/Windows/system32/osk.exe");
                    txtFldUsername.requestFocus();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Label lblPassword = new Label("Parola:");
        txtFldPassword = new TextField();
        Button passwordKeyboardButton = new Button();
        passwordKeyboardButton.setGraphic(new FontIcon("mdi2k-keyboard"));
        passwordKeyboardButton.getStyleClass().add("filled-button");
        passwordKeyboardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Runtime.getRuntime().exec("C:/windows/sysnative/cmd /c C:/Windows/system32/osk.exe");
                    txtFldPassword.requestFocus();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.add(lblUrl, 0, 0);
        gridPane.add(txtFldUrl, 1, 0);
        gridPane.add(urlKeyboardButton, 2, 0);
        gridPane.add(lblUsername, 0, 1);
        gridPane.add(txtFldUsername, 1, 1);
        gridPane.add(userNameKeyboardButton, 2, 1);
        gridPane.add(lblPassword, 0, 2);
        gridPane.add(txtFldPassword, 1, 2);
        gridPane.add(passwordKeyboardButton, 2, 2);
        gridPane.getStyleClass().add("grid-conn");
        inputBox = gridPane;
        return inputBox;
    }

    private Node createConnStatusLabel() {
        Label connStatus = new Label();
        connStatus.textProperty().bind(connectionStatus);
        return connStatus;
    }

//    private Node createConnButton() {
//        Button button = new Button("Conectare");
//        button.setOnAction(event -> {
//            button.setDisable(true);
//            actionHandler.accept(() -> {
//                button.setDisable(false);
//            });
//        });
//        return button;
//    }

    private Node createConnButton() {
        btnConn = new Button("Conectare");
//        button.setOnAction(event -> {
//            button.setDisable(true);
//            actionHandler.accept(() -> {
//                button.setDisable(false);
//            });
//        });
        return btnConn;
    }

    public void setOnActionBtnConn(Consumer<Runnable> actionHandler) {
        Platform.runLater(() -> {
            btnConn.setOnAction(event -> {
//                inputBox.setDisable(true);
//                btnConn.setDisable(true);
                actionHandler.accept(() -> {
//                    inputBox.setDisable(false);
//                    btnConn.setDisable(false);
                });
            });
        });
    }

    public void setConnectionCredentials(String url, String username, String password) {
        Platform.runLater(() -> {
            txtFldUrl.textProperty().set(url);
            txtFldUsername.textProperty().set(username);
            txtFldPassword.textProperty().set(password);
        });
    }

    public void showError() {
        WarningController warningController = new WarningController(stage, "Conectarea la baza de date a esuat!\nVa rugam sa verificati credentialele de conectare si sa reincercati!");
    }

    public String getDBConnectionUrl() {
        return txtFldUrl.getText();
    }

    public String getDBConnectionUsername() {
        return txtFldUsername.getText();
    }

    public String getDBConnectionPassword() {
        return txtFldPassword.getText();
    }

    public void fireConnectButton() {
        btnConn.fire();
    }
}
