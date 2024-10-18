package ro.brutariabaiasprie.evidentaproductie.MVC.DBConn;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.util.function.Consumer;

public class DBConnViewBuilder extends Parent implements Builder<Region> {
    private final DBConnModel.PresentationModel viewModel;
    private final StringProperty connectionStatus = new SimpleStringProperty("Conectarea la baza de date.");
    private final Consumer<Runnable> actionHandler;

    public DBConnViewBuilder(DBConnModel.PresentationModel viewModel, Consumer<Runnable> actionHandler) {
        this.viewModel = viewModel;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox inputBox = new VBox(8);
        inputBox.getChildren().addAll(createInputBox(), createConnStatusLabel(),createConnButton());
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(20));
        return inputBox;
    }

    private Node createInputBox() {
        Label lblUrl = new Label("Url:");
        TextField txtFldUrl = new TextField();
        txtFldUrl.textProperty().bindBidirectional(viewModel.urlProperty());

        Label lblUsername = new Label("Utilizator:");
        TextField txtFldUsername = new TextField();
        txtFldUsername.textProperty().bindBidirectional(viewModel.usernameProperty());

        Label lblPassword = new Label("Parola");
        TextField txtFldPassword = new TextField();
        txtFldPassword.textProperty().bindBidirectional(viewModel.passwordProperty());

        GridPane gridPane = new GridPane();
        gridPane.add(lblUrl, 0, 0);
        gridPane.add(txtFldUrl, 1, 0);
        gridPane.add(lblUsername, 0, 1);
        gridPane.add(txtFldUsername, 1, 1);
        gridPane.add(lblPassword, 0, 2);
        gridPane.add(txtFldPassword, 1, 2);

        return gridPane;
    }

    private Node createConnStatusLabel() {
        Label connStatus = new Label();
        connStatus.textProperty().bind(connectionStatus);
        return connStatus;
    }

    private Node createConnButton() {
        Button button = new Button("Conectare");
        button.setOnAction(event -> {
            button.setDisable(true);
            actionHandler.accept(() -> {
                button.setDisable(false);
            });
        });
        return button;
    }

}
