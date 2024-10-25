package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow;

import javafx.application.Platform;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.MVC.Account.AccountController;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnController;
import ro.brutariabaiasprie.evidentaproductie.MVC.Manager.ManagerController;
import ro.brutariabaiasprie.evidentaproductie.MVC.Production.ProductionController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;
import ro.brutariabaiasprie.evidentaproductie.MVC.UserConn.UserConnController;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class MainWindowController implements SceneController {
    private final Stage PARENT_STAGE;
    public Stage getPARENT_STAGE() {
        return PARENT_STAGE;
    }
    private final MainWindowModel model = new MainWindowModel();
    private final MainWindowView view;
    private ProductionController productionController;
    private Callable<Runnable> disconnectActionHandler;


    public MainWindowController(Stage parentStage, Callable<Runnable> disconnectActionHandler) {
        this.PARENT_STAGE = parentStage;
        this.view = new MainWindowView(this.model, parentStage, this::setCenter);
        this.disconnectActionHandler = disconnectActionHandler;
        Platform.runLater(() ->{
            productionController = new ProductionController(PARENT_STAGE, this::switchScene);
            this.view.openDefaultTab();
        });
    }

    private void setCenter(SceneType sceneType) {
        SceneController controller = null;
        switch (sceneType) {
            case DBCONN:
                controller = new DBConnController(PARENT_STAGE, this::switchScene);
                break;
            case USERCONN:
                controller = new UserConnController(PARENT_STAGE, this::switchScene);
                break;
            case MANAGER:
                controller = new ManagerController(PARENT_STAGE);
                break;
            case PRODUCTION:
                controller = productionController;
                break;
            case ACCOUNT:
                controller = new AccountController(PARENT_STAGE, this::disconnect);
                break;
            case MAIN_WINDOW:
                controller = new MainWindowController(PARENT_STAGE, this::disconnect);
                break;
            case DEFAULT:
                int user_role = ((User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name())).getID_ROLE();
                if(user_role == 1) {
                    controller = new ProductionController(PARENT_STAGE, this::switchScene);
                } else {
                    controller = new ProductionController(PARENT_STAGE, this::switchScene);
                }
        }
        view.setCenter(Objects.requireNonNull(controller).getView());
    }

    private Runnable disconnect() throws Exception {
        disconnectActionHandler.call();
        return null;
    }

    private void switchScene(Runnable runnable, SceneType sceneType) {
        setCenter(sceneType);
    }

    @Override
    public Region getView() {
        return view.build();
    }


}
