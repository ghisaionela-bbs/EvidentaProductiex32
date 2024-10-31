package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Production;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;

import java.util.Dictionary;

public class ProductionController implements SceneController {
    private final ProductionView view;
    private final ProductionModel model = new ProductionModel();

    public ProductionController(Stage window) {
        model.loadProductRecords();
        this.view = new ProductionView(model, window, this::loadProducts, this::addProductRecordToDB);
    }

    private void loadProducts(Runnable runnable) {
        Task<Void> taskDBload = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(model::loadProducts);
                return null;
            }
        };
        taskDBload.setOnSucceeded(evt -> {
            runnable.run();
        });
        Thread dbTaskThread = new Thread(taskDBload);
        dbTaskThread.start();
    }

    private void addProductRecordToDB(Runnable runnable, Dictionary<String, Object> data) {
        Task<Void> taskDBload = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ProductDTO productDTO = (ProductDTO) data.get("product");
                        double quantity = (double) data.get("quantity");
                        model.addProductRecordToDB(productDTO, quantity);
                    }
                });
                return null;
            }
        };
        taskDBload.setOnSucceeded(evt -> {
            runnable.run();
        });
        Thread dbTaskThread = new Thread(taskDBload);
        dbTaskThread.start();
    }

    @Override
    public Region getView() {
        if(view.getRoot() != null) {
            return view.getRoot();
        } else {
            return view.build();
        }
    }
}
