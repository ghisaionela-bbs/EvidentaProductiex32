package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Manager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.kordamp.ikonli.javafx.FontIcon;

import ro.brutariabaiasprie.evidentaproductie.Data.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.ColoredProgressBar;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class ManagerView extends Parent implements Builder<Region> {
    private final Stage stage;
    private final ManagerModel model;
    private final Consumer<Order> productionShortcutHandler;
    private final Consumer<Boolean> reloadOrders;
    private final Runnable filterOrders;
    private final Runnable updateFilters;
    private final Runnable filterProducts;
    private final Runnable updateProductsFilters;

    private CheckComboBox<Group> orderGroupComboBox;
    private CheckComboBox<Group> orderSubgroupComboBox;
    private ToggleGroup orderStatusToggleGroup = new ToggleGroup();

    private CheckComboBox<Group> productGroupComboBox;
    private CheckComboBox<Group> productSubgroupComboBox;

    public ManagerView(ManagerModel model, Stage stage, Consumer<Order> productionShortcutHandler, Consumer<Boolean> reloadOrders, Runnable filterOrders, Runnable updateFilters, Runnable filterProducts, Runnable updateProductsFilters) {
        this.model = model;
        this.stage = stage;
        this.productionShortcutHandler = productionShortcutHandler;
        this.reloadOrders = reloadOrders;
        this.filterOrders = filterOrders;
        this.updateFilters = updateFilters;
        this.filterProducts = filterProducts;
        this.updateProductsFilters = updateProductsFilters;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setFillWidth(true);
        root.getChildren().add(createTabs());

        return root;
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
        contentContainer.getStyleClass().add("sub-main-window-content-container");
        productsTab.setContent(contentContainer);
        productsTab.setClosable(false);
        return productsTab;
    }

    private Node createProductsSectionHeader() {
        VBox header = new VBox();

        Label sectionTitle = new Label("Produse");
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.getStyleClass().add("sub-main-window-title");
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        HBox headerSection = new HBox(sectionTitle);
        headerSection.getStyleClass().add("hbox-container");

        header.getChildren().add(headerSection);
        header.getStyleClass().add("sub-main-window-header");

        //Group filter
        Label groupLabel = new Label("Grupa:");
        groupLabel.setMaxWidth(Double.MAX_VALUE);
        productGroupComboBox = new CheckComboBox<>(model.getProductGroupFilterList());
        productGroupComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Group group) {
                if (group == null) {
                    return "Toate";
                }
                return group.getName();
            }

            @Override
            public Group fromString(String s) {
                return null;
            }
        });
        productGroupComboBox.setMaxWidth(200);
        productGroupComboBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
            private boolean changing = false;
            @Override
            public void onChanged(Change<? extends Integer> change) {
                productGroupComboBox.setTitle("");
                if (!changing) {
                    change.next();
                    if (change.wasRemoved() && change.getRemoved().contains(0)) {
                        changing = true;
                        productGroupComboBox.getCheckModel().clearChecks();
                        changing = false;
                    } else if (change.wasAdded() && change.getList().contains(0)) {
                        changing = true;
                        productGroupComboBox.getCheckModel().checkAll();
                        changing = false;
                    } else if (change.getList().size() < productGroupComboBox.getItems().size()) {
                        changing = true;
                        productGroupComboBox.getCheckModel().clearCheck(0);
                        changing = false;
                    }
                    updateProductsFilters.run();
                    filterProducts.run();
                }
            }
        });
        HBox groupFilter = new HBox(groupLabel, productGroupComboBox);
        groupFilter.setSpacing(8);
        groupFilter.setAlignment(Pos.CENTER_LEFT);
        groupFilter.getStyleClass().add("section");
        groupFilter.getStyleClass().add("vbox-layout");

        // Subgroup filter
        Label subgroupLabel = new Label("Subgrupa:");
        subgroupLabel.setMaxWidth(Double.MAX_VALUE);
        productSubgroupComboBox = new CheckComboBox<>(model.getProductSubgroupFilterList());
        productSubgroupComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Group group) {
                if (group == null) {
                    return "Toate";
                }
                return group.getName();
            }

            @Override
            public Group fromString(String s) {
                return null;
            }
        });
        productSubgroupComboBox.setMaxWidth(200);
        productSubgroupComboBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
            private boolean changing = false;
            @Override
            public void onChanged(Change<? extends Integer> change) {
                productSubgroupComboBox.setTitle("");
                if (!changing) {
                    change.next();
                    if (change.wasRemoved() && change.getRemoved().contains(0)) {
                        changing = true;
                        productSubgroupComboBox.getCheckModel().clearChecks();
                        changing = false;
                    } else if (change.wasAdded() && change.getList().contains(0)) {
                        changing = true;
                        productSubgroupComboBox.getCheckModel().checkAll();
                        changing = false;
                    } else if (change.getList().size() < productSubgroupComboBox.getItems().size()) {
                        changing = true;
                        productSubgroupComboBox.getCheckModel().clearCheck(0);
                        changing = false;
                    }

                    filterProducts.run();
                }
            }
        });
        HBox subgroupFilter = new HBox(subgroupLabel, productSubgroupComboBox);
        subgroupFilter.setSpacing(8);
        subgroupFilter.setAlignment(Pos.CENTER_LEFT);
        subgroupFilter.getStyleClass().add("section");
        subgroupFilter.getStyleClass().add("vbox-layout");

        HBox groupAndSubgroupFilterContainer = new HBox(groupFilter, subgroupFilter);
        groupAndSubgroupFilterContainer.setSpacing(16);

        header.getChildren().addAll(groupAndSubgroupFilterContainer);

        if(ConfigApp.getRole().canEditProducts()) {
            Button addProductButton = new Button();
            addProductButton.textProperty().bind(Bindings.createStringBinding(
                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Adauga produs";},
                    stage.widthProperty())
            );
            addProductButton.setOnAction(event -> new ProductController(stage));
            addProductButton.setGraphic(new FontIcon("mdi2p-plus"));
            addProductButton.getStyleClass().add("sub-main-window-button");

            Button importProductsButton = new Button();
            importProductsButton.textProperty().bind(Bindings.createStringBinding(
                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Importa produse";},
                    stage.widthProperty())
            );
            importProductsButton.setOnAction(event -> new ExcelImportController(stage));
            importProductsButton.setGraphic(new FontIcon("mdi2a-application-import"));
            importProductsButton.setTooltip(new Tooltip("Importa produse dintr-un fisier excel."));
            importProductsButton.getStyleClass().add("sub-main-window-button");

            headerSection.getChildren().addAll(addProductButton, importProductsButton);

        }
        return header;
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
        productNameColumn.prefWidthProperty().bind(productsTableView.widthProperty().multiply(0.5).subtract(72));


        TableColumn<Product, String> groupColumn = new TableColumn<>("Grupa");
        groupColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGroup() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(cellData.getValue().getGroup().getName());
        });
        productsTableView.getColumns().add(groupColumn);
        groupColumn.prefWidthProperty().bind(productsTableView.widthProperty().multiply(0.15));


        TableColumn<Product, String> subgroupColumn = new TableColumn<>("Subgrupa");
        subgroupColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGroup() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(cellData.getValue().getSubgroup().getName());
        });
        productsTableView.getColumns().add(subgroupColumn);
        subgroupColumn.prefWidthProperty().bind(productsTableView.widthProperty().multiply(0.15));


        TableColumn<Product, Double> batchColumn = new TableColumn<>("Sarja");
        batchColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGroup() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(cellData.getValue().getBatchValue());
        });
        productsTableView.getColumns().add(batchColumn);
        batchColumn.prefWidthProperty().bind(productsTableView.widthProperty().multiply(0.1));


        TableColumn<Product, String> umColumn = new TableColumn<>("UM");
        umColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGroup() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(cellData.getValue().getUnitMeasurement());
        });
        productsTableView.getColumns().add(umColumn);
        umColumn.prefWidthProperty().bind(productsTableView.widthProperty().multiply(0.1));

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
            editBtnColumn.prefWidthProperty().set(64);
        }

