package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemReportDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.Globals;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewOrder.AddNewOrderController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport.ExcelExportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport.ExcelImportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewProduct.AddNewProductController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ManagerView extends Parent implements Builder<Region> {
    private final Stage PARENT_STAGE;
    private final ManagerModel model;
    private final Runnable refreshProductsHandler;
    private final Runnable refreshOrdersHandler;

    //products tab
    private Button addProductButton;
    private Button importProductsButton;
    //orders tab
    private Button addOrderButton;
    private Button excelExportButton;

    public ManagerView(ManagerModel model, Stage parentStage, Runnable refreshProductsHandler, Runnable refreshOrdersHandler) {
        this.model = model;
        this.PARENT_STAGE = parentStage;
        this.refreshProductsHandler = refreshProductsHandler;
        this.refreshOrdersHandler = refreshOrdersHandler;
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
            System.out.println(PARENT_STAGE.getWidth());
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

        Tab productsTab = createProductsTab();
        productsTab.setClosable(false);

        tabPane.getTabs().addAll(ordersTab, productsTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return tabPane;
    }

    //region Products Tab
    private Tab createProductsTab() {
        Tab productsTab =  new Tab("Produse");
        VBox contentContainer = new VBox();

        contentContainer.getChildren().addAll(createProductsSectionHeader(), createProductsTable());
        productsTab.setContent(contentContainer);

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
        addProductButton.setOnAction(event -> {
            AddNewProductController controller = new AddNewProductController(PARENT_STAGE);
            if(controller.getSUCCESS()) {
                refreshProductsHandler.run();
            }
        });

        importProductsButton = new Button();
        importProductsButton.setOnAction(event -> {
            ExcelImportController excelImportController = new ExcelImportController(PARENT_STAGE);
            if(excelImportController.getSUCCESS()) {
                refreshProductsHandler.run();
            }
        });
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

    private TableView<ProductDTO> createProductsTable() {
        TableView<ProductDTO> productsTableView = new TableView<>();

        TableColumn<ProductDTO, String> productNameColumn = new TableColumn<>("Denumire");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
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

        TableColumn<ProductDTO, String> productUnitMeasurementColumn = new TableColumn<>("UM");
        productUnitMeasurementColumn.setCellValueFactory(new PropertyValueFactory<>("unitMeasurement"));
        productsTableView.getColumns().add(productUnitMeasurementColumn);

        productsTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        productNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 70);
        productUnitMeasurementColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30);

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

        addOrderButton = new Button();
        addOrderButton.setOnAction(event -> {
            AddNewOrderController orderController = new AddNewOrderController(PARENT_STAGE);
            Platform.runLater(() -> {
                if(orderController.isSUCCESS()) {
                    refreshOrdersHandler.run();
                }
            });
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

        sectionHeaderContainer.getChildren().addAll(ordersSectionTitle, addOrderButton, excelExportButton);
        sectionHeaderContainer.getStyleClass().add("tab-section-header");
        return sectionHeaderContainer;
    }

    private TableView<OrderItemReportDTO> createOrdersTable() {
        TableView<OrderItemReportDTO> ordersTableView = new TableView<>();
        ordersTableView.setPlaceholder(new Label("Nu exista comenzi."));
        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

        TableColumn<OrderItemReportDTO, Integer> orderIDColumn = new TableColumn<>("Comanda");
        orderIDColumn.setCellValueFactory(new PropertyValueFactory<>("ORDER_ID"));
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


        TableColumn<OrderItemReportDTO, Timestamp> dateTimeColumn = new TableColumn<>("Plasata la");
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateAndTimeInserted"));
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

        TableColumn<OrderItemReportDTO, String> productNameColumn = new TableColumn<>("Denumire");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
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

        TableColumn<OrderItemReportDTO, Double> quantityColumn = new TableColumn<>("Comandat");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
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

        TableColumn<OrderItemReportDTO, Double> completedColumn = new TableColumn<>("Realizat");
        completedColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
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

        TableColumn<OrderItemReportDTO, Double> remainderColumn = new TableColumn<>("Rest");
        remainderColumn.setCellValueFactory(new PropertyValueFactory<>("remainder"));
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

        TableColumn<OrderItemReportDTO, String> productUnitMeasurementColumn = new TableColumn<>("UM");
        productUnitMeasurementColumn.setCellValueFactory(new PropertyValueFactory<>("unitMeasurement"));
        ordersTableView.getColumns().add(productUnitMeasurementColumn);

        ordersTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        orderIDColumn.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        dateTimeColumn.setMaxWidth(1f * Integer.MAX_VALUE * 13);
        productNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 34);
        quantityColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 13);
        productUnitMeasurementColumn.setMaxWidth(1f * Integer.MAX_VALUE * 6);
        completedColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 13);
        remainderColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 13);

        ordersTableView.setItems(model.getOrders());
        ordersTableView.getStyleClass().add("main-table-view");

        return ordersTableView;
    }
    //endregion

}
