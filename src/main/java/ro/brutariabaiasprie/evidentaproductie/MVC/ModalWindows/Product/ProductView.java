package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

/***
 * The Product view class used by the ProductController to create the scene
 * Will show the product information and will get the user input to be passed back to the ProductController
 */
public class ProductView extends Parent implements Builder<Region> {
    private final ProductModel model;
    private final WINDOW_TYPE type;
    private final Consumer<ACTION_TYPE> actionHandler;

    private Runnable deleteProductHandler;

    private TextArea productNameTextArea;
    private ToggleGroup unitMeasurementGroup;
    final ComboBox<Group> groupComboBox = new ComboBox<>();
    final ComboBox<Group> subgroupComboBox = new ComboBox<>();

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
        root.setMinWidth(500);
        root.setCenter(createContentSection());
        root.setBottom(createSceneButtons());
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        root.getStyleClass().add("entry-view");
        return root;

    }

    /***
     * Creates the product information section with possibility of editing depending on the WINDOW_TYPE type.
     * @return GridPane with the product information
     */
    private GridPane createContentSection() {
        //  Title
        Label productIdLabel  = new Label();
        productIdLabel.getStyleClass().add("title");

        // Name section
        Label productNameLabel = new Label("Denumire:");
        productNameTextArea = new TextArea();
        productNameTextArea.setPrefSize(250, 100);
        productNameTextArea.setWrapText(true);
        VBox productNameSection = new VBox(productNameLabel, productNameTextArea);
        productNameSection.getStyleClass().add("section");
        productNameSection.getStyleClass().add("vbox-layout");

        // Unit of measurement section
        Label unitMeasurementLabel = new Label("Unitatea de masura:");
        unitMeasurementGroup = new ToggleGroup();
        RadioButton kgRadioButton = new RadioButton("KG");
        kgRadioButton.setUserData("KG");
        kgRadioButton.setToggleGroup(unitMeasurementGroup);
        RadioButton bucRadioButton = new RadioButton("BUC");
        bucRadioButton.setUserData("BUC");
        bucRadioButton.setToggleGroup(unitMeasurementGroup);
        HBox unitMeasurementChoiceBox = new HBox(kgRadioButton, bucRadioButton);
        unitMeasurementChoiceBox.setSpacing(16);
        VBox unitMeasurementSection = new VBox(unitMeasurementLabel, unitMeasurementChoiceBox);
        unitMeasurementSection.getStyleClass().add("section");
        unitMeasurementSection.getStyleClass().add("vbox-layout");

        // User group section
        Label groupLabel = new Label("Grupa:");
        Callback<ListView<Group>, ListCell<Group>> groupCellFactory = new Callback<>() {
            @Override
            public ListCell<Group> call(ListView<Group> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            if(item.getId() == 0) {
                                setText("Fara grupa");
                            } else {
                                setText(item.getName());
                            }
                        }
                    }
                };
            }
        };
        groupComboBox.setCellFactory(groupCellFactory);
        groupComboBox.setButtonCell(groupCellFactory.call(null));
        groupComboBox.setItems(model.getGroups());
        groupComboBox.setMaxWidth(Double.MAX_VALUE);
        groupComboBox.setPromptText("Selectati grupa");
        VBox groupSection = new VBox(groupLabel, groupComboBox);
        groupSection.getStyleClass().add("section");
        groupSection.getStyleClass().add("vbox-layout");

        Label subgroupLabel = new Label("Subgrupa:");
        Callback<ListView<Group>, ListCell<Group>> subgroupCellFactory = new Callback<>() {
            @Override
            public ListCell<Group> call(ListView<Group> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Fara subgrupa");
                        } else {
                            if(item.getId() == 0) {
                                setText("Fara subgrupa");
                            } else {
                                setText(item.getName());

                            }
                        }
                    }
                };
            }
        };
        subgroupComboBox.setCellFactory(subgroupCellFactory);
        subgroupComboBox.setButtonCell(subgroupCellFactory.call(null));
        subgroupComboBox.setItems(model.getSubgroups());
        subgroupComboBox.setMaxWidth(Double.MAX_VALUE);
        subgroupComboBox.setPromptText("Selectati subgrupa");
        subgroupComboBox.setDisable(true);
        groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldGroup, newGroup) -> {
            if(oldGroup != newGroup) {
                subgroupComboBox.getSelectionModel().clearSelection();
            }
        });
        VBox subgroupSection = new VBox(subgroupLabel, subgroupComboBox);
        subgroupSection.getStyleClass().add("section");
        subgroupSection.getStyleClass().add("vbox-layout");

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
                    if(model.getProduct().getGroup().getId() != 0) {
                        groupComboBox.getSelectionModel().select(model.getProduct().getGroup());
                    }
                }
                if(model.getProduct().getSubgroupId() != 0) {
                    subgroupComboBox.getSelectionModel().select(model.getSubgroup(model.getProduct().getSubgroupId()));
                }
                break;
        }

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        gridPane.add(productIdLabel, 0, 0);
        // Adding the controls
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteProductHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            gridPane.add(deleteButton, 2, 0);
        }
        gridPane.add(productNameSection, 0, 1, 3, 1);
        gridPane.add(unitMeasurementSection, 0, 2, 3, 1);
        gridPane.add(groupSection, 0, 3, 3, 1);
        gridPane.add(subgroupSection, 0, 4, 3, 1);
        // adding constraints
//        for (int i = 0 ; i < gridPane.getRowCount(); i++) {
//            RowConstraints row = new RowConstraints();
//            row.setVgrow(Priority.ALWAYS);
//            gridPane.getRowConstraints().add(row);
//        }
        for (int j = 0 ; j < gridPane.getColumnCount(); j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }

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

    public int getSubgroup() {
        if(subgroupComboBox.getValue() == null) {
            return 0;
        }
        return subgroupComboBox.getValue().getId();
    }

    public void loadProductData() {
        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                break;
            case VIEW:
                groupComboBox.setDisable(true);
                subgroupComboBox.setDisable(true);
            case EDIT:
//                if(model.getProduct().getGroup() != null) {
//                    if(model.getProduct().getGroup().getId() != 0) {
//                        groupComboBox.getSelectionModel().select(model.getGroup(model.getUser().getGroupId()));
//                    }
//                }
                if(model.getProduct().getSubgroupId() != 0) {
                    subgroupComboBox.getSelectionModel().select(model.getSubgroup(model.getProduct().getSubgroupId()));
                }
                break;
        }
    }
}
