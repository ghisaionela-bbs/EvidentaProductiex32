package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Builder;

import ro.brutariabaiasprie.evidentaproductie.DTO.OrderDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderResultsDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class OrderAssociationView extends Parent implements Builder<Region> {
    private final OrderAssociationModel model;
    private final Consumer<ACTION_TYPE> actionHandler;

    private TableView<OrderResultsDTO> orderTableView;

    public OrderAssociationView(OrderAssociationModel model, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        Node ordersSection = createOrdersSection();

        root.getChildren().addAll(ordersSection, createButtonsContainer());
        root.getStyleClass().add("modal-window");
        return root;
    }

    private HBox createButtonsContainer() {
        SceneButton confirmButton = new SceneButton("Confirma", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));

        SceneButton continueNoOrderButton = new SceneButton("Continua fara comanda", ACTION_TYPE.CONTINUATION);
        continueNoOrderButton.setOnAction(event -> actionHandler.accept(continueNoOrderButton.getActionType()));

        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, continueNoOrderButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    private Node createOrdersSection() {
        VBox container = new VBox();

        Label infoLabel = new Label("Alegeti comanda pentru care introduceti produsul: " + model.getProduct().getName());

        orderTableView = new TableView<>();
        TableColumn<OrderResultsDTO, Integer> orderIDColumn = new TableColumn<>("Comanda");
        orderIDColumn.setCellValueFactory(new PropertyValueFactory<>("ORDER_ID"));
        orderTableView.getColumns().add(orderIDColumn);

        TableColumn<OrderResultsDTO, Timestamp> dateAndTimeColumn = new TableColumn<>("Data si ora");
        dateAndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateAndTime"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateAndTimeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });
        orderTableView.getColumns().add(dateAndTimeColumn);

        TableColumn<OrderResultsDTO, Double> quantityColumn = new TableColumn<>("Cantitate");
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
        orderTableView.getColumns().add(quantityColumn);

        TableColumn<OrderResultsDTO, String> unitMeasurementColumn = new TableColumn<>("UM");
        unitMeasurementColumn.setCellValueFactory(new PropertyValueFactory<>("unitMeasurement"));
        orderTableView.getColumns().add(unitMeasurementColumn);

        orderTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        orderIDColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        dateAndTimeColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 30 );
        quantityColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        unitMeasurementColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 10 );

        orderTableView.setItems(model.getOrderSearchResults());
        orderTableView.getSelectionModel().select(0);

        container.getChildren().addAll(infoLabel, orderTableView);

        return container;
    }

    public OrderResultsDTO getSelectedOrder() {
        return orderTableView.getSelectionModel().getSelectedItem();
    }

}
