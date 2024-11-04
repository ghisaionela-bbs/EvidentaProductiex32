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
    private final Consumer<Boolean> noOrderHandler;
    private final Stage PARENT_STAGE;
    private final Stage stage;

    private StackPane body;
    private Node loadingSection;
    private Node ordersSection;
    private TableView<OrderResultsDTO> orderTableView;

    public OrderAssociationView(OrderAssociationModel model, Stage stage, Stage PARENT_STAGE, Consumer<ACTION_TYPE> actionHandler, Consumer<Boolean> noOrderHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.PARENT_STAGE = PARENT_STAGE;
        this.noOrderHandler = noOrderHandler;
        this.stage = stage;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setPrefSize(PARENT_STAGE.getWidth() * 0.5, PARENT_STAGE.getHeight() * 0.5);

        loadingSection = createLoadingSection();
        ordersSection = createOrdersSection();

        body = new StackPane(createOrdersSection(), loadingSection);
        VBox.setVgrow(body, Priority.ALWAYS);

        root.getChildren().addAll(body, createButtonsContainer());
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

    private Node createLoadingSection() {
        Label loadingSectionLabel = new Label("Va rugam asteptati.\nSe cauta comenzi pentru produsul " + model.getProduct().getName());
        loadingSectionLabel.setAlignment(Pos.CENTER);
        loadingSectionLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        loadingSectionLabel.setStyle("-fx-background-color: COLOR_WHITE;");
        return loadingSectionLabel;
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

        container.getChildren().addAll(infoLabel, orderTableView);

        return container;
    }

    public void showOrderResults() {
        body.getChildren().remove(loadingSection);
        if(!model.getOrderSearchResults().isEmpty()) {
            orderTableView.getSelectionModel().select(0);
        }
    }

    public OrderResultsDTO getSelectedOrder() {
        if (body.getChildren().contains(loadingSection)) {
            return null;
        }
        return orderTableView.getSelectionModel().getSelectedItem();
    }

    public void showNoOrderFoundWindow() {
        ConfirmationController confirmation = new ConfirmationController(stage, "Atentie!",
                "Nu s-au gasit comenzi pentru produsul: " + model.getProduct().getName() +
                "\nDoriti sa introduceti inregistrari pentru acest produs fara comanda?");
        noOrderHandler.accept(confirmation.isSUCCESS());
    }

}
