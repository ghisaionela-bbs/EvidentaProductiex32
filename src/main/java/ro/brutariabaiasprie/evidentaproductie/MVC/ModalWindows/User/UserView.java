package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.UserRole;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class UserView extends Parent implements Builder<Region> {
    private final UserModel model;
    private final WINDOW_TYPE type;
    private final Consumer<ACTION_TYPE> actionHandler;

    private Runnable deleteUserHandler;

    private TextArea usernameTextArea;
    private TextArea passwordTextArea;
    private ComboBox<UserRole> roleComboBox;
    private ComboBox<Group> groupComboBox;

    public UserView(UserModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.type = type;
        this.actionHandler = actionHandler;
    }

    public void setDeleteUserHandler(Runnable deleteUserHandler) {
        this.deleteUserHandler = deleteUserHandler;
    }

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

    private Node createContentSection() {
        //  Title
        Label productIdLabel  = new Label();
        // Username
        Label usernameLabel = new Label("Nume de utilizator:");
        usernameTextArea = new TextArea();
        usernameTextArea.setPrefSize(400, 200);
        usernameTextArea.setWrapText(true);
        // Password
        Label passwordLabel = new Label("Parola:");
        passwordTextArea = new TextArea();
        passwordTextArea.setPrefSize(400, 200);
        usernameTextArea.setWrapText(true);
        // Role
        Label roleLabel = new Label("Rol:");
        roleComboBox = new ComboBox<>();
        Callback<ListView<UserRole>, ListCell<UserRole>> roleCellFactory = new Callback<ListView<UserRole>, ListCell<UserRole>>() {
            @Override
            public ListCell<UserRole> call(ListView<UserRole> userRoleListView) {
                return null;
            }
        };
        // Group
        Label groupLabel = new Label("Grupa:");
        groupComboBox = new ComboBox<>();
        Callback<ListView<Group>, ListCell<Group>> groupCellFactory = new Callback<>() {
            @Override
            public ListCell<Group> call(ListView<Group> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Selectati grupa");
                        } else {
                            if(item.getId() == 0) {
                                setText(null);
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

//        // Setting up the values and properties of controls
//        switch (type) {
//            case ADD:
//                productIdLabel.setText("Adaugare produs nou");
//                unitMeasurementGroup.selectToggle(kgRadioButton);
//                break;
//            case VIEW:
//                productNameTextArea.setDisable(true);
//                unitMeasurementChoiceBox.setDisable(true);
//                groupComboBox.setDisable(true);
//            case EDIT:
//                productIdLabel.setText("Produs " + model.getProduct().getId());
//                productNameTextArea.setText(model.getProduct().getName());
//                if(model.getProduct().getUnitMeasurement().equals("KG")) {
//                    unitMeasurementGroup.selectToggle(kgRadioButton);
//                } else if (model.getProduct().getUnitMeasurement().equals("BUC")) {
//                    unitMeasurementGroup.selectToggle(bucRadioButton);
//                }
//                if(model.getProduct().getGroup() != null) {
//                    if(model.getProduct().getGroup().getId() != 0) {
//                        groupComboBox.getSelectionModel().select(model.getProduct().getGroup());
//                    }
//                }
//                break;
//        }

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        gridPane.add(productIdLabel, 0, 0);
        // Adding the controls
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteUserHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            gridPane.add(deleteButton, 1, 0);
        }
        gridPane.add(usernameLabel, 0, 1, 2, 1);
        gridPane.add(usernameTextArea, 0, 2, 2, 1);
        gridPane.add(passwordLabel, 0, 3, 2, 1);
        gridPane.add(passwordTextArea, 0, 4, 2, 1);
        gridPane.add(groupLabel, 0, 5);
        gridPane.add(groupComboBox, 0, 6, 2, 1);
        // adding constraints
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

        return gridPane;
    }

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
}
