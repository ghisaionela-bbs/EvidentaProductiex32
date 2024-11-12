package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.Builder;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class OrderView extends Parent implements Builder<Region> {
    private final OrderModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final WINDOW_TYPE type;

    private Runnable deleteOrderHandler;
    private TextField productNameTextField;

    public OrderView(OrderModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.type = type;
    }

    public void setDeleteOrderHandler(Runnable deleteOrderHandler) {
        this.deleteOrderHandler = deleteOrderHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.getChildren().addAll(createInputSection());
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere comanda");
            deleteButton.setOnAction(event -> deleteOrderHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: RED; -fx-text-fill: COLOR_WHITE");
            root.getChildren().add(deleteButton);
        }
        root.getChildren().add(createWindowButtons());
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("modal-window");
        return root;
    }

    private GridPane createInputSection() {
        GridPane gridPane = new GridPane();

        Label orderIdLabel = new Label();

        Label productNameLabel = new Label("Produs:");
        ComboBox<Product> productComboBox = new ComboBox<>();


        productNameTextField = new TextField();

//        gridPane.add(nameLabel, 0, 0);
//        gridPane.add(nameTextField, 1, 0);
//        VBox.setVgrow(gridPane, Priority.ALWAYS);
//
//        switch (type) {
//            case ADD:
//                break;
//            case VIEW:
//                nameTextField.setEditable(false);
//            case EDIT:
//                nameTextField.setText(model.getGroup().getName());
//                break;
//        }

        gridPane.getStyleClass().add("grid-form");
        return gridPane;
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
}
