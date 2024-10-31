package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewProduct;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class AddNewProductView extends Parent implements Builder<Region> {
    private final AddNewProductModel model;
    private TextField productNameTextField;
    private ToggleGroup unitMeasurementGroup;
    private final Consumer<ACTION_TYPE> actionHandler;

    public AddNewProductView(AddNewProductModel model, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Label productNameLabel = new Label("Denumire:");
        productNameTextField = new TextField();

        Label unitMeasurementLabel = new Label("Unitatea de masura:");
        unitMeasurementGroup = new ToggleGroup();
        RadioButton kilogramsButton =  new RadioButton("KG");
        kilogramsButton.setToggleGroup(unitMeasurementGroup);
        kilogramsButton.setSelected(true);
        kilogramsButton.setUserData("KG");
        RadioButton pieceButton = new RadioButton("BUC");
        pieceButton.setToggleGroup(unitMeasurementGroup);
        pieceButton.setUserData("BUC");

        GridPane gridPane = new GridPane();
        gridPane.add(productNameLabel, 0, 0, 1, 1);
        gridPane.add(productNameTextField, 1, 0, 2, 1);
        gridPane.add(unitMeasurementLabel, 0, 1);
        gridPane.add(kilogramsButton, 1, 1);
        gridPane.add(pieceButton, 2, 1);

        gridPane.getStyleClass().add("grid-conn");

        HBox buttonsContainer = createWindowButtons();

        root.setSpacing(16);
        root.getChildren().addAll(gridPane, buttonsContainer);
        root.getStyleClass().add("modal-window");

        return root;
    }

    private HBox createWindowButtons() {
        SceneButton confirmButton = new SceneButton("OK", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    public String getProductName() {
        return this.productNameTextField.getText();
    }

    public String getUnitMeasurement() {
        return unitMeasurementGroup.getSelectedToggle().getUserData().toString();
    }
}
