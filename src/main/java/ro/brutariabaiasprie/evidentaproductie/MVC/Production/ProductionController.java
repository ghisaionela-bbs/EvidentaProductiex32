package ro.brutariabaiasprie.evidentaproductie.MVC.Production;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnView;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.Dictionary;
import java.util.function.BiConsumer;

public class ProductionController implements SceneController {
    private final ProductionView view;
    private final ProductionModel model = new ProductionModel();
    private final BiConsumer<Runnable, SceneType> sceneSwitActionHandler;

    public ProductionController(Stage window, BiConsumer<Runnable, SceneType> sceneSwitActionHandler) {
        model.loadProductRecords();
        this.view = new ProductionView(model, window, this::loadProducts, this::addProductRecordToDB);
        this.sceneSwitActionHandler = sceneSwitActionHandler;
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
        return view.build();
    }
}
