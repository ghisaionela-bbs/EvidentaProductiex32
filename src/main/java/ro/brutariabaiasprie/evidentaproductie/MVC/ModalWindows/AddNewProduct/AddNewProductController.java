package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewProduct;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;

import java.util.Objects;

public class AddNewProductController extends ModalWindow {
    private final Stage PARENT_STAGE;
    private final AddNewProductModel model = new AddNewProductModel();
    private final AddNewProductView view;
    private final Stage stage;
    private Boolean SUCCESS = false;

    public AddNewProductController(Stage owner) {
        this.PARENT_STAGE = owner;
        view = new AddNewProductView(model, this::onWindowAction);

        stage = new Stage();
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Adauga produs");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public Boolean getSUCCESS() {
        return SUCCESS;
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(view.getProductName() == null || view.getProductName().isEmpty()) {
                WarningController warningController = new WarningController(stage, "Denumirea nu poate ramane goala!");
            } else {
                model.addProduct(view.getProductName(), view.getUnitMeasurement());
                SUCCESS = true;
                stage.close();
            }
        } else {
            stage.close();
        }
    }
}
