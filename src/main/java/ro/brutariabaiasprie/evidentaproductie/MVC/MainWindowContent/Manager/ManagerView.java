package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Manager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;

import ro.brutariabaiasprie.evidentaproductie.Data.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport.ExcelExportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport.ExcelImportController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order.OrderController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product.ProductController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Record.RecordController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User.UserController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class ManagerView extends Parent implements Builder<Region> {
    private final Stage stage;
    private final ManagerModel model;
    private final Consumer<Order> productionShortcutHandler;
    //products tab
    private Button addProductButton;
    private Button importProductsButton;

    //orders tab
    private Button addOrderButton;
    private Button excelExportButton;

    public ManagerView(ManagerModel model, Stage stage, Consumer<Order> productionShortcutHandler) {
        this.model = model;
        this.stage = stage;
        this.productionShortcutHandler = productionShortcutHandler;
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
        if(model.getCONNECTED_USER().getID_ROLE() == 1 || model.getCONNECTED_USER().getID_ROLE() == 2) {
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
            tabPane.getTabs().add(createGroupsTab());
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
                        editButton.setOnAction(event -> new ProductController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem()));
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

        Label recordsLabel = new Label("Realizari fara comanda");
        recordsLabel.getStyleClass().add("tab-section-title");
        contentContainer.getChildren().addAll(createOrdersSectionHeader(), createOrdersTable(), recordsLabel, createRecordsWithNoOrderTable());
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

        if(ConfigApp.getRole().canEditOrders()) {
            addOrderButton = new Button();
            addOrderButton.setOnAction(event -> {
                new OrderController(stage, WINDOW_TYPE.ADD);
            });
            excelExportButton = new Button();
            excelExportButton.setOnAction(event -> {
                ExcelExportController excelExportController = new ExcelExportController(stage);
            });

            excelExportButton.setTooltip(new Tooltip("Exporta inregistrarile realizate intr-un excel."));
            if(stage.getWidth() < Globals.MINIMIZE_WIDTH) {
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
        remainderColumn.setCellFactory(column -> new TableCell<>() {
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

        User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

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


        ordersTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

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

        if(user.getID_ROLE() == 1 || user.getID_ROLE() == 2) {
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

        TableColumn<User, String> passwordColumn = new TableColumn<>("Parola");
        passwordColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getPassword()));
        tableView.getColumns().add(passwordColumn);

        TableColumn<User, Integer> roleColumn = new TableColumn<>("Rol");
        roleColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getID_ROLE()));
        tableView.getColumns().add(roleColumn);

        TableColumn<User, Integer> editBtnColumn = new TableColumn<>();
        editBtnColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().getID()));
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
//                    editButton.setOnAction(event -> new GroupController(stage, WINDOW_TYPE.EDIT, getTableRow().getItem()));
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
