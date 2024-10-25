package ro.brutariabaiasprie.evidentaproductie.MVC.Account;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.concurrent.Callable;
import java.util.function.Consumer;


public class AccountView extends Parent implements Builder<Region> {
    private final AccountModel model;
    private Stage PARENT_STAGE;
    private VBox root;
    private Callable<Runnable> disconnectHandler;

    public AccountView(AccountModel model, Stage parentStage, Callable<Runnable> disconnectHandler) {
        this.model = model;
        this.PARENT_STAGE = parentStage;
        this.disconnectHandler = disconnectHandler;
    }

    @Override
    public Region build() {
        User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
        Label nameLabel = new Label(user.getUsername());
        Button disconnectButton = createDisconnectButton();
        root = new VBox(nameLabel, disconnectButton);
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
