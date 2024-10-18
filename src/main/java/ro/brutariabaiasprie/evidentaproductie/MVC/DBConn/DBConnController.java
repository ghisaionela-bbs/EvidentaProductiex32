package ro.brutariabaiasprie.evidentaproductie.MVC.DBConn;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;

public class DBConnController {
    private final DBConnViewBuilder view;
    private final DBConnModel model = new DBConnModel();

    public DBConnController() {
        model.getPresentationModel().setUrl((String) ConfigApp.getConfig(CONFIG_KEY.DBURL.name()));
        model.getPresentationModel().setUsername((String) ConfigApp.getConfig(CONFIG_KEY.DBUSER.name()));
        model.getPresentationModel().setPassword((String) ConfigApp.getConfig(CONFIG_KEY.DBPASS.name()));
        view = new DBConnViewBuilder(model.getPresentationModel(), this::connectToDatabase);
    }

    private void connectToDatabase(Runnable postRunGUIAction) {
        Task<Void> taskDBConnect = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                model.connectToDatabase(this::updateProgress);
                return null;
            }
        };

        taskDBConnect.setOnSucceeded(evt -> {
            System.out.println("success");

//            model.getPresentationModel().passwordProperty().unbind();
//            model.integrateComplicatedResults();
            postRunGUIAction.run();
        });
        Thread bigTaskThread = new Thread(taskDBConnect);
        bigTaskThread.start();
    }

    public Region getView() {
        return view.build();
    }
}
