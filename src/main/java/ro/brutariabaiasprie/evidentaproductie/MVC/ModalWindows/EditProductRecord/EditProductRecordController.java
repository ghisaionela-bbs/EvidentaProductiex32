package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.EditProductRecord;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ChoiceController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport.ExcelExportView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation.OrderAssociationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductTableSelection.ProductTableSelectionController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductTableSelection.ProductTableSelectionView;

import java.awt.*;
import java.util.*;

public class EditProductRecordController extends ModalWindow {
    private final EditProductRecordModel model;
    private final EditProductRecordView view;
    private final Stage stage;
    private final Stage PARENT_STAGE;
    private boolean SUCCESS = false;

    public EditProductRecordController(Stage owner, ProductRecordDTO productRecord) {
        PARENT_STAGE = owner;
        stage = new Stage();
        model = new EditProductRecordModel(productRecord);
        view = new EditProductRecordView(model, stage, this::onWindowAction, this::changeOrderHandler, this::changeProductHandler);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Editeaza inregistrarea");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void changeOrderHandler() {
        OrderAssociationController orderAssociationController = new OrderAssociationController(stage, model.getProduct());
    }

    private void changeProductHandler() {
        ProductTableSelectionController productSelection = new ProductTableSelectionController(stage);
        if(productSelection.isSUCCESS()) {
            ProductDTO product = productSelection.getSelectedProduct();
            if(model.getProductRecord().getORDER_ID() != null) {
                if(!model.checkOrderItems(product)){
                    Map<Object, String> options = new TreeMap<>();
                    options.put(0, "Da");
                    options.put(1, "Nu");
                    ChoiceController choiceController = new ChoiceController(stage, "Confimati",
                            "Produsul acesta nu apare in comanda asociata la inregistrarea curenta.\n" +
                                    "Doriti sa asociati inregistrarea la o alta comanda care contine produsul?",
                                    options);
                    if((Integer) choiceController.getChosenOption() == 0) {
                        OrderAssociationController orderAssociationController = new OrderAssociationController(stage, model.getProduct());
                    }
                    model.setProduct(product);
                } else {
                    model.setProduct(product);
                }
            }


        }
    }


    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            SUCCESS = true;
        } else {
            stage.close();
        }
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }
}
