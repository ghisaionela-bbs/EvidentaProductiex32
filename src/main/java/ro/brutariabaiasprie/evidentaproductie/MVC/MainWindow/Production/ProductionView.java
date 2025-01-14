package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Production;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductionProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Confirmation.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation.OrderAssociationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Record.RecordController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductionView extends Parent implements Builder<Region> {
    private final Consumer<Runnable> productSelectionActionHandler;
    private final BiConsumer<Runnable, Double> productRecordAddActionHandler;
    private final Consumer<Product> searchOrderForProductHandler;
    private final BiConsumer<Product, Order> setSelectedProductHandler;

    private final ProductionModel model;
    private final Stage stage;
    private final User user;

    private HBox root;
    private TextField quantityTextField;
    private GridPane numpad;
    private VBox leftSection;
    private ListView<ProductionProductDTO> productsListView;
    private Label arrowIcon;
    private Label orderLabel;
    private HBox quantityInputContainer;

    public Stage getStage() {
        return stage;
    }

    public ProductionView(ProductionModel model, Stage stage,
                          Consumer<Runnable> productSelectionActionHandler,
                          BiConsumer<Runnable, Double>  productRecordAddActionHandler,
                          Consumer<Product> searchOrderForProductHandler,
                          BiConsumer<Product, Order> setSelectedProductHandler) {
        this.productSelectionActionHandler = productSelectionActionHandler;
        this.productRecordAddActionHandler = productRecordAddActionHandler;
        this.model = model;
        this.stage = stage;
        this.searchOrderForProductHandler = searchOrderForProductHandler;
        this.setSelectedProductHandler = setSelectedProductHandler;
        this.user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
    }

    @Override
    public Region build() {
        Button btnProductChoice = createBtnProductChoice();
        btnProductChoice.getStyleClass().add("production-product-selection");
        quantityTextField = createQuantityField();
        quantityTextField.setMaxWidth(Double.MAX_VALUE);
//        Label unitMeasurementLabel = new Label();
//        unitMeasurementLabel.textProperty().bind(Bindings.createStringBinding(() ->
//        {
//            String text = "";
//            if(model.getSelectedProduct() != null) {
//                text = model.getSelectedProduct().getUnitMeasurement();
//            }
//            return text;
//        }, model.selectedProductProperty()));
//        HBox.setHgrow(quantityTextField, Priority.ALWAYS);
//        unitMeasurementLabel.getStyleClass().add("unit-measurement-indicator");
//        unitMeasurementLabel.setMaxWidth(Double.MAX_VALUE);
//        HBox.setHgrow(unitMeasurementLabel, Priority.ALWAYS);

        quantityInputContainer = new HBox(quantityTextField);
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



        Label titleLabel = new Label("Realizari");
        titleLabel.getStyleClass().add("records-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        TableView<Record> tableView = createRecordTableView();
        tableView.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox records = new VBox(titleLabel, tableView);
        records.setSpacing(8);
        HBox.setHgrow(records, Priority.ALWAYS);

//        TabPane root = new TabPane(createOrdersTab(), createRecordsTab());
        root = new HBox();
        root.getChildren().addAll(leftSection, records);
        root.setSpacing(8);
        root.getStyleClass().add("production-window");
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
            String text = "!!! Selectati produsul !!!";
            if(model.getSelectedProduct() != null) {
                text = model.getSelectedProduct().getName();
            }
            return text;
        }, model.selectedProductProperty()));

        selectedProductNameLabel.setWrapText(true);
        selectedProductNameLabel.setMaxWidth(Double.MAX_VALUE);
        selectedProductNameLabel.setAlignment(Pos.CENTER);

        FontIcon downArrowIcon = new FontIcon("mdi2a-arrow-down-drop-circle");
        FontIcon upArrowIcon = new FontIcon("mdi2a-arrow-up-drop-circle");
        upArrowIcon.setVisible(false);
        StackPane iconPane = new StackPane(downArrowIcon, upArrowIcon);
        StackPane.setAlignment(selectedProductNameLabel, Pos.CENTER);
        StackPane.setAlignment(downArrowIcon, Pos.CENTER_RIGHT);
        StackPane.setAlignment(upArrowIcon, Pos.CENTER_RIGHT);
        StackPane productNameStackPane = new StackPane(downArrowIcon, upArrowIcon, selectedProductNameLabel);
