package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddNewOrder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderItemDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class AddNewOrderView extends Parent implements Builder<Region> {
    private final AddNewOrderModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final Consumer<ProductDTO> addProductToOrderActionHandler;
    private final Consumer<OrderItemDTO> deleteOrderItemHandler;
    private BorderPane root;
    private final Stage stage;
    private final Stage PARENT_STAGE;

    public AddNewOrderView(AddNewOrderModel model,
                           Stage stage,
                           Stage PARENT_STAGE,
                           Consumer<ACTION_TYPE> actionHandler,
                           Consumer<ProductDTO> addProductToOrderActionHandler,
                           Consumer<OrderItemDTO> deleteOrderItemHandler) {
        this.model = model;
        this.stage = stage;
        this.actionHandler = actionHandler;
        this.addProductToOrderActionHandler = addProductToOrderActionHandler;
        this.PARENT_STAGE = PARENT_STAGE;
        this.deleteOrderItemHandler = deleteOrderItemHandler;
    }

    @Override
    public Region build() {
        root = new BorderPane();
        root.setPrefSize(PARENT_STAGE.getHeight() * 0.5, PARENT_STAGE.getWidth() * 0.5);

        root.setCenter(createProductsSection());
        VBox orderDetailsSection = createOrderDetailsSection();
        orderDetailsSection.prefWidthProperty().bind(stage.widthProperty().divide(4));

        root.setRight(createOrderDetailsSection());
        root.getStyleClass().add("place-order-view");

        return root;
    }

    private Node createProductsSection() {
        TilePane tilePane = new TilePane();
        tilePane.setMaxWidth(Region.USE_PREF_SIZE);
        tilePane.getStyleClass().add("tile-pane");
        for(int i = 0; i < model.getProducts().size(); i ++) {
            final ProductDTO productDTO = model.getProducts().get(i);
            Button tileButton = new Button(productDTO.getName() + "\n" + productDTO.getUnitMeasurement());
            tileButton.setWrapText(true);
            tileButton.setPrefWidth(250);
            tileButton.setMaxHeight(Double.MAX_VALUE);
            tileButton.setOnAction(event -> addProductToOrderActionHandler.accept(productDTO));
            tileButton.getStyleClass().add("product-tile");
            tilePane.getChildren().add(tileButton);
        }
        tilePane.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(new StackPane(tilePane));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }

    private VBox createOrderDetailsSection() {
        VBox container = new VBox();
        container.getStyleClass().add("order-details");

        Label orderInfo = new Label("Detalii comanda:");
        orderInfo.getStyleClass().add("section-title");

        SceneButton placeOrderButton = new SceneButton("Plaseaza comanda", ACTION_TYPE.CONFIRMATION);
        placeOrderButton.setMaxWidth(Double.MAX_VALUE);
        placeOrderButton.getStyleClass().add("filled-button");
        placeOrderButton.getStyleClass().add("padded-button");
        placeOrderButton.setOnAction(event-> actionHandler.accept(placeOrderButton.getActionType()));

        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.getStyleClass().add("filled-button");
        cancelButton.getStyleClass().add("secondary");
        cancelButton.getStyleClass().add("padded-button");
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));
        container.getChildren().addAll(orderInfo, createOrderItemsListView(), placeOrderButton, cancelButton);

        return container;
    }

    private Node createOrderItemsListView() {
        ListView<OrderItemDTO> orderItemsListView = new ListView<>();
        VBox.setVgrow(orderItemsListView, Priority.ALWAYS);
        orderItemsListView.prefWidthProperty().bind(root.widthProperty().divide(4));
        orderItemsListView.setItems(model.getOrderItems());
        orderItemsListView.setCellFactory(new Callback<ListView<OrderItemDTO>, ListCell<OrderItemDTO>>() {
            @Override
            public ListCell<OrderItemDTO> call(ListView<OrderItemDTO> orderItemDTOListView) {
                return new ListCell<>() {
                    {
                        setPrefWidth(0);
                    }
                    @Override
                    protected void updateItem(OrderItemDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Text productNameText = new Text(item.getProductName());
                            TextFlow productNameTextFlow = new TextFlow(productNameText);

                            Label quantityLabel = new Label(String.format("%.2f", item.getQuantity()));
                            Label unitMeasurementLabel = new Label(item.getUnitMeasurement());

//                            Button editButton = new Button();
//                            editButton.setGraphic(new FontIcon("mdi2s-square-edit-outline"));
//                            editButton.getStyleClass().add("filled-button");

                            Button deleteButton = new Button();
                            deleteButton.setGraphic(new FontIcon("mdi2d-delete"));
                            deleteButton.getStyleClass().add("filled-button");
                            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    deleteOrderItemHandler.accept(item);
                                }
                            });

                            FlowPane flowPane = new FlowPane(quantityLabel, unitMeasurementLabel, deleteButton);
                            flowPane.setAlignment(Pos.CENTER_RIGHT);
                            flowPane.setHgap(10);

                            VBox container = new VBox(productNameTextFlow, flowPane);

                            setText(null);
                            setGraphic(container);
                        }
                    }
                };
            }
        });

        return orderItemsListView;
    }

}
