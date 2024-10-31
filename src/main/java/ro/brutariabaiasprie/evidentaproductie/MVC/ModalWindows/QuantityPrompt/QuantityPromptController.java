package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.QuantityPrompt;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport.ExcelImportView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class QuantityPromptController extends ModalWindow {
    private final QuantityPromptModel model;
    private final QuantityPromptView view;
    private final Stage stage;
    private boolean SUCCESS = false;

    public QuantityPromptController(Stage owner, ProductDTO product) {
        model = new QuantityPromptModel(product);
        view = new QuantityPromptView(model, this::onWindowAction);

        stage = new Stage();
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Introduceti cantitatea");
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
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(view.getQuantity() == null || view.getQuantity().isEmpty()) {
                WarningController warningController = new WarningController(stage, "Nu ati introdus cantitatea!");
                return;
            }
            double quantity = Double.parseDouble(view.getQuantity());
            if(quantity <= 0) {
                WarningController warningController = new WarningController(stage, "Cantitatea trebuie sa fie mai mare de 0!");
                return;
            }
            model.setQuantity(quantity);
            SUCCESS = true;
        }
        stage.close();
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }

    public double getQuantity() {
        return model.getQuantity();
    }
}
