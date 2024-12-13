package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Manager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import ro.brutariabaiasprie.evidentaproductie.Data.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.ColoredProgressBar;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport.ExcelExportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport.ExcelImportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order.OrderController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport.OrderExportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderImport.OrderImportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product.ProductController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductGroup.ProductGroupController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Record.RecordController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User.UserController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class ManagerView extends Parent implements Builder<Region> {
    private final Stage stage;
    private final ManagerModel model;
    private final Consumer<Order> productionShortcutHandler;
    private final Consumer<Boolean> reloadOrders;
    //products tab
    private Button addProductButton;
    private Button importProductsButton;
    //orders tab
    private Button addOrderButton;
    private Button importOrderButton;
    private Button orderExportButton;
    private Button excelExportButton;
    private CheckBox closedOrdersCheckbox = new CheckBox("Afiseaza comenzi inchise");

    public ManagerView(ManagerModel model, Stage stage, Consumer<Order> productionShortcutHandler, Consumer<Boolean> reloadOrders) {
        this.model = model;
        this.stage = stage;
        this.productionShortcutHandler = productionShortcutHandler;
        this.reloadOrders = reloadOrders;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setFillWidth(true);
        root.getChildren().add(createTabs());

        createStageResizeListeners();
        return root;
    }

    private void createStageResizeListeners() {
        if(model.getCONNECTED_USER().getRoleId() == 1 || model.getCONNECTED_USER().getRoleId() == 2) {
            ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
                if(stage.getWidth() < Globals.MINIMIZE_WIDTH) {
                    addProductButton.setText("➕");
                    importProductsButton.setText("\uD83D\uDCE5");
                    addOrderButton.setText("➕");
                    excelExportButton.setText("");
                } else {
                    addProductButton.setText("➕ Adauga un produs");
                    importProductsButton.setText("Importa produse \uD83D\uDCE5");
                    addOrderButton.setText("➕ Adauga o comanda");
                    excelExportButton.setText("Exporta realizari in excel");
                }
            };
            stage.widthProperty().addListener(stageSizeListener);
            stage.heightProperty().addListener(stageSizeListener);
        }

    }

    private Node createTabs() {
        TabPane tabPane =  new TabPane();

        if(ConfigApp.getRole().canViewOrders()) {
            tabPane.getTabs().add(createOrdersTab());
        }
        if(ConfigApp.getRole().canViewGroups()) {
            tabPane.getTabs().add(createGroupsTab2());
        }
        if (ConfigApp.getRole().canViewProducts()) {
            tabPane.getTabs().add(createProductsTab());
        }
        if(ConfigApp.getRole().canViewUsers()) {
            tabPane.getTabs().add(createUsersTab());
        }

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return tabPane;
    }

    //region Products Tab
    private Tab createProductsTab() {
        Tab productsTab = new Tab("Produse");
        VBox contentContainer = new VBox();

        contentContainer.getChildren().addAll(createProductsSectionHeader(), createProductsTable());
        productsTab.setContent(contentContainer);
        productsTab.setClosable(false);
        return productsTab;
    }

    private Node createProductsSectionHeader() {
        HBox sectionHeaderContainer = new HBox();
        sectionHeaderContainer.setSpacing(10);
        sectionHeaderContainer.setAlignment(Pos.CENTER);

        Label productsSectionTitle = new Label("Produse");
        productsSectionTitle.getStyleClass().add("tab-section-title");
        productsSectionTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(productsSectionTitle, Priority.ALWAYS);

        if(ConfigApp.getRole().canEditProducts()) {
            addProductButton = new Button();
            addProductButton.setOnAction(event -> new ProductController(stage));

            importProductsButton = new Button();
            importProductsButton.setOnAction(event -> new ExcelImportController(stage));
            importProductsButton.setTooltip(new Tooltip("Importa produse dintr-un fisier excel."));
            if(stage.getWidth() < Globals.MINIMIZE_WIDTH) {
                addProductButton.setText("➕");
                importProductsButton.setText("\uD83D\uDCE5");
            } else {
                addProductButton.setText("➕ Adauga un produs");
                importProductsButton.setText("Importa produse \uD83D\uDCE5");
            }
            addProductButton.getStyleClass().add("ghost-button");
            importProductsButton.getStyleClass().add("ghost-button");

        }

        sectionHeaderContainer.getChildren().addAll(productsSectionTitle, addProductButton, importProductsButton);
        sectionHeaderContainer.getStyleClass().add("tab-section-header");
        return sectionHeaderContainer;
    }

    private TableView<Product> createProductsTable() {
        TableView<Product> productsTableView = new TableView<>();

        TableColumn<Product, String> productNameColumn = new TableColumn<>("Denumire");
        productNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        productNameColumn.setCellFactory(column -> new TableCell<>() {
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
        productsTableView.getColumns().add(productNameColumn);

        TableColumn<Product, String> groupColumn = new TableColumn<>("Grupa");
        groupColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGroup() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(cellData.getValue().getGroup().getName());
        });
        productsTableView.getColumns().add(groupColumn);

        if(ConfigApp.getRole().canEditProducts()) {
            TableColumn<Product, Integer> editBtnColumn = new TableColumn<>();
            editBtnColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getId()));
            editBtnColumn.setCellFactory(column -> new TableCell<>() {
                final Button editButton = new Button();
                final FontIcon fontIcon = new FontIcon("mdi2s-square-edit-outline");

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);

                        editButton.setGraphic(fontIcon);
                        editButton.getStyleClass().add("filled-button");
                        editButton.setOnAction(event -> {
                            productsTableView.getSelectionModel().select(getIndex());
                            new ProductController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem());
                        });
                        setGraphic(editButton);
                        setStyle("-fx-alignment:  TOP-RIGHT;");
                    }
                }
            });
            productsTableView.getColumns().add(editBtnColumn);
        }


        productsTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        productsTableView.setPlaceholder(new Label("Nu exista produse."));
        VBox.setVgrow(productsTableView, Priority.ALWAYS);
        productsTableView.setItems(model.getProducts());
        productsTableView.getStyleClass().add("main-table-view");
        return productsTableView;
    }
    //endregion

    //region Orders Tab
    private Tab createOrdersTab() {
        Tab ordersTab = new Tab("Comenzi");
        ordersTab.setClosable(false);

        VBox contentContainer = new VBox();
        
        contentContainer.getChildren().addAll(createOrdersSectionHeader(), createOrdersTable());
        ordersTab.setContent(contentContainer);

        return ordersTab;
    }

    private Node createOrdersSectionHeader() {
        HBox sectionHeaderContainer = new HBox();
        sectionHeaderContainer.setSpacing(10);
        sectionHeaderContainer.setAlignment(Pos.CENTER);

        Label ordersSectionTitle = new Label("Comenzi");
        ordersSectionTitle.getStyleClass().add("tab-section-title");
        ordersSectionTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(ordersSectionTitle, Priority.ALWAYS);

        sectionHeaderContainer.getChildren().addAll(ordersSectionTitle);

//        addProductButton = new Button();
//        addProductButton.setOnAction(event -> new ProductController(stage));
//
//        importProductsButton = new Button();
//        importProductsButton.setOnAction(event -> new ExcelImportController(stage));
//        importProductsButton.setTooltip(new Tooltip("Importa produse dintr-un fisier excel."));
//        if(stage.getWidth() < Globals.MINIMIZE_WIDTH) {
//            addProductButton.setText("➕");
//            importProductsButton.setText("\uD83D\uDCE5");
//        } else {
//            addProductButton.setText("➕ Adauga un produs");
//            importProductsButton.setText("Importa produse \uD83D\uDCE5");
//        }
//        addProductButton.getStyleClass().add("ghost-button");
//        importProductsButton.getStyleClass().add("ghost-button");

        if(ConfigApp.getRole().canEditOrders()) {
            closedOrdersCheckbox.setSelected(true);
            closedOrdersCheckbox.setOnAction(event -> reloadOrders.accept(closedOrdersCheckbox.isSelected()));

            addOrderButton = new Button();
            addOrderButton.setOnAction(event -> {
                new OrderController(stage, WINDOW_TYPE.ADD);
            });
            orderExportButton = new Button("Exporta");
            FontIcon fontIcon = new FontIcon("mdi2f-file-export-outline");
            orderExportButton.setGraphic(fontIcon);
            orderExportButton.getStyleClass().add("ghost-button");
            orderExportButton.setOnAction(event -> new OrderExportController(stage));

            importOrderButton = new Button("Importa comenzi");
            importOrderButton.setOnAction(event -> new OrderImportController(stage));
            importOrderButton.setGraphic(new FontIcon("mdi2a-application-import"));
            importOrderButton.getStyleClass().add("ghost-button");

            excelExportButton = new Button();
            excelExportButton.setOnAction(event -> new ExcelExportController(stage));


            if(stage.getWidth() < Globals.MINIMIZE_WIDTH) {
                addOrderButton.setText("➕");
            } else {
                addOrderButton.setText("➕ Adauga o comanda");
                excelExportButton.setGraphic(new FontIcon("mdi2m-microsoft-excel"));
            }
            addOrderButton.getStyleClass().add("ghost-button");

            sectionHeaderContainer.getChildren().addAll(closedOrdersCheckbox, addOrderButton, importOrderButton, orderExportButton);
        }

        sectionHeaderContainer.getStyleClass().add("tab-section-header");
        return sectionHeaderContainer;
    }

    private TableView<Order> createOrdersTable() {
        TableView<Order> ordersTableView = new TableView<>();
        ordersTableView.setPlaceholder(new Label("Nu exista comenzi."));
        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

        TableColumn<Order, Boolean> isClosedColumn = new TableColumn<>();
        isClosedColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().isClosed()));
        isClosedColumn.setCellFactory(column -> new TableCell<>() {
            final FontIcon fontIcon = new FontIcon("mdi2l-lock");
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    if(item) {
                        setGraphic(fontIcon);
                        setStyle("-fx-alignment: CENTER;");
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        ordersTableView.getColumns().add(isClosedColumn);

        TableColumn<Order, Integer> orderCounterColumn = new TableColumn<>("Nr");
        orderCounterColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCounter()));
        orderCounterColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        ordersTableView.getColumns().add(orderCounterColumn);


        TableColumn<Order, Timestamp> dateTimeColumn = new TableColumn<>("Programata la");
        dateTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateScheduled()));
        ordersTableView.getColumns().add(dateTimeColumn);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateTimeColumn.setCellFactory(column -> new TableCell<>() {
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

        TableColumn<Order, String> productNameColumn = new TableColumn<>("Produs");
        productNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getName()));
        productNameColumn.setCellFactory(new Callback<TableColumn<Order, String>, TableCell<Order, String>>() {
            @Override
            public TableCell<Order, String> call(TableColumn<Order, String> param) {
                return new TableCell<>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        final Label batchNumber = new Label();
                        final Label percentageLabel = new Label();
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            setStyle(null);
                        } else {
                            Text txtName = new Text(item);
                            txtName.getStyleClass().add("text");
                            txtName.wrappingWidthProperty().bind(widthProperty());

                            int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
                            Order order = param.getTableView().getItems().get(currentIndex);

                            // When the value for a batch is defined
                            if(order.getProduct().getBatchValue() > 0) {
                                double quantity = order.getQuantity();
                                double completed = order.getCompleted();
                                double batchValue = order.getProduct().getBatchValue();
                                double percentage = 0.0;
                                double completedBatches = Math.ceil(completed / batchValue);
                                double totalBatches = Math.ceil(quantity / batchValue);

                                // When the order is incomplete
                                if(completed < quantity) {
                                    // When there are no completed batches
                                    if(completedBatches == 0) {
                                        percentage = completed / batchValue;
                                    }
                                    // When there are completed batches
                                    else {
                                        percentage = (completed - (completedBatches - 1) * batchValue ) / batchValue;
                                        // If the current batch is completed and it is not the last batch move to the next one
                                        if (percentage == 1 && completedBatches < totalBatches) {
                                            completedBatches += 1;
                                            percentage = 0;
                                        }
                                    }
                                } else {
                                    percentage = 1 + (completed - quantity) / batchValue;
                                }

                                batchNumber.setText("Sarja: " + (int)completedBatches + "/" + (int)totalBatches);
                                ColoredProgressBar progressBar = new ColoredProgressBar(percentage);
                                percentageLabel.setText(percentage * 100.0 + "%");
                                HBox progressContainer = new HBox(batchNumber, progressBar, percentageLabel);
                                VBox container = new VBox(txtName, progressContainer);
                                progressContainer.setSpacing(8);
                                setGraphic(container);
                            }

                            else {
                                setGraphic(txtName);
                                setText(null);
                                setStyle(null);
                            }
                        }
                    }
                };
            }
        });
        ordersTableView.getColumns().add(productNameColumn);

        TableColumn<Order, Double> quantityColumn = new TableColumn<>("Comandat");
        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        quantityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("%.2f", item));
                    setStyle("-fx-alignment: TOP-RIGHT;");
                }
            }
        });
        ordersTableView.getColumns().add(quantityColumn);

        TableColumn<Order, Double> completedColumn = new TableColumn<>("Realizat");
        completedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCompleted()));
        ordersTableView.getColumns().add(completedColumn);
        completedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("%.2f", item));
                    setStyle("-fx-alignment: TOP-RIGHT;");
                }
            }
        });

        TableColumn<Order, Double> remainderColumn = new TableColumn<>("Rest");
        remainderColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRemainder()));
        ordersTableView.getColumns().add(remainderColumn);
        if(ConfigApp.getRole().canEditOrders()) {
            remainderColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);
                        Text remainderText = new Text(String.format("%.2f", item));
                        final Order order = getTableRow().getItem();
                        if(order != null) {
                            setStyle("-fx-alignment: TOP-RIGHT;");
                            remainderText.getStyleClass().add("remainder");
                            if(order.getCompleted() > order.getQuantity()) {
                                remainderText.getStyleClass().add("exceeded");
                            } else if (order.getCompleted() == order.getQuantity()) {
                                remainderText.getStyleClass().add("exact");
                            } else {
                                remainderText.getStyleClass().add("text");
                            }
                        } else {
                            remainderText.getStyleClass().add("text");
                        }
                        setGraphic(remainderText);
                    }
                }
            });
        } else {
            remainderColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);
                        Text remainderText = new Text(String.format("%.2f", item));
                        remainderText.getStyleClass().add("text");
                        setGraphic(remainderText);
                    }
                }
            });
        }


        TableColumn<Order, Integer> actionBtnColumn = new TableColumn<>();
        actionBtnColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getId()));
        if(ConfigApp.getRole().canEditOrders()) {
            actionBtnColumn.setCellFactory(column -> new TableCell<>() {
                final Button actionButton = new Button();
                final FontIcon fontIcon = new FontIcon("mdi2s-square-edit-outline");
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);

                        actionButton.getStyleClass().add("filled-button");
                        actionButton.setGraphic(fontIcon);
                        actionButton.setOnAction(event -> new OrderController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                        setGraphic(actionButton);
                        setStyle("-fx-alignment: TOP-RIGHT;");
                    }
                }
            });
        } else {
            actionBtnColumn.setCellFactory(column -> new TableCell<>() {
                final Button actionButton = new Button();
                final FontIcon fontIcon = new FontIcon("mdi2a-arrow-right-drop-circle");
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);
                        if(getTableRow() == null) {
                            setGraphic(null);
                            return;
                        }
                        if(getTableRow().getItem() == null) {
                            setGraphic(null);
                            return;
                        }
                        if(getTableRow().getItem().isClosed()) {
                            setGraphic(null);
                            return;
                        }

                        actionButton.getStyleClass().add("filled-button");
                        actionButton.setGraphic(fontIcon);
                        actionButton.setOnAction(event -> productionShortcutHandler.accept(getTableRow().getItem()));
                        setGraphic(actionButton);
                        setStyle("-fx-alignment: TOP-RIGHT;");
                    }
                }
            });
        }
        ordersTableView.getColumns().add(actionBtnColumn);

        ordersTableView.setRowFactory(tv -> {
            TableRow<Order> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if(!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
//                    System.out.println(Arrays.toString(row.getStyleClass().toArray()));
//                    System.out.println("row");
//                } else {
//                    System.out.println("empty");
//                }
//            });
            return row;
        });

        isClosedColumn.prefWidthProperty().set(64);
        orderCounterColumn.prefWidthProperty().set(64);
        dateTimeColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        productNameColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.5).subtract(192));
        quantityColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        completedColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        remainderColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        actionBtnColumn.prefWidthProperty().set(64);
