package ro.brutariabaiasprie.evidentaproductie.MVC.ConnectionWindows.UserConn;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.function.BiConsumer;

public class UserConnController implements SceneController {
    private final UserConnView view;
    private final UserConnModel model = new UserConnModel();
    private final BiConsumer<Runnable, SceneType> sceneSwitchActionHandler;

    public UserConnController(Stage stage, BiConsumer<Runnable, SceneType> sceneSwitchActionHandler) {
        this.view = new UserConnView(stage, this::findUser);
        this.sceneSwitchActionHandler = sceneSwitchActionHandler;

        User configUser = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
        Platform.runLater(() -> {
            view.setOnActionBtnConn(this::findUser);
            if(configUser != null) {
                model.setUsername(configUser.getUsername());
                model.setPassword(configUser.getPassword());
                view.setUserCredentials(model.getUsername(), model.getPassword());
                view.fireConnectButton();
            }
        });
    }

    private void findUser(Runnable runnable, String username, String password) {
        Task<Void> taskDBLookUp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    model.findUser(username, password);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
        taskDBLookUp.setOnSucceeded(evt -> {
            sceneSwitchActionHandler.accept(() -> {}, SceneType.MAIN_WINDOW);
            runnable.run();
        });
        taskDBLookUp.setOnFailed(evt -> {
            view.showError();
            view.connectionStatus.set("");
            runnable.run();
        });
        taskDBLookUp.setOnRunning(evt -> {
            view.connectionStatus.set("Se cauta utilizatorul.");
        });

        Thread dbTaskThread = new Thread(taskDBLookUp);
        dbTaskThread.start();
    }

    @Override
    public Region getView() {
        return view.build();
    }
}
