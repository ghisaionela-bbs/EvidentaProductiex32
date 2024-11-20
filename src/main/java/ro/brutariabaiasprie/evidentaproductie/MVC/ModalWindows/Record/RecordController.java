package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Record;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation.OrderAssociationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation.OrderAssociationModel;

import java.util.Objects;

public class RecordController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private RecordModel model;
    private RecordView view;

    public RecordController(Stage owner, WINDOW_TYPE type) {
        initStage(owner, type, new Record());
    }

    public RecordController(Stage owner, WINDOW_TYPE type, Record record) {
        initStage(owner, type, record);
    }

    private void initStage(Stage owner, WINDOW_TYPE type, Record record) {
        this.type = type;
        this.stage = new Stage();
        this.model = new RecordModel();
        this.model.setRecord(record);
        this.model.loadProducts();
        this.model.loadOrders();
        this.view = new RecordView(this.model, type, this::onWindowAction, this::editOrder);
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        switch (type) {
            case ADD:
                stage.setTitle("Adaugare realizare");
                break;
            case EDIT:
                stage.setTitle("Editare realizare");
                break;
            case VIEW:
                stage.setTitle("Vizualizare realizare");
                break;
        }

        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    private void editOrder(Product selectedProduct) {
        OrderAssociationController orderAssociationController = new OrderAssociationController(stage, selectedProduct, true);
        if(orderAssociationController.isSUCCESS()) {
            Order order = orderAssociationController.getOrder();
            if(order == null) {
                model.getRecord().setOrderId(0);
            } else {
                model.getRecord().setOrderId(order.getId());
            }
            view.setOrderId(model.getRecord().getOrderId());

        }
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(!this.isInputValid(type)) {
                return;
            }

            if (type == WINDOW_TYPE.EDIT) {
                model.getRecord().setProduct(view.getProduct());
                model.getRecord().setQuantity(Double.parseDouble(view.getQuantityInput()));
                model.updateRecord();
            }
        }
        stage.close();
    }

    private boolean isInputValid(WINDOW_TYPE type) {
        if(view.getProduct() == null) {
            new WarningController(stage, "Selectati produsul pentru care doriti sa introduceti inregistrarea!");
            return false;
        }
        if(view.getQuantityInput() == null) {
            new WarningController(stage, "Introduceti cantitatea!");
            return false;
        }
        if(view.getQuantityInput().isEmpty()) {
            new WarningController(stage, "Introduceti cantitatea!");
            return false;
        }
        double quantity = Double.parseDouble(view.getQuantityInput());
        if(quantity <= 0) {
            new WarningController(stage, "Cantitatea trebuie sa fie mai mare de 0!");
            return false;
        }
        return true;
    }
}