//        ordersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY );

        ordersTableView.setItems(model.getOrders());
        ordersTableView.getStyleClass().add("main-table-view");

        return ordersTableView;
    }

    private TableView<Record> createRecordsWithNoOrderTable() {
        TableView<Record> tableView = new TableView<>();

        tableView.setPlaceholder(new Label("Nu exista realizari fara comenzi asociate."));

        TableColumn<Record, Integer> idColumn = new TableColumn<>("Nr");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getId()));
        idColumn.setStyle("-fx-alignment: TOP-RIGHT;");
        tableView.getColumns().add(idColumn);

        TableColumn<Record, Timestamp> dateAndTimeColumn = new TableColumn<>("Data si ora");
        dateAndTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateTimeInserted()));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateAndTimeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(format.format(item));
                    setStyle("-fx-alignment: CENTER;");
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
                    setStyle("-fx-alignment:  TOP-RIGHT;");
                }
            }
        });
        tableView.getColumns().add(quantityColumn);

        TableColumn<Record, String> unitMeasurementColumn = new TableColumn<>("UM");
        unitMeasurementColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getUnitMeasurement()));
        unitMeasurementColumn.setStyle("-fx-alignment: TOP-RIGHT;");
        tableView.getColumns().add(unitMeasurementColumn);

        User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

        if(user.getRoleId() == 1 || user.getRoleId() == 2) {
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
                        setStyle("-fx-alignment:  TOP-RIGHT;");

                        editButton.setOnAction(event -> new RecordController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                        setGraphic(editButton);
                    }
                }
            });
            tableView.getColumns().add(editBtnColumn);
        }


        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        tableView.getStyleClass().add("main-table-view");
        tableView.setItems(model.getRecords());

        return tableView;
    }
    //endregion


    //region Groups Tab
    private Tab createGroupsTab() {
        Label sectionTitle = new Label("Grupe");
        sectionTitle.getStyleClass().add("tab-section-title");
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);

        Button addButton = new Button("Adauga o grupa");
        addButton.getStyleClass().add("ghost-button");
        addButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.ADD));

        Button addProductGroupButton = new Button("Adauga o grupa de produs");
        addProductGroupButton.getStyleClass().add("ghost-button");
        addProductGroupButton.setOnAction(event -> new ProductGroupController(stage));

        HBox headerSection = new HBox(sectionTitle, addButton, addProductGroupButton);
        headerSection.getStyleClass().add("tab-section-header");

        VBox content = new VBox(headerSection, createGroupsTable());

        Tab tab = new Tab("Grupe");
        tab.setClosable(false);
        tab.setContent(content);
        return tab;
    }

    private Tab createGroupsTab2() {
        Label sectionTitle = new Label("Grupe si subgrupe");
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.getStyleClass().add("sub-main-window-title");
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        HBox headerSection = new HBox(sectionTitle);

        if(ConfigApp.getRole().canEditGroups()) {
            Button addGroupButton = new Button("Adaugare grupa");
            addGroupButton.setGraphic(new FontIcon("mdi2p-plus"));
            addGroupButton.getStyleClass().add("sub-main-window-button");
            addGroupButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.ADD));
            headerSection.getChildren().add(addGroupButton);
        }

        headerSection.getStyleClass().add("sub-main-window-header");

        VBox content = new VBox(headerSection, createGroupsList());
        content.getStyleClass().add("sub-main-window-content-container");

        Tab tab = new Tab("Grupe si subgrupe");
        tab.setClosable(false);
        tab.setContent(content);
        return tab;
    }

