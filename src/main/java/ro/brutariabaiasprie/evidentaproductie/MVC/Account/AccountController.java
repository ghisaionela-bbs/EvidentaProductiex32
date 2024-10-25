package ro.brutariabaiasprie.evidentaproductie.MVC.Account;

import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.concurrent.Callable;

public class AccountController implements SceneController {
    private final Stage PARENT_STAGE;
    public Stage getPARENT_STAGE() {
        return PARENT_STAGE;
    }
    private final AccountModel model = new AccountModel();
    private final AccountView view;
    private final Callable<Runnable> disconnectCaller;

    public AccountController(Stage parentStage, Callable<Runnable> disconnectCaller) {
        this.PARENT_STAGE = parentStage;
        this.disconnectCaller = disconnectCaller;
        this.view = new AccountView(model, parentStage, this::disconnect);
    }

    private Runnable disconnect() throws Exception {
        disconnectCaller.call();
        return null;
    }

    @Override
    public Region getView() {
        return view.build();
    }
}
