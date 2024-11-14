package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;

import ro.brutariabaiasprie.evidentaproductie.Data.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport.ExcelExportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport.ExcelImportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order.OrderController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product.ProductController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class ManagerView extends Parent implements Builder<Region> {
    private final Stage PARENT_STAGE;
    private final ManagerModel model;
    //products tab
    private Button addProductButton;
    private Button importProductsButton;

    //orders tab
    private Button addOrderButton;
    private Button excelExportButton;

    public ManagerView(ManagerModel model, Stage parentStage) {
        this.model = model;
        this.PARENT_STAGE = parentStage;
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
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            if(PARENT_STAGE.getWidth() < Globals.MINIMIZE_WIDTH) {
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

        PARENT_STAGE.widthProperty().addListener(stageSizeListener);
        PARENT_STAGE.heightProperty().addListener(stageSizeListener);
    }

    private Node createTabs() {
        TabPane tabPane =  new TabPane();

        Tab ordersTab = createOrdersTab();
        ordersTab.setClosable(false);
        tabPane.getTabs().add(ordersTab);

        if(model.getCONNECTED_USER().getID_ROLE() == 1) {
            tabPane.getTabs().add(createProductsTab());
            tabPane.getTabs().add(createGroupsTab());
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

        addProductButton = new Button();
        addProductButton.setOnAction(event -> new ProductController(PARENT_STAGE));

        importProductsButton = new Button();
        importProductsButton.setOnAction(event -> new ExcelImportController(PARENT_STAGE));
        importProductsButton.setTooltip(new Tooltip("Importa produse dintr-un fisier excel."));
        if(PARENT_STAGE.getWidth() < Globals.MINIMIZE_WIDTH) {
            addProductButton.setText("➕");
            importProductsButton.setText("\uD83D\uDCE5");
        } else {
            addProductButton.setText("➕ Adauga un produs");
            importProductsButton.setText("Importa produse \uD83D\uDCE5");
        }
        addProductButton.getStyleClass().add("ghost-button");
        importProductsButton.getStyleClass().add("ghost-button");

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

        TableColumn<Product, String> productUnitMeasurementColumn = new TableColumn<>("UM");
        productUnitMeasurementColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUnitMeasurement()));
        productsTableView.getColumns().add(productUnitMeasurementColumn);

        TableColumn<Product, String> groupColumn = new TableColumn<>("Grupa");
        groupColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGroup() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(cellData.getValue().getGroup().getName());
        });
        productsTableView.getColumns().add(groupColumn);

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

                    editButton.setGraphic(new FontIcon("mdi2s-square-edit-outline"));

                    editButton.getStyleClass().add("filled-button");

                    editButton.setOnAction(event -> new ProductController(PARENT_STAGE, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                    setGraphic(editButton);
                    setStyle("-fx-alignment: CENTER-RIGHT;");
                }
            }
        });
        productsTableView.getColumns().add(editBtnColumn);

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

        if(model.getCONNECTED_USER().getID_ROLE() == 0 || model.getCONNECTED_USER().getID_ROLE() == 1) {
            addOrderButton = new Button();
            addOrderButton.setOnAction(event -> {
                new OrderController(PARENT_STAGE, WINDOW_TYPE.ADD);
//                AddNewOrderController orderController = new AddNewOrderController(PARENT_STAGE);
            });
            excelExportButton = new Button();
            excelExportButton.setOnAction(event -> {
                ExcelExportController excelExportController = new ExcelExportController(PARENT_STAGE);
            });

            excelExportButton.setTooltip(new Tooltip("Exporta inregistrarile realizate intr-un excel."));
            if(PARENT_STAGE.getWidth() < Globals.MINIMIZE_WIDTH) {
                addOrderButton.setText("➕");
                excelExportButton.setText("");
            } else {
                addOrderButton.setText("➕ Adauga o comanda");
                excelExportButton.setText("Exporta realizari in excel");
                excelExportButton.setGraphic(new FontIcon("mdi2m-microsoft-excel"));
            }
            addOrderButton.getStyleClass().add("ghost-button");
            excelExportButton.getStyleClass().add("ghost-button");
            sectionHeaderContainer.getChildren().addAll(addOrderButton, excelExportButton);
        }

        sectionHeaderContainer.getStyleClass().add("tab-section-header");
        return sectionHeaderContainer;
    }

    private TableView<Order> createOrdersTable() {
        TableView<Order> ordersTableView = new TableView<>();
        ordersTableView.setPlaceholder(new Label("Nu exista comenzi."));
        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

        TableColumn<Order, Integer> orderIDColumn = new TableColumn<>("Comanda");
        orderIDColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        orderIDColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    if(item == 0) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            }
        });
        ordersTableView.getColumns().add(orderIDColumn);


        TableColumn<Order, Timestamp> dateTimeColumn = new TableColumn<>("Plasata la");
        dateTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateTimeInserted()));
        ordersTableView.getColumns().add(dateTimeColumn);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateTimeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        TableColumn<Order, String> productNameColumn = new TableColumn<>("Produs");
        productNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getName()));
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
                }
            }
        });

        TableColumn<Order, Double> remainderColumn = new TableColumn<>("Rest");
        remainderColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRemainder()));
        ordersTableView.getColumns().add(remainderColumn);
        remainderColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        TableColumn<Order, String> productUnitMeasurementColumn = new TableColumn<>("UM");
        productUnitMeasurementColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getUnitMeasurement()));
        ordersTableView.getColumns().add(productUnitMeasurementColumn);

        User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

        TableColumn<Order, Integer> editBtnColumn = new TableColumn<>();
        editBtnColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getId()));
        if(user.getID_ROLE() == 1) {
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
                        editButton.getStyleClass().add("filled-button");
                        editButton.setGraphic(fontIcon);
                        editButton.setOnAction(event -> new OrderController(PARENT_STAGE, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                        setGraphic(editButton);
                        setStyle("-fx-alignment: CENTER-RIGHT;");
                    }
                }
            });
        } else {
            editBtnColumn.setCellFactory(column -> new TableCell<>() {
                final Button editButton = new Button();
                final FontIcon fontIcon = new FontIcon("mdi2a-arrow-right-drop-circle");
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(null);
                        editButton.getStyleClass().add("filled-button");
                        editButton.setGraphic(fontIcon);
                        editButton.setOnAction(event -> new OrderController(PARENT_STAGE, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                        setGraphic(editButton);
                        setStyle("-fx-alignment: CENTER-RIGHT;");
                    }
                }
            });
        }

        ordersTableView.getColumns().add(editBtnColumn);


        ordersTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
//        orderIDColumn.setMaxWidth(1f * Integer.MAX_VALUE * 8);
//        dateTimeColumn.setMaxWidth(1f * Integer.MAX_VALUE * 13);
//        productNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 34);
//        quantityColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 13);
//        productUnitMeasurementColumn.setMaxWidth(1f * Integer.MAX_VALUE * 6);
//        completedColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 13);
//        remainderColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 13);

        ordersTableView.setItems(model.getOrders());
        ordersTableView.getStyleClass().add("main-table-view");

        return ordersTableView;
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
        addButton.setOnAction(event -> new GroupController(PARENT_STAGE, WINDOW_TYPE.ADD));

        HBox headerSection = new HBox(sectionTitle, addButton);
        headerSection.getStyleClass().add("tab-section-header");

        VBox content = new VBox(headerSection, createGroupsTable());

        Tab tab = new Tab("Grupe");
        tab.setClosable(false);
        tab.setContent(content);
        return tab;
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
                    editButton.setOnAction(event -> new GroupController(PARENT_STAGE, WINDOW_TYPE.EDIT, getTableRow().getItem()));
                    setGraphic(editButton);
                    setStyle("-fx-alignment: CENTER-RIGHT;");
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
}
