package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Production;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.EditProductRecord.EditProductRecordController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Timestamp;

public class ProductionController implements SceneController {
    private final ProductionView view;
    private final ProductionModel model = new ProductionModel();

    public ProductionController(Stage window) {
        model.loadProductRecords();
        this.view = new ProductionView(model, window,
                this::loadProducts,
                this::addProductRecordToDB,
                this::searchOrderForProduct,
                this::setSelectedProduct,
                this::editProductRecordHandler);
        DBConnectionService.getModifiedTables().addListener(new MapChangeListener<String, Timestamp>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Timestamp> change) {
                if(change.wasAdded()) {
                    if(change.getKey().equals("PRODUSE")) {
                        model.loadProducts();
                    }
                    if(change.getKey().equals("INREGISTRARI_PRODUSE")) {
                        model.loadProductRecords();
                    }
                }
            }
        });
    }

    @Override
    public Region getView() {
        if(view.getRoot() != null) {
            return view.getRoot();
        } else {
            return view.build();
        }
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

    private void addProductRecordToDB(Runnable runnable, Double quantity) {
        Task<Void> taskDBload = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        model.addProductRecordToDB(quantity);
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

    private void editProductRecordHandler(ProductRecordDTO productRecord) {
        EditProductRecordController editProductRecordController = new EditProductRecordController(view.getStage(), productRecord);
    }

    private void searchOrderForProduct(ProductDTO productDTO) {
        Task<Void> taskDBSelect = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                model.searchForOrders(productDTO);
                return null;
            }
        };
        taskDBSelect.setOnSucceeded(evt -> {
            view.handleOrderSearchForProduct(productDTO, true);
        });
        taskDBSelect.setOnFailed(evt -> {
            view.handleOrderSearchForProduct(productDTO, false);
        });
        Thread dbTaskThread = new Thread(taskDBSelect);
        dbTaskThread.start();
    }

    private void setSelectedProduct(ProductDTO productDTO, OrderDTO order) {
        Platform.runLater(() -> {
            model.setSelectedProduct(productDTO);
            model.setAssociatedOrder(order);});
    }
}