//        productsTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

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
        contentContainer.getStyleClass().add("sub-main-window-content-container");
        ordersTab.setContent(contentContainer);

        return ordersTab;
    }

    private Node createOrdersSectionHeader() {
        VBox header = new VBox();

        Label sectionTitle = new Label("Comenzi");
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.getStyleClass().add("sub-main-window-title");
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        HBox headerSection = new HBox(sectionTitle);
        headerSection.getStyleClass().add("hbox-container");

        header.getChildren().add(headerSection);
        header.getStyleClass().add("sub-main-window-header");

        if(ConfigApp.getRole().canEditOrders()) {
            RadioButton orderStatusAll = new RadioButton("Toate");
            orderStatusAll.setUserData(-1);
            orderStatusAll.setToggleGroup(orderStatusToggleGroup);
            orderStatusAll.setSelected(true);
            RadioButton orderStatusOpen = new RadioButton("Deschise");
            orderStatusOpen.setUserData(0);
            orderStatusOpen.setToggleGroup(orderStatusToggleGroup);
            RadioButton orderStatusClosed = new RadioButton("Inchise");
            orderStatusClosed.setUserData(1);
            orderStatusClosed.setToggleGroup(orderStatusToggleGroup);
            HBox orderStatusFilterContainer = new HBox(orderStatusAll, orderStatusOpen, orderStatusClosed);
            orderStatusFilterContainer.setSpacing(16);
            orderStatusToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> filterOrders.run());

            //Group filter
            Label groupLabel = new Label("Grupa:");
            groupLabel.setMaxWidth(Double.MAX_VALUE);
            orderGroupComboBox = new CheckComboBox<>(model.getGroupFilterList());
            orderGroupComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Group group) {
                    if (group == null) {
                        return "Toate";
                    }
                    return group.getName();
                }

                @Override
                public Group fromString(String s) {
                    return null;
                }
            });
            orderGroupComboBox.setMaxWidth(200);
            orderGroupComboBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
                private boolean changing = false;
                @Override
                public void onChanged(Change<? extends Integer> change) {
                    orderGroupComboBox.setTitle("");
                    if (!changing) {
                        change.next();
                        if (change.wasRemoved() && change.getRemoved().contains(0)) {
                            changing = true;
                            orderGroupComboBox.getCheckModel().clearChecks();
                            changing = false;
                        } else if (change.wasAdded() && change.getList().contains(0)) {
                            changing = true;
                            orderGroupComboBox.getCheckModel().checkAll();
                            changing = false;
                        } else if (change.getList().size() < orderGroupComboBox.getItems().size()) {
                            changing = true;
                            orderGroupComboBox.getCheckModel().clearCheck(0);
                            changing = false;
                        }
                        updateFilters.run();
                        filterOrders.run();
                    }
                }
            });
            HBox groupFilter = new HBox(groupLabel, orderGroupComboBox);
            groupFilter.setSpacing(8);
            groupFilter.setAlignment(Pos.CENTER_LEFT);
            groupFilter.getStyleClass().add("section");
            groupFilter.getStyleClass().add("vbox-layout");

            // Subgroup filter
            Label subgroupLabel = new Label("Subgrupa:");
            subgroupLabel.setMaxWidth(Double.MAX_VALUE);
            orderSubgroupComboBox = new CheckComboBox<>(model.getSubgroupFilterList());
            orderSubgroupComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Group group) {
                    if (group == null) {
                        return "Toate";
                    }
                    return group.getName();
                }

                @Override
                public Group fromString(String s) {
                    return null;
                }
            });
            orderSubgroupComboBox.setMaxWidth(200);
            orderSubgroupComboBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
                private boolean changing = false;
                @Override
                public void onChanged(Change<? extends Integer> change) {
                    orderSubgroupComboBox.setTitle("");
                    if (!changing) {
                        change.next();
                        if (change.wasRemoved() && change.getRemoved().contains(0)) {
                            changing = true;
                            orderSubgroupComboBox.getCheckModel().clearChecks();
                            changing = false;
                        } else if (change.wasAdded() && change.getList().contains(0)) {
                            changing = true;
                            orderSubgroupComboBox.getCheckModel().checkAll();
                            changing = false;
                        } else if (change.getList().size() < orderSubgroupComboBox.getItems().size()) {
                            changing = true;
                            orderSubgroupComboBox.getCheckModel().clearCheck(0);
                            changing = false;
                        }

                        filterOrders.run();
                    }
                }
            });
            HBox subgroupFilter = new HBox(subgroupLabel, orderSubgroupComboBox);
            subgroupFilter.setSpacing(8);
            subgroupFilter.setAlignment(Pos.CENTER_LEFT);
            subgroupFilter.getStyleClass().add("section");
            subgroupFilter.getStyleClass().add("vbox-layout");

            HBox groupAndSubgroupFilterContainer = new HBox(groupFilter, subgroupFilter);
            groupAndSubgroupFilterContainer.setSpacing(16);

            header.getChildren().addAll(orderStatusFilterContainer, groupAndSubgroupFilterContainer);

            Button addOrderButton = new Button();
            addOrderButton.textProperty().bind(Bindings.createStringBinding(
                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Adauga comanda";},
                    stage.widthProperty())
            );
            addOrderButton.setOnAction(event -> new OrderController(stage, WINDOW_TYPE.ADD));
            addOrderButton.setGraphic(new FontIcon("mdi2p-plus"));
            addOrderButton.getStyleClass().add("sub-main-window-button");

            Button orderExportButton = new Button();
            orderExportButton.textProperty().bind(Bindings.createStringBinding(
                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Exporta in excel";},
                    stage.widthProperty())
            );
            orderExportButton.setOnAction(event -> new OrderExportController(stage));
            orderExportButton.setGraphic(new FontIcon("mdi2f-file-export-outline"));
            orderExportButton.getStyleClass().add("sub-main-window-button");

            Button importOrderButton = new Button();
            importOrderButton.textProperty().bind(Bindings.createStringBinding(
                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Importa comenzi";},
                    stage.widthProperty())
            );
            importOrderButton.setOnAction(event -> new OrderImportController(stage));
            importOrderButton.setGraphic(new FontIcon("mdi2a-application-import"));
            importOrderButton.getStyleClass().add("sub-main-window-button");

            addOrderButton.getStyleClass().add("sub-main-window-button");

            headerSection.getChildren().addAll(addOrderButton, importOrderButton, orderExportButton);
        }

        return header;
    }

    private TableView<Order> createOrdersTable() {
        TableView<Order> ordersTableView = new TableView<>();
        ordersTableView.setPlaceholder(new Label("Nu exista comenzi."));
        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

        if(ConfigApp.getRole().canEditOrders()) {
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
            isClosedColumn.prefWidthProperty().set(32);
        }


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
        DecimalFormat df = new DecimalFormat("#.##");
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

                            int currentIndex = indexProperty().getValue() < 0 ? 1 : indexProperty().getValue();
                            Order order = param.getTableView().getItems().get(currentIndex);
                            double percentage = order.getCompleted() / order.getQuantity();

                            // If the batch value of the order is defined show the [number of batches completed] / [number of total batches]
                            if (order.getProduct().getBatchValue() > 0) {
                                double quantity = order.getQuantity();
                                double completed = order.getCompleted();
                                double batchValue = order.getProduct().getBatchValue();
                                double totalBatches = 0;
                                double completedBatches = 0;

                                totalBatches = Math.ceil(quantity / batchValue);

                                // When the quantity completed is less than one batch
                                if (completed == quantity) {
                                    completedBatches = totalBatches;
                                }
                                else if (completed < quantity) {
                                    completedBatches = Math.floor(completed / batchValue);
                                } else {
                                    completedBatches = Math.floor(completed / batchValue) + 1;
                                }

                                batchNumber.setText("Sarja: " + (int)completedBatches + "/" + (int)totalBatches);
                            } else {
                                batchNumber.setText("");
                            }

                            ColoredProgressBar progressBar = new ColoredProgressBar(percentage);
                            percentageLabel.setText(df.format(percentage * 100.0) + "%");
                            HBox progressContainer = new HBox(batchNumber, progressBar, percentageLabel);
                            VBox container = new VBox(txtName, progressContainer);
                            progressContainer.setSpacing(8);
                            setGraphic(container);
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

        orderCounterColumn.prefWidthProperty().set(64);
        dateTimeColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        if(ConfigApp.getRole().canEditOrders()) {
            productNameColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.5).subtract(192));
        } else {
            productNameColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.5).subtract(128));
        }
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

        Button addButton = new Button("Adauga grupa");
        addButton.getStyleClass().add("ghost-button");
        addButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.ADD));

        Button addProductGroupButton = new Button("Adauga grupa de produs");
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
        Label sectionTitle = new Label("Grupe");
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.getStyleClass().add("sub-main-window-title");
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        HBox headerSection = new HBox(sectionTitle);

        if(ConfigApp.getRole().canEditGroups()) {
            Button addGroupButton = new Button();
            addGroupButton.textProperty().bind(Bindings.createStringBinding(
                () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Adauga grupa";},
                stage.widthProperty())
            );

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

    private ListView<Group> createGroupsList() {
        ListView<Group> listView = new ListView<>();
        listView.setFocusTraversable(false);
        listView.getStyleClass().add("group-listview");
        listView.setCellFactory(param -> new ListCell<>() {
            final Button addSubGroupButton = new Button();
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

                            addSubGroupButton.textProperty().bind(Bindings.createStringBinding(
                                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Adauga subgrupa";},
                                    stage.widthProperty())
                            );

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
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.getStyleClass().add("sub-main-window-title");
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
        HBox headerSection = new HBox(sectionTitle);
        headerSection.getStyleClass().add("sub-main-window-header");
//
//        Label sectionTitle = new Label("Utilizatori");
//        sectionTitle.getStyleClass().add("tab-section-title");
//        sectionTitle.setMaxWidth(Double.MAX_VALUE);
//        HBox.setHgrow(sectionTitle, Priority.ALWAYS);
//        HBox headerSection = new HBox(sectionTitle);

        if(ConfigApp.getRole().canEditUsers()) {
            Button addUserButton = new Button();
            addUserButton.textProperty().bind(Bindings.createStringBinding(
                    () -> {if (stage.getWidth() < Globals.MINIMIZE_WIDTH) {return "";} else return "Adauga utilizator";},
                    stage.widthProperty())
            );

            addUserButton.setOnAction(event -> new UserController(stage));
            addUserButton.setGraphic(new FontIcon("mdi2p-plus"));
            addUserButton.getStyleClass().add("sub-main-window-button");

            headerSection.getChildren().add(addUserButton);
        }

//        headerSection.getStyleClass().add("tab-section-header");

        VBox content = new VBox(headerSection, createUsersTable());
        content.getStyleClass().add("sub-main-window-content-container");

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

    public int getOrderStatusFilter() {
        return (int) orderStatusToggleGroup.getSelectedToggle().getUserData();
    }

    public void setGroupFilter() {
        orderGroupComboBox.getCheckModel().check(0);
    }

    public void setSubgroupFilter() {
        orderSubgroupComboBox.getCheckModel().check(0);
    }

    public CheckComboBox<Group> getOrderGroupFilter() {
        return orderGroupComboBox;
    }

    public CheckComboBox<Group> getOrderSubgroupFilter() {
        return orderSubgroupComboBox;
    }

    public void setProductGroupFilter() {
        productGroupComboBox.getCheckModel().check(0);
    }

    public void setProductSubgroupFilter() {
        productSubgroupComboBox.getCheckModel().check(0);
    }

    public CheckComboBox<Group> getProductGroupFilter() {
        return productGroupComboBox;
    }

    public CheckComboBox<Group> getProductSubgroupFilter() {
        return productSubgroupComboBox;
    }


    //endregion
}