//    private Node createGroupCard() {
//
//    }

    private ListView<Group> createGroupsList() {
        ListView<Group> listView = new ListView<>();
        listView.setFocusTraversable(false);
        listView.getStyleClass().add("group-listview");
        listView.setCellFactory(param -> new ListCell<>() {
            final Button addSubGroupButton = new Button("Adaugare subgrupa");
            final FontIcon plusIcon = new FontIcon("mdi2p-plus");
            final Button editButton = new Button();
            final FontIcon editIcon = new FontIcon("mdi2s-square-edit-outline");
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                Group lastItem = getItem();
                if(item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    if(item.getParentGroupId() == 0) {
                        final Label groupLabel = new Label(item.getName());


                        final Label spacer = new Label();
                        spacer.setMaxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        HBox titleContainer = new HBox();

                        if(ConfigApp.getRole().canEditGroups()) {
                            editButton.setGraphic(editIcon);
                            editButton.setStyle("-fx-background-color: TRANSPARENT; -fx-cursor: hand;");
                            editButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.EDIT, item));
                            addSubGroupButton.setGraphic(plusIcon);
                            addSubGroupButton.getStyleClass().add("sub-main-window-button");
                            addSubGroupButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.ADD, new Group(0, "", item.getId())));
                            titleContainer = new HBox(groupLabel, editButton, spacer, addSubGroupButton);
                        } else {
                            titleContainer = new HBox(groupLabel, spacer);
                        }

                        titleContainer.setAlignment(Pos.CENTER_LEFT);

                        final Label subgroupLabel = new Label("Subgrupe:");
                        subgroupLabel.setStyle("-fx-text-fill: GRAY; -fx-padding: 0 0 0 20;");
                        subgroupLabel.setMaxWidth(Double.MAX_VALUE);

                        HBox.setHgrow(subgroupLabel, Priority.ALWAYS);
                        final HBox subGroupContainer = new HBox(subgroupLabel);

                        final VBox container = new VBox(titleContainer, subGroupContainer);
                        container.getStyleClass().add("sub-main-view-list-header");
                        container.setSpacing(0);
                        container.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(container);
                    } else {
                        final Label subgroupLabel = new Label(item.getName());

                        final HBox subgroupContainer = new HBox(subgroupLabel);
                        if(ConfigApp.getRole().canEditGroups()) {
                            editButton.setGraphic(editIcon);
                            editButton.setStyle("-fx-background-color: TRANSPARENT; -fx-cursor: hand;");
                            editButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.EDIT, item));
                            subgroupContainer.getChildren().add(editButton);
                        }

                        subgroupContainer.setAlignment(Pos.CENTER_LEFT);

                        subgroupContainer.setPadding(new Insets(0, 10, 0, 60));
