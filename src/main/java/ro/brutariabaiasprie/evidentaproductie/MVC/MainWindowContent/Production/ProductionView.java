package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Production;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.DTO.Order;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation.OrderAssociationController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductionView extends Parent implements Builder<Region> {
    private final Consumer<Runnable> productSelectionActionHandler;
    private final BiConsumer<Runnable, Double> productRecordAddActionHandler;
    private final Consumer<ProductDTO> searchOrderForProductHandler;
    private final BiConsumer<ProductDTO, OrderDTO> setSelectedProductHandler;
    private final Consumer<ProductRecordDTO> editProductRecordHandler;

    private final ProductionModel model;
    private final Stage stage;
    private final User user;

    private TabPane root;
    private TextField quantityTextField;
    private GridPane numpad;
    private VBox leftSection;
    private ListView<ProductDTO> productsListView;
    private Label arrowIcon;
    private Label orderLabel;
    private HBox quantityInputContainer;

    public Stage getStage() {
        return stage;
    }

    public ProductionView(ProductionModel model, Stage stage,
                          Consumer<Runnable> productSelectionActionHandler,
                          BiConsumer<Runnable, Double>  productRecordAddActionHandler,
                          Consumer<ProductDTO> searchOrderForProductHandler,
                          BiConsumer<ProductDTO, OrderDTO> setSelectedProductHandler, Consumer<ProductRecordDTO> editProductRecordHandler) {
        this.productSelectionActionHandler = productSelectionActionHandler;
        this.productRecordAddActionHandler = productRecordAddActionHandler;
        this.model = model;
        this.stage = stage;
        this.searchOrderForProductHandler = searchOrderForProductHandler;
        this.setSelectedProductHandler = setSelectedProductHandler;
        this.editProductRecordHandler = editProductRecordHandler;
        this.user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
    }

    @Override
    public Region build() {
        Button btnProductChoice = createBtnProductChoice();
        quantityTextField = createQuantityField();
        quantityTextField.setMaxWidth(Region.USE_COMPUTED_SIZE);
        Label unitMeasurementLabel = new Label();
        unitMeasurementLabel.textProperty().bind(Bindings.createStringBinding(() ->
        {
            String text = "";
            if(model.getSelectedProduct() != null) {
                text = model.getSelectedProduct().getUnitMeasurement();
            }
            return text;
        }, model.selectedProductProperty()));
        HBox.setHgrow(quantityTextField, Priority.ALWAYS);
        unitMeasurementLabel.getStyleClass().add("unit-measurement-indicator");
        unitMeasurementLabel.setMaxWidth(Region.USE_COMPUTED_SIZE);


        quantityInputContainer = new HBox(quantityTextField, unitMeasurementLabel);
        quantityInputContainer.setAlignment(Pos.CENTER);
        quantityInputContainer.setSpacing(8);
        quantityInputContainer.getStyleClass().add("production-quantity-input");

        numpad = createNumpad();

        leftSection = new VBox(btnProductChoice, quantityInputContainer, numpad);
        leftSection.getStyleClass().add("production-input-section");

        btnProductChoice.prefHeightProperty().bind(leftSection.heightProperty().divide(6));
        quantityTextField.prefHeightProperty().bind(leftSection.heightProperty().divide(6));


        VBox.setVgrow(numpad, Priority.ALWAYS);
        productsListView = createProductListView();
        productsListView.maxWidthProperty().bind(stage.widthProperty().divide(3));

        Label titleLabel = new Label("Inregistrari introduse");
        TableView<ProductRecordDTO> tableView = createProductRecordTableView();
        tableView.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox records = new VBox(titleLabel, tableView);
        records.setAlignment(Pos.CENTER);
        HBox.setHgrow(records, Priority.ALWAYS);

        TabPane root = new TabPane(createOrdersTab(), createRecordsTab());
//        root = new HBox();
//        root.getChildren().addAll(leftSection, records);
//        root.setSpacing(8);
        return root;
    }

    public Region getRoot() {
        return root;
    }

    private Button createBtnProductChoice() {
        VBox infoContainer = new VBox();

        Label selectedProductNameLabel = new Label();
        selectedProductNameLabel.getStyleClass().add("product-name");
        selectedProductNameLabel.textProperty().bind(Bindings.createStringBinding(() ->
        {
            String text = "Nici un produs selectat";
            if(model.getSelectedProduct() != null) {
                text = model.getSelectedProduct().getName();
            }
            return text;
        }, model.selectedProductProperty()));

        selectedProductNameLabel.setWrapText(true);

        orderLabel = new Label();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        orderLabel.textProperty().bind(Bindings.createStringBinding(() ->
                {
                    if(model.getAssociatedOrder() == null) {
                        orderLabel.getStyleClass().add("warning");
                        return "!!! Nici o comanda asociata !!!";
                    }
                    orderLabel.getStyleClass().remove("warning");
                    OrderDTO order = model.getAssociatedOrder();
                    return "Asociat la comanda: " + order.getID() + " din " + dateTimeFormatter.format(order.getDateAndTimeInserted());

                },
                model.associatedOrderProperty()
        ));
        orderLabel.wrapTextProperty().set(true);
        orderLabel.getStyleClass().add("select-product-button-order-info");

        infoContainer.getChildren().addAll(selectedProductNameLabel, orderLabel);
        infoContainer.setAlignment(Pos.CENTER);

        arrowIcon = new Label("▼");
        arrowIcon.setAlignment(Pos.CENTER_RIGHT);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(infoContainer, arrowIcon);
        StackPane.setAlignment(arrowIcon, Pos.CENTER_RIGHT);

        Button btnProductChoice = new Button();
        btnProductChoice.setGraphic(stackPane);
        btnProductChoice.getStyleClass().add("production-product-selection-button");
        btnProductChoice.prefWidthProperty().bind(stage.widthProperty().divide(3));
        btnProductChoice.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnProductChoice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                leftSection.setDisable(true);
                productSelectionActionHandler.accept(() -> {
                    if(leftSection.getChildren().contains(productsListView)) {
                        leftSection.setDisable(false);
                        leftSection.getChildren().addAll(quantityInputContainer, numpad);
                        leftSection.getChildren().remove(productsListView);
                        arrowIcon.setText("▼");
                    } else {
                        leftSection.setDisable(false);
                        leftSection.getChildren().removeAll(quantityInputContainer, numpad);
                        leftSection.getChildren().add(productsListView);
                        arrowIcon.setText("▲");
                    }

                });
            }
        });
        return btnProductChoice;
    }

    private TextField createQuantityField() {
        TextField quantityTextField = new TextField();
        quantityTextField.promptTextProperty().set("----------.--");

        quantityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.isEmpty()) {
                    if(model.getSelectedProduct() == null) {
                        WarningController warningController = new WarningController(stage, "Selectati produsul pentru care doriti sa adaugati inregistrarea!");
                        quantityTextField.clear();
                        return;
                    }

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

    private GridPane createNumpad() {
        GridPane numpad = new GridPane();
        Button num1 = new Button("1");
        num1.setOnAction(handleBtnNumpadOnAction());
        Button num2 = new Button("2");
        num2.setOnAction(handleBtnNumpadOnAction());
        Button num3 = new Button("3");
        num3.setOnAction(handleBtnNumpadOnAction());
        Button num4 = new Button("4");
        num4.setOnAction(handleBtnNumpadOnAction());
        Button num5 = new Button("5");
        num5.setOnAction(handleBtnNumpadOnAction());
        Button num6 = new Button("6");
        num6.setOnAction(handleBtnNumpadOnAction());
        Button num7 = new Button("7");
        num7.setOnAction(handleBtnNumpadOnAction());
        Button num8 = new Button("8");
        num8.setOnAction(handleBtnNumpadOnAction());
        Button num9 = new Button("9");
        num9.setOnAction(handleBtnNumpadOnAction());
        Button num0 = new Button("0");
        num0.setOnAction(handleBtnNumpadOnAction());
        Button numDot = new Button(".");
        numDot.setOnAction(handleBtnNumpadOnAction());
        Button numDel = new Button("⌫");
        numDel.setOnAction(handleBtnNumpadOnAction());
        Button numAdd = new Button("Adauga +");
        numAdd.setOnAction(handleBtnNumpadOnAction());
        numAdd.getStyleClass().add("numpad-button-add");

        numpad.add(num1, 0, 0);
        GridPane.setVgrow(num1, Priority.ALWAYS);
        GridPane.setHgrow(num1, Priority.ALWAYS);
        GridPane.setFillWidth(num1, true);
        GridPane.setFillHeight(num1, true);
        num1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num2, 1, 0);
        GridPane.setVgrow(num2, Priority.ALWAYS);
        GridPane.setHgrow(num2, Priority.ALWAYS);
        GridPane.setFillWidth(num2, true);
        GridPane.setFillHeight(num2, true);
        num2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num3, 2, 0);
        GridPane.setVgrow(num3, Priority.ALWAYS);
        GridPane.setHgrow(num3, Priority.ALWAYS);
        GridPane.setFillWidth(num3, true);
        GridPane.setFillHeight(num3, true);
        num3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num4, 0, 1);
        GridPane.setVgrow(num4, Priority.ALWAYS);
        GridPane.setHgrow(num4, Priority.ALWAYS);
        GridPane.setFillWidth(num4, true);
        GridPane.setFillHeight(num4, true);
        num4.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num5, 1, 1);
        GridPane.setVgrow(num5, Priority.ALWAYS);
        GridPane.setHgrow(num5, Priority.ALWAYS);
        GridPane.setFillWidth(num5, true);
        GridPane.setFillHeight(num5, true);
        num5.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num6, 2, 1);
        GridPane.setVgrow(num6, Priority.ALWAYS);
        GridPane.setHgrow(num6, Priority.ALWAYS);
        GridPane.setFillWidth(num6, true);
        GridPane.setFillHeight(num6, true);
        num6.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num7, 0, 2);
        GridPane.setVgrow(num7, Priority.ALWAYS);
        GridPane.setHgrow(num7, Priority.ALWAYS);
        GridPane.setFillWidth(num7, true);
        GridPane.setFillHeight(num7, true);
        num7.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num8, 1, 2);
        GridPane.setVgrow(num8, Priority.ALWAYS);
        GridPane.setHgrow(num8, Priority.ALWAYS);
        GridPane.setFillWidth(num8, true);
        GridPane.setFillHeight(num8, true);
        num8.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num9, 2, 2);
        GridPane.setVgrow(num9, Priority.ALWAYS);
        GridPane.setHgrow(num9, Priority.ALWAYS);
        GridPane.setFillWidth(num9, true);
        GridPane.setFillHeight(num9, true);
        num9.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num0, 0, 3);
        GridPane.setVgrow(num0, Priority.ALWAYS);
        GridPane.setHgrow(num0, Priority.ALWAYS);
        GridPane.setFillWidth(num0, true);
        GridPane.setFillHeight(num0, true);
        num0.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(numDot, 1, 3);
        GridPane.setVgrow(numDot, Priority.ALWAYS);
        GridPane.setHgrow(numDot, Priority.ALWAYS);
        GridPane.setFillWidth(numDot, true);
        GridPane.setFillHeight(numDot, true);
        numDot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(numDel, 2, 3);
        GridPane.setVgrow(numDel, Priority.ALWAYS);
        GridPane.setHgrow(numDel, Priority.ALWAYS);
        GridPane.setFillWidth(numDel, true);
        GridPane.setFillHeight(numDel, true);
        numDel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(numAdd, 0, 4);
        GridPane.setColumnSpan(numAdd, 3);
        GridPane.setVgrow(numAdd, Priority.ALWAYS);
        GridPane.setHgrow(numAdd, Priority.ALWAYS);
        GridPane.setFillWidth(numAdd, true);
        GridPane.setFillHeight(numAdd, true);
        numAdd.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(20);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(33.33);

        numpad.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints);
        numpad.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints, rowConstraints, rowConstraints);

        numpad.getStyleClass().add("numpad");
        return numpad;
    }

    private EventHandler<ActionEvent> handleBtnNumpadOnAction() {
        return new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                Button node = (Button) event.getSource() ;
                String value = node.getText();
                //Handle warning screen for no product selected
                if(model.getSelectedProduct() == null) {
                    WarningController warningController = new WarningController(stage, "Selectati produsul pentru care doriti sa adaugati inregistrarea!");
                    return;
                }
                if("0123456789.".contains(value)) {
                    String quantity = quantityTextField.getText();
                    quantityTextField.setText(quantity + value);
                } else if ("⌫".equals(value)) {
                    String quantity = quantityTextField.getText();
                    if (quantity.isEmpty()){
                        return;
                    }
                    quantityTextField.setText(quantity.substring(0, quantity.length() - 1));
                } else if ("Adauga +".equals(value)) {
                    //Handle warning for no quantity entered
                    if(quantityTextField.getText().isEmpty() || quantityTextField.getText() == null) {
                        WarningController warningController = new WarningController(stage, "Introduceti cantitatea pentru produsul:\n" +
                                model.getSelectedProduct().getName());
                        return;
                    }
                    double quantity = Double.parseDouble(quantityTextField.getText());
                    if(quantity <= 0) {
                        WarningController warningController = new WarningController(stage, "Cantitatea trebuie sa fie mai mare de 0!");
                        return;
                    }
                    //Ask for confirmation
                    ConfirmationController confirmationController = new ConfirmationController(stage, "Confirmati introducere inregistrare",
                            "Doriti sa introduceti " + quantity + " " + model.getSelectedProduct().getUnitMeasurement() +
                                    " pentru produsul\n" + model.getSelectedProduct().getName());
                    if(!confirmationController.isSUCCESS()){
                        return;
                    }
                    //Add the product record
                    leftSection.setDisable(true);
                    productRecordAddActionHandler.accept(() -> {
                        leftSection.setDisable(false);
                        quantityTextField.textProperty().set("");
                    }, quantity);
                }
            }
        };
    }

