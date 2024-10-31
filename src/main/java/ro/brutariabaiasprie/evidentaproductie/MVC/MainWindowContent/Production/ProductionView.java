package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Production;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.Hashtable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductionView extends Parent implements Builder<Region> {
    private final Consumer<Runnable> productSelectionActionHandler;
    private final BiConsumer<Runnable, Dictionary<String, Object>> productRecordAddActionHandler;
    private ProductionModel model;
    private Stage stage;
    private User user;

//    private final BorderPane root = new BorderPane();
    private HBox root;
    private Label lblSelectedProductName;
    private TextField txtFldQuantity;
    private GridPane numpad;
    private VBox leftSection;
    private ListView<ProductDTO> lstViewProducts;
    private Label arrowIcon;

    private ProductDTO selectedProduct;


    public ProductionView(ProductionModel model, Stage stage, Consumer<Runnable> productSelectionActionHandler, BiConsumer<Runnable, Dictionary<String, Object>> productRecordAddActionHandler) {
        this.productSelectionActionHandler = productSelectionActionHandler;
        this.productRecordAddActionHandler = productRecordAddActionHandler;
        this.model = model;
        this.stage = stage;
        this.user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
    }

    @Override
    public Region build() {
        Button btnProductChoice = createBtnProductChoice();
        txtFldQuantity = createQuantityField();

        numpad = createNumpad();
        leftSection = new VBox(btnProductChoice, txtFldQuantity, numpad);

        btnProductChoice.prefHeightProperty().bind(leftSection.heightProperty().divide(6));
        txtFldQuantity.prefHeightProperty().bind(leftSection.heightProperty().divide(6));


        VBox.setVgrow(numpad, Priority.ALWAYS);
        lstViewProducts = createProductListView();
        lstViewProducts.maxWidthProperty().bind(stage.widthProperty().divide(3));
        TableView<ProductRecordDTO> tableView = createProductRecordTableView();
        tableView.setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        HBox.setHgrow(tableView, Priority.ALWAYS);

        root = new HBox();
        root.getChildren().addAll(leftSection, tableView);
        return root;
    }

    public Region getRoot() {
        return root;
    }

    private Button createBtnProductChoice() {
        lblSelectedProductName = new Label("Nici un produs selectat");
        lblSelectedProductName.getStyleClass().add("lbl-sel-prod");
        lblSelectedProductName.setWrapText(true);
        lblSelectedProductName.prefWidthProperty().bind(stage.widthProperty().divide(3.5));

        arrowIcon = new Label("▼");
        arrowIcon.setAlignment(Pos.CENTER_RIGHT);


        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(lblSelectedProductName, arrowIcon);
        StackPane.setAlignment(arrowIcon, Pos.CENTER_RIGHT);

        Button btnProductChoice = new Button();
        btnProductChoice.setGraphic(stackPane);
        btnProductChoice.getStyleClass().add("btn-sel-prod");
        btnProductChoice.prefWidthProperty().bind(stage.widthProperty().divide(3));
        btnProductChoice.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnProductChoice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                leftSection.setDisable(true);
                productSelectionActionHandler.accept(() -> {
                    if(leftSection.getChildren().contains(lstViewProducts)) {
                        leftSection.setDisable(false);
                        leftSection.getChildren().addAll(txtFldQuantity, numpad);
                        leftSection.getChildren().remove(lstViewProducts);
                        arrowIcon.setText("▼");
                    } else {
                        leftSection.setDisable(false);
                        leftSection.getChildren().removeAll(txtFldQuantity, numpad);
                        leftSection.getChildren().add(lstViewProducts);
                        arrowIcon.setText("▲");
                    }


                });
            }
        });
        return btnProductChoice;
    }

    private TextField createQuantityField() {
        TextField txtFldQuantity = new TextField();
        txtFldQuantity.setPromptText("-.--");
        txtFldQuantity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (newValue != null && !newValue.isEmpty()) {
                    if(selectedProduct == null) {
                        WarningController warningController = new WarningController(stage, "Selectati produsul pentru care doriti sa adaugati inregistrarea!");
                        txtFldQuantity.clear();
                        return;
                    }

                    String filteredValue = newValue.replaceAll(" ", "");
                    Pattern pattern = Pattern.compile("(\\d{1,10}\\.\\d{1,2}|\\d{1,10}\\.|\\.\\d{1,2}|\\d{1,10})");
                    Matcher matcher = pattern.matcher(filteredValue);
                    if (matcher.find())
                    {
                        txtFldQuantity.setText(matcher.group(0));
                    } else {
                        txtFldQuantity.clear();
                    }
                } else {
                    txtFldQuantity.clear();
                }
            }
        });
        txtFldQuantity.getStyleClass().add("txt-fld-quantity");
        txtFldQuantity.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return txtFldQuantity;
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
                if(selectedProduct == null) {
                    WarningController warningController = new WarningController(stage, "Selectati produsul pentru care doriti sa adaugati inregistrarea!");
                    return;
                }
                //TODO - WARNING FOR NO ORDER
                if("0123456789.".contains(value)) {
                    String quantity = txtFldQuantity.getText();
                    txtFldQuantity.setText(quantity + value);
                } else if ("⌫".equals(value)) {
                    String quantity = txtFldQuantity.getText();
                    if (quantity.isEmpty()){
                        return;
                    }
                    txtFldQuantity.setText(quantity.substring(0, quantity.length() - 1));
                } else if ("Adauga +".equals(value)) {
                    //Handle warning for no quantity entered
                    if(txtFldQuantity.getText().isEmpty() || txtFldQuantity.getText() == null) {
                        WarningController warningController = new WarningController(stage, "Introduceti cantitatea pentru produsul:\n" + selectedProduct.getName());
                        return;
                    }
                    double quantity = Double.parseDouble(txtFldQuantity.getText());
                    if(quantity <= 0) {
                        WarningController warningController = new WarningController(stage, "Cantitatea trebuie sa fie mai mare de 0!");
                        return;
                    }
                    //Ask for confirmation
                    ConfirmationController confirmationController = new ConfirmationController(stage, "Confirmati introducere inregistrare",
                            "Doriti sa introduceti " + quantity + " " + selectedProduct.getUnitMeasurement() + " pentru produsul\n" + selectedProduct.getName());
                    if(!confirmationController.isSUCCESS()){
                        return;
                    }
                    //Add the product record
                    Dictionary<String, Object> data = new Hashtable<>();
                    data.put("product", selectedProduct);
                    data.put("quantity", quantity);
                    leftSection.setDisable(true);
                    productRecordAddActionHandler.accept(() -> {
                        leftSection.setDisable(false);
                        txtFldQuantity.textProperty().set("");
                    }, data);
                }
            }
        };
    }

    private EventHandler<ActionEvent> handleBtnEditRecordOnAction(ProductRecordDTO productRecordDTO) {
        return new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(productRecordDTO);
            }
        };
    }

    private ListView<ProductRecordDTO> createProductRecordListView() {
        ListView<ProductRecordDTO> listView = new ListView<>();

        listView.setCellFactory(new Callback<ListView<ProductRecordDTO>, ListCell<ProductRecordDTO>>() {
            @Override
            public ListCell<ProductRecordDTO> call(ListView<ProductRecordDTO> param) {
                return new ListCell<ProductRecordDTO>() {

                    @Override
                    protected void updateItem(ProductRecordDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Button btnEdit = new Button("✎");
                            btnEdit.setOnAction(handleBtnEditRecordOnAction(item));

                            Label lblProductName = new Label(item.getName());
                            lblProductName.getStyleClass().add("den-prod-record-list-cell");
                            Label lblProductDetails = new Label(" Cantitate : " + String.format("%.2f", item.getQuantity()) + " " + item.getUnitMeasurement());
                            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            Label lblDateTime = new Label(" Data si ora: " + dateTimeFormatter.format(item.getDateAndTimeInserted()));
                            HBox productCell = new HBox();
                            productCell.getChildren().addAll(lblProductName, lblProductDetails, lblDateTime, btnEdit);

                            productCell.setSpacing(10);
                            productCell.setPadding(new Insets(10));
                            setText(null);
                            setGraphic(productCell);
                        }
                    }
                };
            }
        });
        listView.setItems(model.getProductRecords());
        return listView;
    }

    private TableView<ProductRecordDTO> createProductRecordTableView() {
        TableView<ProductRecordDTO> tableView = new TableView<>();

        tableView.setPlaceholder(new Label("Nu exista inregistrari in baza de date."));

        TableColumn<ProductRecordDTO, String> nameColumn = new TableColumn<>("Produs");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
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
        dateAndTimeColumn.setCellFactory(column -> new TableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
                        Button btnEdit = new Button("\uD83D\uDD27");
                        btnEdit.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

                        btnEdit.setOnAction(event -> {
                            WarningController warningController = new WarningController(stage, "Doresti sa editezi inregistrarea: " + item);
                        });
                        setGraphic(btnEdit);
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
                            lblProductName.maxWidthProperty().bind(widthProperty().subtract(20));
                            Label lblProductDetails = new Label(item.getUnitMeasurement());
                            VBox container = new VBox();
                            container.getChildren().addAll(lblProductName, lblProductDetails);
                            container.setSpacing(10);
                            container.setPadding(new Insets(10));
                            setText(null);
                            setGraphic(container);
                            prefWidthProperty().bind(listView.widthProperty().subtract(20));
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
        //TODO - WARNING FOR PRODUCT WITH NOT ORDER
        selectedProduct = product;
        lblSelectedProductName.setText(product.getName());
        leftSection.getChildren().addAll(txtFldQuantity, numpad);
        leftSection.getChildren().remove(lstViewProducts);
        txtFldQuantity.setPromptText("0.00 " + product.getUnitMeasurement());
    }
}
