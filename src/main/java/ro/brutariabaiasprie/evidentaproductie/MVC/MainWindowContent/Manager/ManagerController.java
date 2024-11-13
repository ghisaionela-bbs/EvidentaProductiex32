package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order.OrderController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Timestamp;

public class ManagerController implements SceneController {
    private final Stage PARENT_STAGE;
    private final ManagerModel model = new ManagerModel();
    private ManagerView view;

    public ManagerController(Stage parentStage) {
        this.PARENT_STAGE = parentStage;
        this.view = new ManagerView(this.model, parentStage, this::refreshOrders);
        Platform.runLater(() -> {
            view.setEditOrderHandler(this::editOrder);
            model.loadProducts();
            model.loadOrders();
            model.loadGroups();
        });
        DBConnectionService.getModifiedTables().addListener((MapChangeListener<String, Timestamp>) change -> {
            if (change.wasAdded()) {
                String key = change.getKey();
                switch (key) {
                    case "PRODUSE":
                        model.loadProducts();
                        break;
                    case "COMENZI":
                        model.loadOrders();
                        break;
                    case "GRUPE":
                        model.loadGroups();
                        break;
                }
            }
        });
    }

    private void editOrder(Order order) {
        OrderController orderController = new OrderController(PARENT_STAGE, WINDOW_TYPE.EDIT, order);
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

    @Override
    public Region getView() {
        return view.build();
    }

}
