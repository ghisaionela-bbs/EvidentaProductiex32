package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Record;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordView extends Parent implements Builder<Region> {
    private final RecordModel model;
    private final WINDOW_TYPE type;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final Consumer<Product> editOrderHandler;

    private TextField quantityTextField;
    private ComboBox<Product> productComboBox;
    private final Label orderLabel = new Label();

    public RecordView(RecordModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler, Consumer<Product> editOrderHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.type = type;
        this.editOrderHandler = editOrderHandler;
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

    private GridPane createContentSection() {
        //  Title
        Label recordIdLabel  = new Label();
        recordIdLabel.getStyleClass().add("title");
        // Name
        Label productLabel = new Label("Produs:");
        productComboBox = new ComboBox<>();
        Callback<ListView<Product>, ListCell<Product>> productCellFactory = new Callback<>() {
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
        productComboBox.setCellFactory(productCellFactory);
        productComboBox.setButtonCell(productCellFactory.call(null));
        productComboBox.setItems(model.getProducts());
        productComboBox.setMaxWidth(Double.MAX_VALUE);
        productComboBox.setPromptText("Selecteaza produsul");
        VBox productSection = new VBox(productLabel, productComboBox);
        productSection.getStyleClass().add("section");
        productSection.getStyleClass().add("vbox-layout");

        Label quantityLabel = new Label("Cantitate:");
        quantityTextField = createQuantityField();

        Button editOrderButton = new Button();
        editOrderButton.setGraphic(new FontIcon("mdi2s-square-edit-outline"));
        editOrderButton.getStyleClass().add("filled-button");
        editOrderButton.setOnAction(event -> editOrderHandler.accept(productComboBox.getValue()));
        GridPane.setHalignment(editOrderButton, HPos.RIGHT);

        Label dateLabel = new Label();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        // Setting up the values and properties of controls
        switch (type) {
            case VIEW:
                productComboBox.setDisable(true);
                quantityTextField.setDisable(true);
            case EDIT:
                recordIdLabel.setText("Realizarea " + model.getRecord().getId());
                if(model.getRecord().getOrderId() == 0) {
                    orderLabel.setText("Comanda: fara comanda");
                } else {
                    orderLabel.setText("Comanda: " + model.getRecord().getOrderId());
                }
                quantityTextField.setText(String.valueOf(model.getRecord().getQuantity()));
                productComboBox.getSelectionModel().select(model.getRecord().getProduct());
                dateLabel.setText("Introdusa la: " + dateTimeFormatter.format(model.getRecord().getDateTimeInserted()));
                break;
        }

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        // Adding the controls
        gridPane.add(recordIdLabel, 0, 0, 2, 1);
        gridPane.add(productSection, 0, 1, 2, 1);
        gridPane.add(orderLabel, 0, 3);
        gridPane.add(editOrderButton, 1,3);
        gridPane.add(quantityLabel, 0, 4, 2, 1);
        gridPane.add(quantityTextField, 0, 5, 2, 1);
        gridPane.add(dateLabel, 0, 6, 2, 1);
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

    protected TextField createQuantityField() {
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

    public void setOrderId(int orderId) {
        if(orderId == 0) {
            orderLabel.setText("Comanda: fara comanda");
        } else {
            orderLabel.setText("Comanda: " + orderId);
        }
    }
}
