package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Account;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;

import java.util.concurrent.Callable;


public class AccountView extends Parent implements Builder<Region> {
    private final AccountModel model;
    private Stage PARENT_STAGE;
    private VBox root;
    private final Callable<Runnable> disconnectHandler;

    public AccountView(AccountModel model, Stage parentStage, Callable<Runnable> disconnectHandler) {
        this.model = model;
        this.PARENT_STAGE = parentStage;
        this.disconnectHandler = disconnectHandler;
    }

    @Override
    public Region build() {
        VBox userInfoContainer = new VBox();

        Label userInfoTitle = new Label("Informatii cont");
        HBox.setHgrow(userInfoTitle, Priority.ALWAYS);
        userInfoTitle.setMaxWidth(Double.MAX_VALUE);
        userInfoTitle.getStyleClass().add("sub-main-window-title");

        Label usernameLabel = new Label("Utilizator: " + ConfigApp.getUser().getUsername());
        Label roleLabel = new Label("Rol: " + ConfigApp.getRole().getName());
        Button disconnectButton = createDisconnectButton();
        disconnectButton.setGraphic(new FontIcon("mdi2p-power"));
        disconnectButton.getStyleClass().add("sub-main-window-button");
        disconnectButton.getStyleClass().add("disconnect");
        HBox userInfoHeader = new HBox(userInfoTitle, disconnectButton);
        userInfoHeader.getStyleClass().add("sub-main-window-header");

        VBox infoContainer = new VBox(usernameLabel, roleLabel);
        infoContainer.getStyleClass().add("sub-main-window-header");
        userInfoContainer.getChildren().addAll(userInfoHeader, infoContainer);
        userInfoContainer.setSpacing(8);
        userInfoContainer.getStyleClass().add("sub-main-window-content-container");

        root = new VBox(userInfoContainer);
        return root;
    }

    private Button createDisconnectButton() {
        Button disconnectButton = new Button("Deconectare");
        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    disconnectHandler.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return disconnectButton;
    }
}