//                        subgroupContainer.setStyle("-fx-background-color: derive(COLOR_WHITE, -1%);");
                        setGraphic(subgroupContainer);
                    }
                    setFocusTraversable(false);
                }
            }
        });
        listView.setItems(model.getGroups2());
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }


//    private ListView<Group> createGroupsList() {
//        ListView<Group> listView = new ListView<>();
//        listView.setCellFactory(param -> new ListCell<>() {
//            @Override
//            protected void updateItem(Group item, boolean empty) {
//                super.updateItem(item, empty);
//                if(item == null || empty) {
//                    setText(null);
//                    setGraphic(null);
//                } else {
//                    setText(null);
//                    final BorderPane container = new BorderPane();
//
//                    final Button deleteGroupButton = new Button("Stergere");
//                    final Label groupNameLabel = new Label(item.getName());
//                    groupNameLabel.setMaxWidth(Double.MAX_VALUE);
//                    HBox.setHgrow(groupNameLabel, Priority.ALWAYS);
//                    final HBox titleSection = new HBox(groupNameLabel, deleteGroupButton);
//                    container.setTop(titleSection);
//
//                    final Button addGroupButton = new Button();
//                    final Label addGroupButtonLabel = new Label("Adauga o subgrupa");
//                    addGroupButtonLabel.setMaxWidth(Double.MAX_VALUE);
//                    HBox.setHgrow(addGroupButtonLabel, Priority.ALWAYS);
//                    final HBox addGroupButtonGraphic = new HBox(addGroupButtonLabel, new FontIcon("mdi2p-plus-box"));
//                    addGroupButton.setGraphic(addGroupButtonGraphic);
//                    addGroupButton.setMaxWidth(Double.MAX_VALUE);
//                    addGroupButton.getStyleClass().add("add-group-button");
//                    final VBox centerSection = new VBox(addGroupButton, createSubGroupListView());
//                    container.setCenter(centerSection);
//
//                    setGraphic(container);
//                }
//            }
//        });
//        listView.setEditable(false);
//        listView.setFocusModel(null);
//        listView.setItems(model.getGroups());
//        return listView;
//    }

    private ListView<Group> createSubGroupListView() {
        ListView<Group> listView = new ListView<>();
        return  listView;
    }

    private TableView<Group> createGroupsTable() {
        TableView<Group> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("Nu exista grupe."));

        TableColumn<Group, String> nameColumn = new TableColumn<>("Denumire");
        nameColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getName()));
        tableView.getColumns().add(nameColumn);

        TableColumn<Group, Integer> editBtnColumn = new TableColumn<>();
        editBtnColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getId()));
        editBtnColumn.setCellFactory(column -> new TableCell<>() {
            final Button editButton = new Button();
            final FontIcon fontIcon = new FontIcon("mdi2s-square-edit-outline");

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);

                    editButton.setGraphic(fontIcon);
                    editButton.getStyleClass().add("filled-button");
                    editButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                    setGraphic(editButton);
                    setStyle("-fx-alignment:  TOP-RIGHT;");
                }
            }
        });
        tableView.getColumns().add(editBtnColumn);
        tableView.getStyleClass().add("main-table-view");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(model.getGroups());
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return tableView;
    }
    //endregion

    //region Users Tab
    private Tab createUsersTab() {
        Label sectionTitle = new Label("Utilizatori");
        sectionTitle.getStyleClass().add("tab-section-title");
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        HBox headerSection = new HBox(sectionTitle);

        if(ConfigApp.getRole().canEditUsers()) {
            Button addButton = new Button("Adauga un utilizator");
            addButton.getStyleClass().add("ghost-button");
            addButton.setOnAction(event -> new UserController(stage));
            addButton.setGraphic(new FontIcon("mdi2p-plus"));
            headerSection.getChildren().add(addButton);
        }

        headerSection.getStyleClass().add("tab-section-header");

        VBox content = new VBox(headerSection, createUsersTable());

        Tab tab = new Tab("Utilizatori");
        tab.setClosable(false);
        tab.setContent(content);
        return tab;
    }

    private TableView<User> createUsersTable() {
        TableView<User> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("Nu exista utilizatori."));

        TableColumn<User, String> usernameColumn = new TableColumn<>("Nume utilizator");
        usernameColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getUsername()));
        tableView.getColumns().add(usernameColumn);

        TableColumn<User, Integer> roleColumn = new TableColumn<>("Rol");
        roleColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getRoleId()));
        roleColumn.setCellFactory(column -> new TableCell<>() {
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setText(null);
                } else {
                    setText(new UserRole(ACCESS_LEVEL.values()[item]).getName());
                }
            }
        });
        tableView.getColumns().add(roleColumn);

        TableColumn<User, Integer> editBtnColumn = new TableColumn<>();
        editBtnColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getId()));
        editBtnColumn.setCellFactory(column -> new TableCell<>() {
            final Button editButton = new Button();
            final FontIcon fontIcon = new FontIcon("mdi2s-square-edit-outline");

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);

                    editButton.setGraphic(fontIcon);
                    editButton.getStyleClass().add("filled-button");
                    editButton.setOnAction(event -> {
                        tableView.getSelectionModel().select(getIndex());
                        new UserController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem());
                    });
                    setGraphic(editButton);
                    setStyle("-fx-alignment:  TOP-RIGHT;");
                }
            }
        });
        tableView.getColumns().add(editBtnColumn);
        tableView.getStyleClass().add("main-table-view");

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(model.getUsers());
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return tableView;
    }

    //endregion
}
