package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderResultsDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class OrderAssociationController extends ModalWindow{
    private final OrderAssociationModel model;
    private OrderAssociationView view;
    private Stage stage;
    private boolean SUCCESS = false;
    private final Stage PARENT_STAGE;
    private final Product product;

    public OrderAssociationController(Stage owner, Product product) {
        //loading screen
        this.PARENT_STAGE = owner;
        this.product = product;
        this.stage = new Stage();

        this.model = new OrderAssociationModel(product);
        this.searchOrders(product);

        Scene loadingScene = new Scene(new LoadingView("Va rugam asteptati.\nSe cauta comenzi pentru produsul: " + product.getName()).build());
        loadingScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());
        this.stage.setTitle("Va rugam asteptati");
        this.stage.setScene(loadingScene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        this.stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        this.stage.showAndWait();

    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        switch (actionType) {
            case CONFIRMATION:
                Order order = view.getSelectedOrder();
                if (order == null) {
                    new WarningController(stage, "Nu ati ales nici o comanda!");
                    return;
                }
                model.setOrder(order);
                SUCCESS = true;
                stage.close();
                break;
            case CONTINUATION:
                if(model.getOrder() == null) {
                    ConfirmationController confirmation = new ConfirmationController(stage, "Atentie!",
                            "Sunteti sigur ca doriti sa continuati fara a asocia o comanda produsului?");
                    if(confirmation.isSUCCESS()) {
                        SUCCESS = true;
                        stage.close();
                    }
                }
                break;
            case CANCELLATION:
                stage.close();
                break;
        }
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }

    public Order getOrder() {
        return model.getOrder();
    }

    private void searchOrders(Product product) {
        Task<Void> taskDBSelect = new Task<>() {
            @Override
            protected Void call() {
                try {
                    model.loadSearchResults();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        taskDBSelect.setOnSucceeded(event -> handleOrderSearchSuccess());
        taskDBSelect.setOnFailed(event -> handleOrderSearchFail());
        Thread dbTaskThread = new Thread(taskDBSelect);
        dbTaskThread.start();
    }

    private void handleOrderSearchSuccess() {
        Platform.runLater(stage::close);
        this.stage = new Stage();
        this.view = new OrderAssociationView(model, this::onWindowAction);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());
        this.stage.setTitle("Va rugam asteptati");
        this.stage.setScene(scene);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        this.stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        this.stage.showAndWait();
    }

    private void handleOrderSearchFail() {
        Platform.runLater(stage::close);
        ConfirmationController confirmation = new ConfirmationController(PARENT_STAGE, "Atentie!",
                "Nu au fost gasite comenzi pentru produsul: " + product.getName() +
                        "\nSunteti sigur ca doriti sa adaugati inregistrari pentru acest produs?");
        if(confirmation.isSUCCESS()) {
            SUCCESS = true;
        }

    }

}
