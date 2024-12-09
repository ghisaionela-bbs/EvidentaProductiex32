package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Production;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.ModifiedTableData;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation.OrderAssociationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

public class ProductionController implements SceneController {
    private final ProductionView view;
    private final ProductionModel model = new ProductionModel();

    public ProductionController(Stage owner) {
        model.loadRecords();
        this.view = new ProductionView(model, owner,
                this::loadProducts,
                this::addProductRecordToDB,
                this::searchOrderForProduct,
                this::setSelectedProduct);
        User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

        DBConnectionService.getModifiedTables().addListener((MapChangeListener<String, ModifiedTableData>) change -> {
            Platform.runLater(model::loadAssociatedOrder);
            if(change.wasAdded()) {
                Platform.runLater(() -> {
                    if(change.getKey().equals("PRODUSE")) {
                        model.loadProducts();
                        model.loadRecords();
                    }
                    if(model.getSelectedProduct()!= null) {
                        ModifiedTableData tableData = change.getValueAdded();
                        // When there was an operation with the current selected product
                        if(model.getSelectedProduct().getId() == tableData.getRowId()) {
                            // If it was an update
                            if(tableData.getOperation_type().equals("UPDATE")) {
                                // Update the product
                                model.loadSelectedProduct();
                                // When the user is not an administrator check the group of the product
                                if(user.getRoleId() != 1 || user.getRoleId() != 2) {
                                    if(model.getSelectedProduct().getGroup() == null) {
                                        model.setSelectedProduct(null);
                                        model.setAssociatedOrder(null);
                                        new WarningController(owner, "Produsului selectat i s-a sters grupa.\nVa rugam selectati un alt produs (si o alta comanda)!");
                                        return;
                                    }
                                }

                                model.loadAssociatedOrder();

                            } else if (tableData.getOperation_type().equals("DELETE")) {
                                model.setSelectedProduct(null);
                                model.setAssociatedOrder(null);
                                new WarningController(owner, "Produsul selectat a fost sters.\nVa rugam selectati un alt produs (si o alta comanda)!");
                                return;
                            }
                        }
                    }

                    if(change.getKey().equals("REALIZARI")) {
                        model.loadRecords();
                    }
                    if(change.getKey().equals("COMENZI")) {
                        model.loadProducts();
                        //when we have an assciated order
                        if(model.getAssociatedOrder() != null) {
                            ModifiedTableData tableData = change.getValueAdded();
                            if(tableData.getOperation_type().equals("UPDATE")) {
                                // if the order changed it's product
                                Order oldOrder = model.getAssociatedOrder();
                                model.loadAssociatedOrder();
                                if(oldOrder.getProduct().getId() != model.getAssociatedOrder().getProduct().getId()) {
                                    model.setSelectedProduct(null);
                                    model.setAssociatedOrder(null);
                                    new WarningController(owner, "Produsul de pe comanda selectata a fost schimbat! Va rugam selectati un alt produs (si o alta comanda!");
                                    return;
                                }
                                // if the order was closed
                                if(model.getAssociatedOrder().isClosed()) {
                                    new WarningController(owner, "Comanda a fost inchisa! Va rugam selectati o alta comanda!");
                                    OrderAssociationController orderAssociationController = new OrderAssociationController(owner, model.getSelectedProduct(), false);
                                    if(orderAssociationController.isSUCCESS()) {
                                        model.setAssociatedOrder(orderAssociationController.getOrder());
                                    } else {
                                        model.setAssociatedOrder(null);
                                    }

                                    return;
                                }

                            }
                            if(tableData.getOperation_type().equals("DELETE"))   {
                                new WarningController(owner, "Comanda a fost stearsa! Va rugam selectati o alta comanda!");
                                OrderAssociationController orderAssociationController = new OrderAssociationController(owner, model.getSelectedProduct(), false);
                                if(orderAssociationController.isSUCCESS()) {
                                    model.setAssociatedOrder(orderAssociationController.getOrder());
                                } else {
                                    model.setAssociatedOrder(null);
                                }
                                return;
                            }
                        }
                    }
                });
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

    private void searchOrderForProduct(Product product) {
        Task<Void> taskDBSelect = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                model.searchForOrders(product);
                return null;
            }
        };
        taskDBSelect.setOnSucceeded(evt -> {
            view.handleOrderSearchForProduct(product, true);
        });
        taskDBSelect.setOnFailed(evt -> {
            view.handleOrderSearchForProduct(product, false);
        });
        Thread dbTaskThread = new Thread(taskDBSelect);
        dbTaskThread.start();
    }

    private void setSelectedProduct(Product productDTO, Order order) {
        Platform.runLater(() -> {
            model.setSelectedProduct(productDTO);
            model.setAssociatedOrder(order);});
    }

    public void setOrder(Order order) {
        model.setAssociatedOrder(order);
        model.setSelectedProduct(order.getProduct());
    }
}
