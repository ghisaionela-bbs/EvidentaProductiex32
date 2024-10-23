package ro.brutariabaiasprie.evidentaproductie.MVC.Production;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.MVC.Widgets.WarningView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductionView extends Parent implements Builder<Region> {
    private final Consumer<Runnable> productSelectionActionHandler;
    private final BiConsumer<Runnable, Dictionary<String, Object>> productRecordAddActionHandler;
    private ProductionModel model;
    private Stage stage;

    private final BorderPane root = new BorderPane();
    private Label lblSelectedProductName;
    private TextField txtFldQuantity;
    private GridPane numpad;
    private VBox leftSection;
    private ListView<ProductDTO> lstViewProducts;

    private ProductDTO selectedProduct;


    public ProductionView(ProductionModel model, Stage stage, Consumer<Runnable> productSelectionActionHandler, BiConsumer<Runnable, Dictionary<String, Object>> productRecordAddActionHandler) {
        this.productSelectionActionHandler = productSelectionActionHandler;
        this.productRecordAddActionHandler = productRecordAddActionHandler;
        this.model = model;
        this.stage = stage;
    }

    private Button createBtnProductChoice() {
        HBox contentOfBtnProductChoice = new HBox();
        VBox textContent = new VBox();

        lblSelectedProductName = new Label("Nici un produs selectat");
        lblSelectedProductName.getStyleClass().add("lbl-sel-prod");
        lblSelectedProductName.setWrapText(true);
        lblSelectedProductName.prefWidthProperty().bind(stage.widthProperty().divide(3.5));
        textContent.getChildren().add(lblSelectedProductName);

        Label lblSelectAnotherProduct = new Label("Selecteaza un alt produs >>");
        lblSelectAnotherProduct.getStyleClass().add("lbl-sel-prod-sub");
        textContent.getChildren().add(lblSelectAnotherProduct);
        contentOfBtnProductChoice.getChildren().add(textContent);
        contentOfBtnProductChoice.fillHeightProperty().set(false);

        Label lblArrowIcon = new Label("▼");
        lblArrowIcon.getStyleClass().add("arrow-icon-sel-prod");
        contentOfBtnProductChoice.getChildren().add(lblArrowIcon);
        contentOfBtnProductChoice.setFillHeight(false);
        contentOfBtnProductChoice.setPrefHeight(0);

        Button btnProductChoice = new Button();
        btnProductChoice.setGraphic(contentOfBtnProductChoice);
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
                    } else {
                        leftSection.setDisable(false);
                        leftSection.getChildren().removeAll(txtFldQuantity, numpad);
                        leftSection.getChildren().add(lstViewProducts);
                    }


                });
            }
        });
        return btnProductChoice;
    }

    private TextField createQuantityField() {
        TextField txtFldQuantity = new TextField();
        txtFldQuantity.setPromptText("0.00");
        txtFldQuantity.setAlignment(Pos.CENTER_RIGHT);
        txtFldQuantity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (newValue != null && !newValue.isEmpty()) {
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

        numpad.getStyleClass().add("numpad");
        return numpad;
    }

    private EventHandler<ActionEvent> handleBtnNumpadOnAction() {
        return new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                Button node = (Button) event.getSource() ;
                String value = node.getText();
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
                    //Handle wargning screen for no product selected
                    if(selectedProduct == null) {
                        Stage warningStage = new Stage();
                        WarningView warningView = new WarningView("Selectati produsul pentru care doriti sa adaugati inregistrarea!",
                                new Consumer<Runnable>() {
                                    @Override
                                    public void accept(Runnable runnable) {
                                        warningStage.close();
                                    }
                                });
                        Scene warningScene = new Scene(warningView.build());
                        warningScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

                        warningStage.setTitle("Atentie!");
                        warningStage.setScene(warningScene);
                        warningStage.initOwner(stage);
                        warningStage.initModality(Modality.APPLICATION_MODAL);
                        warningStage.showAndWait();



                    }
                    //Handle warning for no quantity entered
                    if(!(txtFldQuantity.getText().isEmpty() || txtFldQuantity.getText() == null)
                            && selectedProduct != null) {
                        Dictionary<String, Object> data = new Hashtable<>();
                        data.put("product", selectedProduct);
                        data.put("quantity", Double.valueOf(txtFldQuantity.getText()));

                        leftSection.setDisable(true);
                        productRecordAddActionHandler.accept(() -> {
                            leftSection.setDisable(false);
                        }, data);
                    }
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
                            Label lblDateTime = new Label(" Data si ora: " + dateTimeFormatter.format(item.getDateAndTime()));
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
                            lblProductName.maxWidthProperty().bind(stage.widthProperty().divide(3.5));
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
            }
        });
        //Setting up double tap for elements in listview
        listView.setOnTouchPressed(event -> {
            if (event.getTouchCount() == 2) {
                //Use ListView's getSelected Item
                ProductDTO productDTO = listView.getSelectionModel().getSelectedItem();
                handleListViewItemSelected(productDTO);
            }
        });
        listView.setItems(model.getProducts());
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }

    private void handleListViewItemSelected(ProductDTO product) {
        selectedProduct = product;
        lblSelectedProductName.setText(product.getName());
        leftSection.getChildren().addAll(txtFldQuantity, numpad);
        leftSection.getChildren().remove(lstViewProducts);
    }

    @Override
    public Region build() {
        Button btnProductChoice = createBtnProductChoice();
        txtFldQuantity = createQuantityField();
        txtFldQuantity.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad = createNumpad();
        leftSection = new VBox(btnProductChoice, txtFldQuantity, numpad);
//        leftSection.prefWidthProperty().bind(window.widthProperty().multiply(0.25));
        VBox.setVgrow(numpad, Priority.ALWAYS);
        ListView<ProductRecordDTO> listView = createProductRecordListView();
        listView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        leftSection.setMaxWidth(Region.USE_COMPUTED_SIZE);
        leftSection.prefWidthProperty().bind(stage.widthProperty().divide(3));
        leftSection.prefHeightProperty().bind(stage.widthProperty());
        listView.maxWidthProperty().bind(stage.widthProperty().divide(3/2));

        lstViewProducts = createProductListView();
        lstViewProducts.maxWidthProperty().bind(stage.widthProperty().divide(3));

        root.setLeft(leftSection);
        root.setCenter(listView);
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return root;
    }
}
