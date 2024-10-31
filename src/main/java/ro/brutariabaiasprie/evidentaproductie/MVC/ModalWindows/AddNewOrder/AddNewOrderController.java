package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewOrder;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.QuantityPrompt.QuantityPromptController;

import java.util.Objects;

public class AddNewOrderController extends ModalWindow {
    private final AddNewOrderModel model = new AddNewOrderModel();
    private final AddNewOrderView view;
    private final Stage stage;
    private final Stage PARENT_STAGE;

    public AddNewOrderController(Stage owner) {
        this.PARENT_STAGE = owner;
        stage = new Stage();
        model.loadProducts();
        view = new AddNewOrderView(model, stage, PARENT_STAGE, this::onWindowAction, this::onAddProductToOrderAction);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Adauga o comanda noua");

        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        stage.showAndWait();
    }

    private void onAddProductToOrderAction(ProductDTO product) {
        QuantityPromptController promptController = new QuantityPromptController(PARENT_STAGE, product);
        if(promptController.isSUCCESS()) {
            double quantity = promptController.getQuantity();
            OrderItemDTO orderItem = new OrderItemDTO(product.getId(), product.getName(), product.getUnitMeasurement(), quantity);
            model.addItem(orderItem);
        }
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        switch (actionType) {
            case CANCELLATION:
                stage.close();
        }
    }

}
