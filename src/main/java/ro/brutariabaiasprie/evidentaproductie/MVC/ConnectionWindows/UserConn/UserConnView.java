package ro.brutariabaiasprie.evidentaproductie.MVC.ConnectionWindows.UserConn;

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
import org.apache.logging.log4j.util.TriConsumer;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;

import java.io.IOException;

public class UserConnView extends Parent implements Builder<Region> {
    private final Stage stage;
    private final TriConsumer<Runnable, String, String> actionHandler;

    private TextField txtFldUsername;
    private TextField txtFldPassword;
    private Button btnConn = new Button();
    private GridPane inputBox = new GridPane();
    public final StringProperty connectionStatus = new SimpleStringProperty();
    private final VBox root = new VBox(8);

    public UserConnView(Stage stage, TriConsumer<Runnable, String, String> actionHandler) {
        this.stage = stage;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        root.getChildren().addAll(createInputBox(), createConnStatusLabel(), createConnButton());
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("vbox-conn");

        return root;
    }

    private Node createInputBox() {
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
        gridPane.add(lblUsername, 0, 0);
        gridPane.add(txtFldUsername, 1, 0);
        gridPane.add(userNameKeyboardButton, 2, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(txtFldPassword, 1, 1);
        gridPane.add(passwordKeyboardButton, 2, 1);
        gridPane.getStyleClass().add("grid-conn");
        inputBox = gridPane;
        return inputBox;
    }

    private Node createConnStatusLabel() {
        Label connStatus = new Label();
        connStatus.textProperty().bind(connectionStatus);
        return connStatus;
    }

    private Node createConnButton() {
        btnConn = new Button("Conectare");
        return btnConn;
    }

    public void setUserCredentials(String username, String password) {
        txtFldUsername.textProperty().set(username);
        txtFldPassword.textProperty().set(password);
    }

    public void setOnActionBtnConn(TriConsumer<Runnable, String, String> actionHandler) {
        btnConn.setOnAction(event -> actionHandler.accept(
                () -> {},
                txtFldUsername.getText().trim(),
                txtFldPassword.getText().trim()
        ));
    }

    public void showError() {
        WarningController warningController = new WarningController(stage, "Numele de utilizator sau parola sunt incorecte!");
    }

    public void fireConnectButton() {
        btnConn.fire();
    }

}
