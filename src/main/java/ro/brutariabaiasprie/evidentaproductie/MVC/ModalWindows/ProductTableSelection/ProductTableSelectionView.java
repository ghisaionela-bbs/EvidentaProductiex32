package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductTableSelection;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class ProductTableSelectionView extends Parent implements Builder<Region> {
    private final ProductTableSelectionModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final Stage PARENT_STAGE;

    private TableView<ProductDTO> tableView;

    public ProductTableSelectionView(ProductTableSelectionModel model, Stage PARENT_STAGE, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.PARENT_STAGE = PARENT_STAGE;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setPrefSize(PARENT_STAGE.getWidth(), PARENT_STAGE.getHeight());
        tableView = createTableView();
        root.getChildren().addAll(tableView, createButtonsContainer());
        root.getStyleClass().add("modal-window");
        return root;
    }

    private HBox createButtonsContainer() {
        SceneButton confirmButton = new SceneButton("Confirma", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));

        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    private TableView<ProductDTO> createTableView() {
        TableView<ProductDTO> tableView = new TableView<>();

        tableView.setPlaceholder(new Label("Nu exista inregistrari."));

        TableColumn<ProductDTO, String> nameColumn = new TableColumn<>("Produs");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
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

        TableColumn<ProductDTO, String> unitMeasurementColumn = new TableColumn<>("UM");
        unitMeasurementColumn.setCellValueFactory(new PropertyValueFactory<>("unitMeasurement"));
        tableView.getColumns().add(unitMeasurementColumn);

        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 70);
        unitMeasurementColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);

        tableView.setItems(model.getProducts());
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return tableView;
    }

    public void selectFirst() {
        if(!model.getProducts().isEmpty()) {
            tableView.getSelectionModel().select(0);
        }
    }

    public ProductDTO getSelectedProduct() {
        return tableView.getSelectionModel().getSelectedItem();
    }

}
