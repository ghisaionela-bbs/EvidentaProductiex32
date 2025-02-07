package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.NumericInput.NumericInputController;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderView extends Parent implements Builder<Region> {
    private final Stage stage;
    private final OrderModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final WINDOW_TYPE type;

    private Runnable deleteOrderHandler;
    private Supplier<Boolean> isOrderStarted;
    private TextField productNameTextField;
    private Button deleteButton = new Button();
    private ComboBox<Product> productComboBox;
    private TextField quantityTextField;
    private CheckBox isClosedCheckBox;
    private final DatePicker datePicker = new DatePicker();
    private final Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);

    public OrderView(Stage stage, OrderModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler,
                     Supplier<Boolean> isOrderStarted) {
        this.stage = stage;
        this.model = model;
        this.actionHandler = actionHandler;
        this.type = type;
        this.isOrderStarted = isOrderStarted;
    }

    public void setDeleteOrderHandler(Runnable deleteOrderHandler) {
        this.deleteOrderHandler = deleteOrderHandler;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setCenter(createContentSection());
        root.setBottom(createSceneButtons());
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        root.getStyleClass().add("entry-view");
        return root;
    }

    /***
     * Creates the product information section with possibility of editing depending on the WINDOW_TYPE type.
     * @return GridPane with the product information
     */
    private GridPane createContentSection() {
        //  Title
        Label orderIdLabel  = new Label();
        orderIdLabel.getStyleClass().add("title");
        orderIdLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(orderIdLabel, Priority.ALWAYS);
        HBox titleContainer = new HBox(orderIdLabel);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        // Analytics
        Button analyticsButton = new Button("Raport avansat");
        analyticsButton.setGraphic(new FontIcon("mdi2c-chart-box"));
        analyticsButton.getStyleClass().add("sub-main-window-button");
        analyticsButton.getStyleClass().add("filled-button");

        // Product
        Label productLabel = new Label("Produs:");
        productComboBox = new ComboBox<>();
        Callback<ListView<Product>, ListCell<Product>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Product> call(ListView<Product> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Product item, boolean empty) {
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
        productComboBox.setCellFactory(cellFactory);
        productComboBox.setButtonCell(cellFactory.call(null));
        productComboBox.setItems(model.getProducts());
        productComboBox.setMaxWidth(Double.MAX_VALUE);
        productComboBox.setPromptText("Selecteaza produsul");
        VBox productSection = new VBox(productLabel, productComboBox);
        productSection.getStyleClass().add("section");
        productSection.getStyleClass().add("vbox-layout");

        Label quantityLabel = new Label("Cantitate");
        quantityTextField = createQuantityField();
        quantityTextField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(quantityTextField, Priority.ALWAYS);
        Button numpadButton = new Button();
        numpadButton.setGraphic(new FontIcon("mdi2n-numeric"));
        numpadButton.setOnAction(event -> {
            Double quantity = 0.00;
            if(!quantityTextField.getText().isEmpty()) {
                quantity = Double.parseDouble(quantityTextField.getText());
            }
            NumericInputController numericInputController = new NumericInputController(stage, quantity);
            if(numericInputController.isSUCCESS()) {
                quantityTextField.textProperty().set(numericInputController.getInput());
            }
        });
        numpadButton.getStyleClass().add("filled-button");
        numpadButton.setMaxHeight(Double.MAX_VALUE);
        HBox quantityFieldContainer = new HBox(quantityTextField, numpadButton);
        VBox quantitySection = new VBox(quantityLabel, quantityFieldContainer);
        quantitySection.getStyleClass().add("section");
        quantitySection.getStyleClass().add("vbox-layout");

        Label dateLabel = new Label("Data");
//        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
        datePicker.setShowWeekNumbers(false);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        VBox dateSection = new VBox(dateLabel, datePicker);
        dateSection.getStyleClass().add("section");
        dateSection.getStyleClass().add("vbox-layout");


        Label hourLabel = new Label("Ora:");
        VBox hourSection = new VBox(hourLabel, new HBox(hourSpinner, minuteSpinner));
        hourSection.getStyleClass().add("section");
        hourSection.getStyleClass().add("vbox-layout");

        isClosedCheckBox = new CheckBox("Comanda inchisa:");
        isClosedCheckBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

//        GridPane numpad = createNumpad();

        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                orderIdLabel.setText("Adaugare comanda noua");
                LocalTime localTime = LocalTime.now();
                datePicker.setValue(LocalDate.now());
                hourSpinner.getValueFactory().setValue(localTime.getHour());
                minuteSpinner.getValueFactory().setValue(localTime.getMinute());
                break;
            case VIEW:
                productComboBox.setDisable(true);
                quantityTextField.setDisable(true);
                isClosedCheckBox.setDisable(true);
//                numpad.setDisable(true);
                datePicker.setDisable(true);
                hourSpinner.setEditable(true);
                minuteSpinner.setEditable(true);
            case EDIT:
                orderIdLabel.setText("Comanda " + model.getOrder().getId());
                productComboBox.getSelectionModel().select(model.getOrder().getProduct());
                quantityTextField.setText(String.format("%.2f", model.getOrder().getQuantity()));
//                dateLabel.setText("Introdusa la: " + dateTimeFormatter.format(model.getOrder().getDateTimeInserted()));
                isClosedCheckBox.setSelected(model.getOrder().isClosed());
                break;
        }

        if(type == WINDOW_TYPE.EDIT) {
            LocalDateTime dateScheduled = model.getOrder().getDateScheduled().toLocalDateTime();
            datePicker.setValue(dateScheduled.toLocalDate());
            hourSpinner.getValueFactory().setValue(dateScheduled.getHour());
            minuteSpinner.getValueFactory().setValue(dateScheduled.getMinute());

        }

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        // Adding the controls
        gridPane.add(titleContainer, 0, 0, 2, 1);
        if(type == WINDOW_TYPE.EDIT) {
            deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteOrderHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            titleContainer.getChildren().add(deleteButton);
        }
        gridPane.add(analyticsButton, 0, 1);
        gridPane.add(productSection, 0, 2);
        gridPane.add(quantitySection, 0, 3);
//        gridPane.add(numpad, 1, 1, 1, 4);
        gridPane.add(dateSection, 0, 4);
        gridPane.add(hourSection, 0, 5);
        gridPane.add(isClosedCheckBox, 0, 6);
//        GridPane.setHalignment(isClosedCheckBox, HPos.RIGHT);
        // adding constraints
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

    private HBox createSceneButtons() {
        SceneButton confirmButton = new SceneButton("OK", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    public Product getProduct() {
        return productComboBox.getValue();
    }

    public String getQuantityInput() {
        return quantityTextField.getText();
    }

    public boolean isClosed() {
        return isClosedCheckBox.isSelected();
    }

    public Timestamp getDateScheduled() {
        LocalDateTime localDateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
        return Timestamp.valueOf(localDateTime);
    }

    public void setDeleteButtonVisibility() {
        deleteButton.setVisible(!isOrderStarted.get());
    }
}
