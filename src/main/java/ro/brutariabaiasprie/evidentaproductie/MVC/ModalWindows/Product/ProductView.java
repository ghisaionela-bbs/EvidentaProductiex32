package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ro.brutariabaiasprie.evidentaproductie.DTO.GroupDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

/***
 * The Product view class used by the ProductController to create the scene
 * Will show the product information and will get the user input to be passed back to the ProductController
 */
public class ProductView extends Parent implements Builder<Region> {
    private final ProductModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final WINDOW_TYPE type;

    private Runnable deleteProductHandler;

    private TextArea productNameTextArea;
    private ToggleGroup unitMeasurementGroup;
    private ComboBox<Group> groupComboBox;

    /***
     * Constructs the ProductView instance
     * @param model the ProductModel contained in the ProductController
     * @param type the WINDOW_TYPE, can be EDIT, ADD or VIEW. The components in the view will be created depending on it.
     * @param actionHandler the handler for the SceneButtons
     */
    public ProductView(ProductModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.type = type;
    }

    /***
     * Builds the view for the ProductController
     */
    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setCenter(createContentSection());
        root.setBottom(createSceneButtons());
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        return root;

    }

    /***
     * Creates the product information section with possibility of editing depending on the WINDOW_TYPE type.
     * @return GridPane with the product information
     */
    private GridPane createContentSection() {
        //  Title
        Label productIdLabel  = new Label();
        // Name
        Label productNameLabel = new Label("Denumire:");
        productNameTextArea = new TextArea();
        productNameTextArea.setPrefSize(400, 200);
        productNameTextArea.setWrapText(true);
        // Unit of measurement
        Label unitMeasurementLabel = new Label("Unitatea de masura:");
        unitMeasurementGroup = new ToggleGroup();
        RadioButton kgRadioButton = new RadioButton("KG");
        kgRadioButton.setUserData("KG");
        kgRadioButton.setToggleGroup(unitMeasurementGroup);
        RadioButton bucRadioButton = new RadioButton("BUC");
        bucRadioButton.setUserData("BUC");
        bucRadioButton.setToggleGroup(unitMeasurementGroup);
        HBox unitMeasurementChoiceBox = new HBox(kgRadioButton, bucRadioButton);
        unitMeasurementChoiceBox.setSpacing(8);
        // Group
        Label groupLabel = new Label("Grupa:");
        groupComboBox = new ComboBox<>();
        Callback<ListView<Group>, ListCell<Group>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Group> call(ListView<Group> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Selectati grupa");
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };
        groupComboBox.setCellFactory(cellFactory);
        groupComboBox.setButtonCell(cellFactory.call(null));
        groupComboBox.setItems(model.getGroups());
        groupComboBox.setMaxWidth(Double.MAX_VALUE);

        // Setting up the container
        GridPane gridPane = new GridPane();
        for (int i = 0 ; i < gridPane.getRowCount(); i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(row);
        }
        for (int j = 0 ; j < gridPane.getColumnCount(); j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }
        gridPane.getStyleClass().add("grid-form");

        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                productIdLabel.setText("Adaugare produs nou");
                unitMeasurementGroup.selectToggle(kgRadioButton);
                break;
            case VIEW:
                productNameTextArea.setDisable(true);
                unitMeasurementChoiceBox.setDisable(true);
                groupComboBox.setDisable(true);
            case EDIT:
                productIdLabel.setText("Produs " + model.getProduct().getId());
                productNameTextArea.setText(model.getProduct().getName());
                if(model.getProduct().getUnitMeasurement().equals("KG")) {
                    unitMeasurementGroup.selectToggle(kgRadioButton);
                } else if (model.getProduct().getUnitMeasurement().equals("BUC")) {
                    unitMeasurementGroup.selectToggle(bucRadioButton);
                }
                if(model.getProduct().getGroup() != null) {
                    groupComboBox.getSelectionModel().select(model.getProduct().getGroup());
                }
                break;
        }

        // Adding the controls
        gridPane.add(productIdLabel, 0, 0);
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteProductHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            gridPane.add(deleteButton, 1, 0);
        }
        gridPane.add(productNameLabel, 0, 1, 2, 1);
        gridPane.add(productNameTextArea, 0, 2, 2, 1);
        gridPane.add(unitMeasurementLabel, 0, 3, 2, 1);
        gridPane.add(unitMeasurementChoiceBox, 0, 4, 2, 1);
        gridPane.add(groupLabel, 0, 5);
        gridPane.add(groupComboBox, 0, 6, 2, 1);

        return gridPane;
    }

    /***
     * Creates a section which contains the main buttons of the scene.
     * The actions that can be made are CONFIRMATION and CANCELLATION
     * @return a container for the main buttons of the scene
     */
    private HBox createSceneButtons() {
        SceneButton confirmButton = new SceneButton("OK", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    /***
     * Sets the handler for the delete button for deleting the current product
     * @param deleteProductHandler handler for deleting the current product
     */
    public void setDeleteProductHandler(Runnable deleteProductHandler) {
        this.deleteProductHandler = deleteProductHandler;
    }

    public String getName() {
        return productNameTextArea.getText().trim();
    }

    public String getUnitMeasurement() {
        return (String) unitMeasurementGroup.getSelectedToggle().getUserData();
    }

    public Group getGroup() {
        return groupComboBox.getValue();
    }
}
