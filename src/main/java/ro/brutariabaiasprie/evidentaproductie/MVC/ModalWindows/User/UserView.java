package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACCESS_LEVEL;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
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

    private TextField usernameTextField;
    private PasswordField passwordField;
    private ComboBox<UserRole> roleComboBox = new ComboBox<>();
    final ComboBox<Group> groupComboBox = new ComboBox<>();
    final ComboBox<Group> subgroupComboBox = new ComboBox<>();

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
        root.getStyleClass().add("entry-view");
        return root;
    }

    private Node createContentSection() {
        //  Title
        Label userIdLabel  = new Label();
        userIdLabel.getStyleClass().add("title");
        userIdLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(userIdLabel, Priority.ALWAYS);
        HBox titleContainer = new HBox(userIdLabel);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        // Username section
        Label usernameLabel = new Label("Nume de utilizator:");
        usernameTextField = new TextField();
        usernameTextField.setPrefWidth(250);
        VBox usernameSection = new VBox(usernameLabel, usernameTextField);
        usernameSection.getStyleClass().add("section");
        usernameSection.getStyleClass().add("vbox-layout");

        // Password section
        Label passwordLabel = new Label("Parola:");
        passwordField = new PasswordField();
        TextField passwordTextField  = new TextField();
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        StackPane passwordStackPane = new StackPane(passwordTextField, passwordField);

        ToggleButton passwordVisibilityToggle = new ToggleButton();
        FontIcon eyeOpenedIcon = new FontIcon("mdi2e-eye");
        FontIcon eyeClosedIcon = new FontIcon("mdi2e-eye-off");
        passwordVisibilityToggle.setGraphic(eyeOpenedIcon);
        passwordVisibilityToggle.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue) {
                passwordVisibilityToggle.setGraphic(eyeClosedIcon);
            } else {
                passwordVisibilityToggle.setGraphic(eyeOpenedIcon);
            }
        }));
        passwordField.visibleProperty().bind(passwordVisibilityToggle.selectedProperty().not());
        passwordVisibilityToggle.getStyleClass().add("toggle");
        HBox.setHgrow(passwordStackPane, Priority.ALWAYS);
        HBox passwordComponent = new HBox(passwordStackPane, passwordVisibilityToggle);
        passwordComponent.setAlignment(Pos.CENTER_LEFT);
        passwordComponent.getStyleClass().add("password-component");
        passwordComponent.getStyleClass().add("vbox-layout");

        VBox passwordSection = new VBox(passwordLabel, passwordComponent);
        passwordSection.setPrefWidth(250);
        passwordSection.getStyleClass().add("section");
        passwordSection.getStyleClass().add("vbox-layout");

        // Role section
        Label roleLabel = new Label("Rol utilizator:");
        roleLabel.setMaxWidth(Double.MAX_VALUE);
        Callback<ListView<UserRole>, ListCell<UserRole>> roleCellFactory = new Callback<>() {
            @Override
            public ListCell<UserRole> call(ListView<UserRole> userRoleListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(UserRole item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Fara rol");
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        };
        roleComboBox.setItems(FXCollections.observableArrayList());
        roleComboBox.setButtonCell(roleCellFactory.call(null));
        roleComboBox.setCellFactory(roleCellFactory);
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setPromptText("Selectati rolul");
        roleComboBox.setItems(model.getRoles());
        VBox roleSection = new VBox(roleLabel, roleComboBox);
        roleSection.getStyleClass().add("section");
        roleSection.getStyleClass().add("vbox-layout");

        // User group section
        Label userGroupLabel = new Label("Grupa produse:");
        userGroupLabel.setMaxWidth(Double.MAX_VALUE);
        Callback<ListView<Group>, ListCell<Group>> userGroupCellFactory = new Callback<>() {
            @Override
            public ListCell<Group> call(ListView<Group> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("Fara grupa");
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
        groupComboBox.setItems(model.getGroups());
        groupComboBox.setButtonCell(userGroupCellFactory.call(null));
        groupComboBox.setCellFactory(userGroupCellFactory);
        groupComboBox.setMaxWidth(Double.MAX_VALUE);
        groupComboBox.setPromptText("Selectati grupa");
        VBox groupSection = new VBox(userGroupLabel, groupComboBox);
        groupSection.getStyleClass().add("section");
        groupSection.getStyleClass().add("vbox-layout");
        // Product group section
        Label productGroupLabel = new Label("Grupa produse:");
        productGroupLabel.setMaxWidth(Double.MAX_VALUE);
        subgroupComboBox.setDisable(true);
        groupComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if(newValue == null) {
                subgroupComboBox.setDisable(true);
            } else {
                subgroupComboBox.setDisable(false);
            }
        });
        Callback<ListView<Group>, ListCell<Group>> productGroupCellFactory = new Callback<>() {
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
        subgroupComboBox.setCellFactory(productGroupCellFactory);
        subgroupComboBox.setButtonCell(productGroupCellFactory.call(null));
        subgroupComboBox.setItems(model.getGroups());
        subgroupComboBox.setMaxWidth(Double.MAX_VALUE);
        subgroupComboBox.setItems(model.getSubgroups());
        subgroupComboBox.setPromptText("Selectati subgrupa");
        VBox productGroupSection = new VBox(productGroupLabel, subgroupComboBox);
        productGroupSection.getStyleClass().add("section");
        productGroupSection.getStyleClass().add("vbox-layout");

        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                userIdLabel.setText("Adaugare utilizator nou");
                break;
            case VIEW:
                usernameTextField.setDisable(true);
                passwordField.setDisable(true);
                roleComboBox.setDisable(true);
                groupComboBox.setDisable(true);
                subgroupComboBox.setDisable(true);
            case EDIT:
                userIdLabel.setText("Utilizator " + model.getUser().getId());
                usernameTextField.setText(model.getUser().getUsername());
                passwordField.setText(model.getUser().getPassword());
                if(model.getUser().getRoleId() != 0) {
                    roleComboBox.getSelectionModel().select(new UserRole(ACCESS_LEVEL.values()[model.getUser().getRoleId()]));
                }
                if(model.getUser().getGroupId() != 0) {
                    groupComboBox.getSelectionModel().select(model.getGroup(model.getUser().getGroupId()));
                }
                if(model.getUser().getSubgroupId() != 0) {
                    subgroupComboBox.getSelectionModel().select(model.getSubgroup(model.getUser().getSubgroupId()));
                }
                break;
        }

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        // Adding the controls
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteUserHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            titleContainer.getChildren().add(deleteButton);
        }
        gridPane.add(titleContainer, 0, 0, 2, 1);
        gridPane.add(usernameSection, 0, 1);
        gridPane.add(passwordSection, 1, 1);
        gridPane.add(roleSection, 0, 2, 2, 1);
        gridPane.add(groupSection, 0, 3, 2, 1);
        gridPane.add(productGroupSection, 0, 4, 2, 1);
        gridPane.getStyleClass().add("grid-layout");

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

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public int getRoleId() {
        if (roleComboBox.getSelectionModel().getSelectedItem() == null) {
            return 0;
        }
        return roleComboBox.getSelectionModel().getSelectedItem().getAccessLevel().getValue();
    }

    public int getGroupId() {
        if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
            return 0;
        }
        return groupComboBox.getSelectionModel().getSelectedItem().getId();
    }

    public int getSubgroupId() {
        if (subgroupComboBox.getSelectionModel().getSelectedItem() == null) {
            return 0;
        }
        return subgroupComboBox.getSelectionModel().getSelectedItem().getId();
    }

    public void loadUserData() {
        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                break;
            case VIEW:
                usernameTextField.setDisable(true);
                passwordField.setDisable(true);
                roleComboBox.setDisable(true);
                groupComboBox.setDisable(true);
                subgroupComboBox.setDisable(true);
            case EDIT:
                usernameTextField.setText(model.getUser().getUsername());
                passwordField.setText(model.getUser().getPassword());
                if(model.getUser().getRoleId() != 0) {
                    roleComboBox.getSelectionModel().select(new UserRole(ACCESS_LEVEL.values()[model.getUser().getRoleId()]));
                }
                if(model.getUser().getGroupId() != 0) {
                    groupComboBox.getSelectionModel().select(model.getGroup(model.getUser().getGroupId()));
                }
                if(model.getUser().getSubgroupId() != 0) {
                    subgroupComboBox.getSelectionModel().select(model.getSubgroup(model.getUser().getSubgroupId()));
                }
                break;
        }
    }

}