//        HBox.setHgrow(selectedProductNameLabel, Priority.ALWAYS);
//        HBox productNameSection = new HBox(selectedProductNameLabel, iconPane);
//        productNameSection.setSpacing(8);
//        productNameSection.setStyle("-fx-background-color: COLOR_BRAND_BROWN;" +
//                "-fx-padding: 1em;");
        productNameStackPane.setStyle("-fx-background-color: COLOR_BRAND_BROWN;" +
                "-fx-padding: 1em;");
//        VBox.setVgrow(productNameStackPane, Priority.ALWAYS);

        orderLabel = new Label();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm ");
        orderLabel.textProperty().bind(Bindings.createStringBinding(() ->
            {
                if(model.getAssociatedOrder() == null) {
                    orderLabel.getStyleClass().add("warning");
                    return "!!! Asociati comanda !!!";
                }
                orderLabel.getStyleClass().remove("warning");
                Order order = model.getAssociatedOrder();
                if(order.getRemainder() > 0) {
                    return "Necesar: " + order.getRemainder() +
                            "\nComanda: " + order.getCounter() + " din " + dateTimeFormatter.format(order.getDateTimeInserted());
                } else {
                    return "Comanda completa" +
                            "\nComanda: " + order.getCounter() + " din " + dateTimeFormatter.format(order.getDateTimeInserted());
                }
            },
            model.associatedOrderProperty()
        ));
        orderLabel.wrapTextProperty().set(true);
        orderLabel.setMinHeight(100);

        orderLabel.getStyleClass().add("select-product-button-order-info");
        VBox.setVgrow(orderLabel, Priority.ALWAYS);
        infoContainer.getChildren().addAll(productNameStackPane, orderLabel);
        infoContainer.setAlignment(Pos.TOP_CENTER);

        arrowIcon = new Label("▼");
        arrowIcon.setAlignment(Pos.CENTER_RIGHT);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(infoContainer);
        StackPane.setAlignment(arrowIcon, Pos.CENTER_RIGHT);

        Button btnProductChoice = new Button();
        btnProductChoice.setGraphic(stackPane);
        btnProductChoice.getStyleClass().add("production-product-selection-button");
        btnProductChoice.prefWidthProperty().bind(stage.widthProperty().divide(3));
        btnProductChoice.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnProductChoice.setOnAction(event -> {
            leftSection.setDisable(true);
            productSelectionActionHandler.accept(() -> {
                if (leftSection.getChildren().contains(productsListView)) {
                    leftSection.setDisable(false);
                    leftSection.getChildren().addAll(quantityInputContainer, numpad);
                    leftSection.getChildren().remove(productsListView);
                    upArrowIcon.setVisible(false);
                    downArrowIcon.setVisible(true);
//                    arrowIcon.setText("▼");
                } else {
                    leftSection.setDisable(false);
                    leftSection.getChildren().removeAll(quantityInputContainer, numpad);
                    leftSection.getChildren().add(productsListView);
//                    arrowIcon.setText("▲");
                    upArrowIcon.setVisible(true);
                    downArrowIcon.setVisible(false);
                }

            });
        });
        return btnProductChoice;
    }

    private TextField createQuantityField() {
        TextField quantityTextField = new TextField();
        quantityTextField.promptTextProperty().set("0.00");

        quantityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.isEmpty()) {
                    if(model.getSelectedProduct() == null) {
                        new WarningController(stage, "Selectati produsul pentru care doriti sa adaugati inregistrarea!");
                        quantityTextField.clear();
                        return;
                    }
                    if(model.getAssociatedOrder() == null) {
                        new WarningController(stage, "Selectati o comanda pentru a introduce inregistrari!");
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
        numDel.setGraphic(new FontIcon("mdi2b-backspace"));
        numDel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
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
                    new WarningController(stage, "Selectati produsul pentru care doriti sa adaugati inregistrarea!");
                    return;
                }
                if(model.getAssociatedOrder() == null) {
                    new WarningController(stage, "Selectati o comanda pentru a introduce inregistrari!");
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
                        new WarningController(stage, "Introduceti cantitatea pentru produsul:\n" +
                                model.getSelectedProduct().getName());
                        return;
                    }
                    double quantity = Double.parseDouble(quantityTextField.getText());
                    if(quantity <= 0) {
                        new WarningController(stage, "Cantitatea trebuie sa fie mai mare de 0!");
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

    private TableView<Record> createRecordTableView() {
        TableView<Record> tableView = new TableView<>();

        tableView.setPlaceholder(new Label("Nu exista realizari."));

        TableColumn<Record, Timestamp> dateAndTimeColumn = new TableColumn<>("Data si ora");
        dateAndTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateTimeInserted()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateAndTimeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                final Label timeLabel = new Label();
                final Label dateLabel = new Label();
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    timeLabel.setText(timeFormat.format(item));
                    dateLabel.setText(dateFormat.format(item));
                    FlowPane flowPane = new FlowPane(timeLabel, dateLabel);
                    flowPane.setHgap(8);
                    flowPane.setVgap(4);
                    setGraphic(flowPane);
                }
            }
        });
        tableView.getColumns().add(dateAndTimeColumn);

        TableColumn<Record, String> nameColumn = new TableColumn<>("Produs");
        nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getName()));
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

        TableColumn<Record, Double> quantityColumn = new TableColumn<>("Cantitate");
        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        quantityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                    setStyle("-fx-alignment: TOP-RIGHT;");
                }
            }
        });
        tableView.getColumns().add(quantityColumn);

        if(ConfigApp.getRole().canEditRecords()) {
            TableColumn<Record, Integer> editBtnColumn = new TableColumn<>();
            editBtnColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
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
                        editButton.getStyleClass().add("filled-button");
                        setStyle("-fx-alignment: TOP-RIGHT;");

                        editButton.setOnAction(event -> new RecordController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                        setGraphic(editButton);
                    }
                }
            });
            tableView.getColumns().add(editBtnColumn);
            editBtnColumn.setPrefWidth(64);
        }

        dateAndTimeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        if(ConfigApp.getRole().canEditRecords()) {
            nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5).subtract(64));
        } else {
            nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
        }
        quantityColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));

