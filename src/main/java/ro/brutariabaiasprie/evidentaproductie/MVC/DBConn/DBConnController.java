package ro.brutariabaiasprie.evidentaproductie.MVC.DBConn;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneFactory;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.function.BiConsumer;

public class DBConnController implements SceneController {
    private final DBConnView view;
    private final DBConnModel model = new DBConnModel();
    private final BiConsumer<Runnable, SceneType> sceneSwitchActionHandler;

    public DBConnController(Stage stage, BiConsumer<Runnable, SceneType> sceneSwitchActionHandler) {
        this.sceneSwitchActionHandler = sceneSwitchActionHandler;
        model.setUrl((String) ConfigApp.getConfig(CONFIG_KEY.DBURL.name()));
        model.setUsername((String) ConfigApp.getConfig(CONFIG_KEY.DBUSER.name()));
        model.setPassword((String) ConfigApp.getConfig(CONFIG_KEY.DBPASS.name()));
        view = new DBConnView(stage, this::connectToDatabase);
        view.setConnectionCredentials(model.getUrl(), model.getUsername(), model.getPassword());
        view.setOnActionBtnConn(this::connectToDatabase);

        if(model.getUrl() != null && model.getUsername() != null && model.getPassword() != null) {
            Platform.runLater(view::fireConnectButton);
        }
    }

    private void connectToDatabase(Runnable postRunGUIAction) {
        Task<Void> taskDBConnect = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                model.setConnectionCredentials(view.getDBConnectionUrl(), view.getDBConnectionUsername(), view.getDBConnectionPassword());
                model.connectToDatabase();
                return null;
            }
        };

        taskDBConnect.setOnSucceeded(evt -> {
//            System.out.println("success");
            view.connectionStatus.set("Success.");
            sceneSwitchActionHandler.accept(() -> {}, SceneType.USERCONN);
//            model.integrateComplicatedResults();
            postRunGUIAction.run();
        });
        taskDBConnect.setOnFailed(evt -> {
            view.showError();
            view.connectionStatus.set("");
            postRunGUIAction.run();
//            System.out.println("failed");
        });
        taskDBConnect.setOnCancelled(evt -> {
            System.out.println("cancelled");
        });
        taskDBConnect.setOnRunning(evt -> {
//            System.out.println("running");
            view.connectionStatus.set("Se incearca conectarea la baza de date");
        });
        taskDBConnect.setOnScheduled(evt -> {
//            System.out.println("scheduled");
            view.connectionStatus.set("Start");
        });
        Thread dbTaskThread = new Thread(taskDBConnect);
        dbTaskThread.start();
    }

    public Region getView() {
        return view.build();
    }
}
