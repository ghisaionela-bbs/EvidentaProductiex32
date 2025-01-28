package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.*;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class OrderAssociationView extends Parent implements Builder<Region> {
    private final OrderAssociationModel model;
    private final Consumer<ACTION_TYPE> actionHandler;

    private TableView<Order> ordersTableView;

    public OrderAssociationView(OrderAssociationModel model, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        Node ordersSection = createOrdersSection();

        root.setCenter(ordersSection);
        root.setBottom(createButtonsContainer());

//        root.getChildren().addAll(ordersSection, createButtonsContainer());
        root.getStyleClass().add("modal-window");

        return root;
    }

    private HBox createButtonsContainer() {
        SceneButton confirmButton = new SceneButton("Confirma", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));

//        SceneButton continueNoOrderButton = new SceneButton("Continua fara comanda", ACTION_TYPE.CONTINUATION);
//        continueNoOrderButton.setOnAction(event -> actionHandler.accept(continueNoOrderButton.getActionType()));

        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    private Node createOrdersSection() {
        VBox container = new VBox();

        Label infoLabel = new Label("Alegeti comanda pentru care introduceti produsul: " + model.getProduct().getName());

        ordersTableView = new TableView<>();
        ordersTableView.setPlaceholder(new Label("Nu exista comenzi."));
        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

//        TableColumn<Order, Boolean> isClosedColumn = new TableColumn<>();
//        isClosedColumn.setCellValueFactory(dataCell -> new SimpleObjectProperty<>(dataCell.getValue().isClosed()));
//        isClosedColumn.setCellFactory(column -> new TableCell<>() {
//            final FontIcon fontIcon = new FontIcon("mdi2l-lock");
//            @Override
//            protected void updateItem(Boolean item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty) {
//                    setText(null);
//                    setGraphic(null);
//                } else {
//                    setText(null);
//                    if(item) {
//                        setGraphic(fontIcon);
//                        setStyle("-fx-alignment: CENTER;");
//                    } else {
//                        setGraphic(null);
//                    }
//
//                }
//            }
//        });
//        ordersTableView.getColumns().add(isClosedColumn);

        TableColumn<Order, Integer> orderCounterColumn = new TableColumn<>("Nr");
        orderCounterColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCounter()));
        orderCounterColumn.setCellFactory(column -> new TableCell<>() {
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
        ordersTableView.getColumns().add(orderCounterColumn);

        TableColumn<Order, Timestamp> dateTimeColumn = new TableColumn<>("Plasata la");
        dateTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateScheduled()));
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

//        TableColumn<Order, String> productUnitMeasurementColumn = new TableColumn<>("UM");
//        productUnitMeasurementColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct().getUnitMeasurement()));
//        ordersTableView.getColumns().add(productUnitMeasurementColumn);
        orderCounterColumn.prefWidthProperty().set(100);

        ordersTableView.setItems(model.getOrders());
        ordersTableView.getStyleClass().add("main-table-view");
        ordersTableView.getSelectionModel().select(0);

        orderCounterColumn.prefWidthProperty().set(64);
        dateTimeColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        productNameColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.5).subtract(64));
        quantityColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        completedColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));
        remainderColumn.prefWidthProperty().bind(ordersTableView.widthProperty().multiply(0.125));

        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

        container.getChildren().addAll(infoLabel, ordersTableView);

        return container;
    }

    public Order getSelectedOrder() {
        return ordersTableView.getSelectionModel().getSelectedItem();
    }

}
