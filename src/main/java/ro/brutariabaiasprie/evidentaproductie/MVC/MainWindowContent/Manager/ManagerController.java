package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;

public class ManagerController implements SceneController {
    private final Stage PARENT_STAGE;
    private final ManagerModel model = new ManagerModel();
    private ManagerView view;

    public ManagerController(Stage parentStage) {
        this.PARENT_STAGE = parentStage;
        this.view = new ManagerView(this.model, parentStage, this::refreshProducts, this::refreshOrders);
        Platform.runLater(() -> {
            model.loadProducts();
            model.loadOrders();
        });
    }

    private void refreshOrders() {
        Task<Void> taskDBload = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        model.loadOrders();
                    }
                });
                return null;
            }
        };

        Thread dbTaskThread = new Thread(taskDBload);
        dbTaskThread.setDaemon(true);
        dbTaskThread.start();
    }

    private void refreshProducts() {
        Task<Void> taskDBload = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        model.loadProducts();
                    }
                });
                return null;
            }
        };

        Thread dbTaskThread = new Thread(taskDBload);
        dbTaskThread.setDaemon(true);
        dbTaskThread.start();

    }

    @Override
    public Region getView() {
        return view.build();
    }

}
