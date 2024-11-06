package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Dashboard;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddProductGroup.AddProductGroupController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;


public class DashboardController implements SceneController {
    private final DashboardModel model = new DashboardModel();
    private final DashboardView view;
    private final Stage stage;

    public DashboardController(Stage owner) {
        this.stage = owner;
        this.view = new DashboardView(model);
        this.view.addProductGroupHandler = this::addProductGroupHandler;
        this.loadProductGroups();
    }

    private void addProductGroupHandler() {
        new AddProductGroupController(stage);
    }

    private void loadProductGroups() {
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                model.loadProductGroups();
                return null;
            }
        };
        Thread loadThread = new Thread(loadTask);
        loadThread.start();
    }

    public Region getView() {
        return view.build();
    }
}
