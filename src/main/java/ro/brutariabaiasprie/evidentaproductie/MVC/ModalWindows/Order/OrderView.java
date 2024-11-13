package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderView extends Parent implements Builder<Region> {
    private final OrderModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final WINDOW_TYPE type;

    private Runnable deleteOrderHandler;
    private TextField productNameTextField;
    private ComboBox<Product> productComboBox;
    private TextField quantityTextField;
    private CheckBox isClosedCheckBox;

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
        root.getChildren().add(createWindowButtons());
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("modal-window");
        return root;
    }

    /***
     * Creates the product information section with possibility of editing depending on the WINDOW_TYPE type.
     * @return GridPane with the product information
     */
    private GridPane createContentSection() {
        // Title
        Label orderIdLabel  = new Label();
        // Product
        Label productLabel = new Label("Produs:");
        productComboBox = new ComboBox<>();
        Callback<ListView<Group>, ListCell<Group>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Group> call(ListView<Group> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Selectati produsul");
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };

        quantityTextField = createQuantityField();
        isClosedCheckBox = new CheckBox("Inchisa");

        // Setting up the container
        GridPane gridPane = new GridPane();
        for (int i = 0 ; i < gridPane.getRowCount(); i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(row);
        }
        for (int j = 0 ; j < gridPane.getColumnCount(); j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }
        gridPane.getStyleClass().add("grid-form");

        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                orderIdLabel.setText("Adaugare comanda nioua");
                break;
            case VIEW:
                productComboBox.setDisable(true);
                quantityTextField.setDisable(true);
                isClosedCheckBox.setDisable(true);
            case EDIT:
                productIdLabel.setText("Produs " + model.getProduct().getId());
                productNameTextArea.setText(model.getProduct().getName());
                if(model.getProduct().getUnitMeasurement().equals("KG")) {
                    unitMeasurementGroup.selectToggle(kgRadioButton);
                } else if (model.getProduct().getUnitMeasurement().equals("BUC")) {
                    unitMeasurementGroup.selectToggle(bucRadioButton);
                }
                if(model.getProduct().getGroup() != null) {
                    groupComboBox.getSelectionModel().select(model.getProduct().getGroup());
                }
                break;
        }

        // Adding the controls
        gridPane.add(productIdLabel, 0, 0);
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteProductHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            gridPane.add(deleteButton, 1, 0);
        }
        gridPane.add(productNameLabel, 0, 1, 2, 1);
        gridPane.add(productNameTextArea, 0, 2, 2, 1);
        gridPane.add(unitMeasurementLabel, 0, 3, 2, 1);
        gridPane.add(unitMeasurementChoiceBox, 0, 4, 2, 1);
        gridPane.add(groupLabel, 0, 5);
        gridPane.add(groupComboBox, 0, 6, 2, 1);

        return gridPane;
    }

    private TextField createQuantityField() {
        TextField quantityTextField = new TextField();
        quantityTextField.promptTextProperty().set("0.00");

        quantityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.isEmpty()) {
                    String filteredValue = newValue.replaceAll(" ", "");
                    Pattern pattern = Pattern.compile("(\\d{1,10}\\.\\d{1,2}|\\d{1,10}\\.|\\.\\d{1,2}|\\d{1,10})");
                    Matcher matcher = pattern.matcher(filteredValue);
                    if (matcher.find())
                    {
                        quantityTextField.setText(matcher.group(0));
                    } else {
                        quantityTextField.clear();
                    }
                } else {
                    quantityTextField.clear();
                }
            }
        });
        quantityTextField.getStyleClass().add("txt-fld-quantity");
        quantityTextField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return quantityTextField;
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
