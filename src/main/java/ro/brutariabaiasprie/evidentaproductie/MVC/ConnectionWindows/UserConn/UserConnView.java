package ro.brutariabaiasprie.evidentaproductie.MVC.ConnectionWindows.UserConn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;

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

        Label lblPassword = new Label("Parola:");
        txtFldPassword = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.add(lblUsername, 0, 0);
        gridPane.add(txtFldUsername, 1, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(txtFldPassword, 1, 1);
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