//        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        tableView.getStyleClass().add("main-table-view");
        tableView.setItems(model.getRecords());

        return tableView;
    }

    private ListView<ProductionProductDTO> createProductListView() {
        ListView<ProductionProductDTO> listView = new ListView<>();
        listView.setCellFactory(new Callback<ListView<ProductionProductDTO>, ListCell<ProductionProductDTO>>() {
            @Override
            public ListCell<ProductionProductDTO> call(ListView<ProductionProductDTO> param) {
                return new ListCell<ProductionProductDTO>() {
                    {
                        setPrefWidth(0);
                    }
                    @Override
                    protected void updateItem(ProductionProductDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            Label productLabel = new Label(item.getProduct().getName());
                            productLabel.setMaxWidth(Double.MAX_VALUE);
                            productLabel.setWrapText(true);
                            HBox.setHgrow(productLabel, Priority.ALWAYS);

                            HBox container = new HBox(productLabel);

                            if(item.getOrderCount() > 0) {
                                Label orderCountLabel = new Label(String.valueOf(item.getOrderCount()));
                                orderCountLabel.setStyle("-fx-background-radius: 50px; " +
                                        "-fx-border-radius: 50px; " +
                                        "-fx-background-color: derive(COLOR_BRAND_BROWN, 90%); " +
                                        "-fx-text-fill: COLOR_WHITE; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-padding: 0.3em; " +
                                        "-fx-min-width: 40px; " +
                                        "-fx-text-alignment: CENTER; " +
                                        "-fx-alignment: CENTER; ");
                                container.getChildren().add(orderCountLabel);
                            }

                            setWrapText(true);
                            setPadding(new Insets(20));
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
                ProductionProductDTO productionProductDTO = listView.getSelectionModel().getSelectedItem();
                handleListViewItemSelected(productionProductDTO.getProduct());
                arrowIcon.setText("▼");
            }
        });
        //Setting up double tap for elements in listview
        listView.setOnTouchPressed(event -> {
            if (event.getTouchCount() == 2) {
                //Use ListView's getSelected Item
                ProductionProductDTO productionProductDTO = listView.getSelectionModel().getSelectedItem();
                handleListViewItemSelected(productionProductDTO.getProduct());
                arrowIcon.setText("▼");
            }
        });
        listView.setItems(model.getProducts());
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }

    private void handleListViewItemSelected(Product product) {
        searchOrderForProductHandler.accept(product);
    }

    public void handleOrderSearchForProduct(Product product, boolean isFound) {
        OrderAssociationController orderAssociationController = new OrderAssociationController(stage, product, false);
        if(orderAssociationController.isSUCCESS()) {
            leftSection.getChildren().addAll(quantityInputContainer, numpad);
            leftSection.getChildren().remove(productsListView);
            setSelectedProductHandler.accept(product, orderAssociationController.getOrder());
        }
    }

}
