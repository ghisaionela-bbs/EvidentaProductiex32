package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Account;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;
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
        Label usernameLabel = new Label("Utilizator: " + ConfigApp.getUser().getUsername());
//        Label usernameValueLabel = new Label(ConfigApp.getUser().getUsername());
        Label roleLabel = new Label("Rol: " + ConfigApp.getRole().getName());
//        Label roleValueLabel = new Label(ConfigApp.getRole().getName());
        Button disconnectButton = createDisconnectButton();

//        VBox infoContainer = new VBox(usernameLabel, usernameValueLabel, roleLabel, roleValueLabel);
        VBox infoContainer = new VBox(usernameLabel, roleLabel);

        root = new VBox(infoContainer, disconnectButton);
        root.setPadding(new Insets(16));
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
