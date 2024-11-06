package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductTableSelection;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class ProductTableSelectionController extends ModalWindow {
    private final Stage stage;
    private ProductTableSelectionModel model;
    private ProductTableSelectionView view;
    private boolean SUCCESS = false;

    public ProductTableSelectionController(Stage owner) {
        stage = new Stage();
        model = new ProductTableSelectionModel();
        view = new ProductTableSelectionView(model, owner, this::onWindowAction);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());
        stage.setTitle("Selecteaza produsul");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        model.loadProducts();
        view.selectFirst();
        stage.showAndWait();
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            SUCCESS = true;
        }
        stage.close();
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }

    public ProductDTO getSelectedProduct() {
        return view.getSelectedProduct();
    }
}