//    private ListView<ProductRecordDTO> createProductRecordListView() {
//        ListView<ProductRecordDTO> listView = new ListView<>();
//
//        listView.setCellFactory(new Callback<ListView<ProductRecordDTO>, ListCell<ProductRecordDTO>>() {
//            @Override
//            public ListCell<ProductRecordDTO> call(ListView<ProductRecordDTO> param) {
//                return new ListCell<ProductRecordDTO>() {
//
//                    @Override
//                    protected void updateItem(ProductRecordDTO item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item == null || empty) {
//                            setText(null);
//                            setGraphic(null);
//                        } else {
//                            Button btnEdit = new Button("✎");
//                            btnEdit.setOnAction(handleBtnEditRecordOnAction(item));
//
//                            Label lblProductName = new Label(item.getName());
//                            lblProductName.getStyleClass().add("den-prod-record-list-cell");
//                            Label lblProductDetails = new Label(" Cantitate : " + String.format("%.2f", item.getQuantity()) + " " + item.getUnitMeasurement());
//                            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                            Label lblDateTime = new Label(" Data si ora: " + dateTimeFormatter.format(item.getDateAndTimeInserted()));
//                            HBox productCell = new HBox();
//                            productCell.getChildren().addAll(lblProductName, lblProductDetails, lblDateTime, btnEdit);
//
//                            productCell.setSpacing(10);
//                            productCell.setPadding(new Insets(10));
//                            setText(null);
//                            setGraphic(productCell);
//                        }
//                    }
//                };
//            }
//        });
//        listView.setItems(model.getProductRecords());
//        return listView;
//    }

    private TableView<ProductRecordDTO> createProductRecordTableView() {
        TableView<ProductRecordDTO> tableView = new TableView<>();

        tableView.setPlaceholder(new Label("Nu exista inregistrari."));

        TableColumn<ProductRecordDTO, String> nameColumn = new TableColumn<>("Produs");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text txtName = new Text(item);
                    txtName.getStyleClass().add("text");
                    txtName.wrappingWidthProperty().bind(widthProperty());
                    setGraphic(txtName);
                    setWrapText(true);
                    setText(item);
                }
            }
        });
        tableView.getColumns().add(nameColumn);

        TableColumn<ProductRecordDTO, Double> quantityColumn = new TableColumn<>("Cantitate");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });
        tableView.getColumns().add(quantityColumn);

        TableColumn<ProductRecordDTO, String> unitMeasurementColumn = new TableColumn<>("UM");
        unitMeasurementColumn.setCellValueFactory(new PropertyValueFactory<>("unitMeasurement"));
        tableView.getColumns().add(unitMeasurementColumn);

        TableColumn<ProductRecordDTO, Timestamp> dateAndTimeColumn = new TableColumn<>("Data si ora");
        dateAndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateAndTimeInserted"));
        quantityColumn.setPrefWidth(100);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateAndTimeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });
        tableView.getColumns().add(dateAndTimeColumn);

        int percentEditBtn = 0;
        if(user.getID_ROLE() == 0 || user.getID_ROLE() == 1) {
            TableColumn<ProductRecordDTO, Integer> editBtnColumn = new TableColumn<>();
            editBtnColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
            editBtnColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);

                        Button editButton = new Button();
                        editButton.setGraphic(new FontIcon("mdi2s-square-edit-outline"));

                        editButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
                        editButton.getStyleClass().add("edit-button");
                        editButton.getStyleClass().add("filled-button");

                        editButton.setOnAction(event -> {
                            editProductRecordHandler.accept(getTableRow().getItem());
                        });
                        setGraphic(editButton);
                    }
                }
            });
            tableView.getColumns().add(editBtnColumn);
            percentEditBtn = 10;
            editBtnColumn.setMaxWidth(1f * Integer.MAX_VALUE * percentEditBtn);
        }

        int percentName = 40 - percentEditBtn;
        int percentQuantity = 25;
        int percentUnitMeasurement = 10;
        int percentDateAndTime = 25;

        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * percentName); // 40% width
        quantityColumn.setMaxWidth( 1f * Integer.MAX_VALUE * percentQuantity ); // 30% width
        unitMeasurementColumn.setMaxWidth(1f * Integer.MAX_VALUE * percentUnitMeasurement); // 10% width
        dateAndTimeColumn.setMaxWidth( 1f * Integer.MAX_VALUE * percentDateAndTime ); // 50% width

        tableView.getStyleClass().add("tbl-product-record-view");
        tableView.setItems(model.getProductRecords());

        return tableView;
    }

    private ListView<ProductDTO> createProductListView() {
        ListView<ProductDTO> listView = new ListView<>();
        listView.setCellFactory(new Callback<ListView<ProductDTO>, ListCell<ProductDTO>>() {
            @Override
            public ListCell<ProductDTO> call(ListView<ProductDTO> param) {
                return new ListCell<ProductDTO>() {
                    {
                        setPrefWidth(0);
                    }
                    @Override
                    protected void updateItem(ProductDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label lblProductName = new Label(item.getName());
                            lblProductName.getStyleClass().add("den-prod-record-list-cell");
                            lblProductName.setWrapText(true);
                            Label lblProductDetails = new Label(item.getUnitMeasurement());
                            VBox container = new VBox();
                            container.getChildren().addAll(lblProductName, lblProductDetails);
                            container.setSpacing(10);
                            container.setPadding(new Insets(10));
                            setText(null);
                            setGraphic(container);
                        }
                    }
                };
            }
        });
        //Setting up double click for elements in listview
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                //Use ListView's getSelected Item
                ProductDTO productDTO = listView.getSelectionModel().getSelectedItem();
                handleListViewItemSelected(productDTO);
                arrowIcon.setText("▼");
            }
        });
        //Setting up double tap for elements in listview
        listView.setOnTouchPressed(event -> {
            if (event.getTouchCount() == 2) {
                //Use ListView's getSelected Item
                ProductDTO productDTO = listView.getSelectionModel().getSelectedItem();
                handleListViewItemSelected(productDTO);
                arrowIcon.setText("▼");
            }
        });
        listView.setItems(model.getProducts());
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }

    private void handleListViewItemSelected(ProductDTO product) {
        searchOrderForProductHandler.accept(product);
    }

    public void handleOrderSearchForProduct(ProductDTO product, boolean isFound) {
        OrderAssociationController orderAssociationController = new OrderAssociationController(stage, product);
        if(orderAssociationController.isSUCCESS()) {
            leftSection.getChildren().addAll(quantityInputContainer, numpad);
            leftSection.getChildren().remove(productsListView);
            setSelectedProductHandler.accept(product, orderAssociationController.getOrder());
        }

    }

    private Tab createOrdersTab() {
        Tab ordersTab = new Tab("Comenzi");
        ordersTab.setClosable(false);
        return ordersTab;
    }

    private Tab createRecordsTab() {
        Tab recordsTab = new Tab("Realizari");
        recordsTab.setClosable(false);
        return recordsTab;
    }

    private Node createOrdersListview() {
        ListView<Order> listView = new ListView<>();
        return listView;
    }
}
