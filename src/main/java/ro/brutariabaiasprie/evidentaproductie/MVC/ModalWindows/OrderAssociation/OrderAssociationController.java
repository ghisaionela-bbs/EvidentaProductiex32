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
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewOrder.AddNewOrderView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class OrderAssociationController extends ModalWindow {
    private final OrderAssociationModel model;
    private final OrderAssociationView view;
    private final Stage stage;
    private boolean SUCCESS = false;

    public OrderAssociationController(Stage owner, ProductDTO product) {
        stage = new Stage();
        model = new OrderAssociationModel(product);
        view = new OrderAssociationView(model, stage, owner, this::onWindowAction, this::continueNoOrder);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());
        stage.setTitle("Selecteaza comanda");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        switch (actionType) {
            case CONFIRMATION:
                OrderResultsDTO orderResult = view.getSelectedOrder();
                if (orderResult == null) {
                    WarningController warning = new WarningController(stage, "Nu ati ales nici o comanda!");
                    return;
                }
                OrderDTO order = model.getSelectedOrder(orderResult.getORDER_ID());
                if(order!= null){
                    model.setOrder(order);
                    SUCCESS = true;
                    stage.close();
                }
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

    private void searchForOrders() {
        Task<Void> taskDBSelect = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                model.loadSearchResults();
                return null;
            }
        };
        taskDBSelect.setOnSucceeded(event -> view.showOrderResults());
        taskDBSelect.setOnFailed(event -> view.showNoOrderFoundWindow());
        Thread dbTaskThread = new Thread(taskDBSelect);
        dbTaskThread.start();
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }

    public OrderDTO getOrder() {
        return model.getOrder();
    }

    private void continueNoOrder(Boolean isConfimation) {
        if(isConfimation) {
            SUCCESS = true;
        }
        stage.close();
    }

}
