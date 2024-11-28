package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class UserController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private UserModel model;
    private UserView view;

    public UserController(Stage owner) {
        initStage(owner, WINDOW_TYPE.ADD, new User());
    }

    public UserController(Stage owner, WINDOW_TYPE type, User user) throws RuntimeException {
        if(type == WINDOW_TYPE.ADD) {
            throw new RuntimeException("The WINDOW_TYPE cannot be ADD when an existing product is provided");
        }
        initStage(owner, type, user);
    }

    private void initStage(Stage owner, WINDOW_TYPE type, User user) {
        this.type = type;
        this.stage = new Stage();
        this.model = new UserModel();
        this.model.setUser(user);
        this.view = new UserView(this.model, type, this::onWindowAction);
        this.view.setDeleteUserHandler(this::deleteUser);
        this.view.groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(oldValue != newValue) {
                if(newValue == null) {
                    this.view.subgroupComboBox.getSelectionModel().clearSelection();
                    this.view.subgroupComboBox.setDisable(true);
                } else if(newValue.getId() == 0) {
                    this.view.subgroupComboBox.setDisable(true);
                } else {
                    this.view.subgroupComboBox.setDisable(false);
                }
                model.loadSubgroups(newValue);
            }
        });
        this.runDatabaseTask(() -> {
            model.loadGroups();
            view.loadUserData();
        });
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        switch (type) {
            case ADD:
                stage.setTitle("Adaugare produs");
                break;
            case EDIT:
                stage.setTitle("Editare produs");
                break;
            case VIEW:
                stage.setTitle("Vizualizare produs");
                break;
        }

        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    private void deleteUser() {
        if(new ConfirmationController(stage, "Confirmati stergerea",
                String.format("Sunteti sigur ca doriti sa stergeti utilizatorul %s?", model.getUser().getUsername())).isSUCCESS()) {
            model.deleteUser();
            stage.close();
        }
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(!isValidInput()) {
                return;
            }

            model.getUser().setUsername(view.getUsername().trim());
            model.getUser().setPassword(view.getPassword().trim());
            model.getUser().setRoleId(view.getRoleId());
            model.getUser().setGroupId(view.getGroupId());
            model.getUser().setSubgroupId(view.getSubgroupId());

            if(type == WINDOW_TYPE.ADD) {
                model.addUser();
            } else {
                model.updateUser();
            }
        }
        stage.close();
    }

    private boolean isValidInput() {
        if(view.getUsername().trim().isEmpty()) {
            new WarningController(stage, "Numele de utilizator nu poate sa ramana gol!");
            return false;
        }
        if(view.getPassword().trim().isEmpty()) {
            new WarningController(stage, "Parola nu poate sa ramana goala!");
            return false;
        }
        if(view.getRoleId() == 0) {
            new WarningController(stage, "Rolul utilizatorului nu poate ramane gol!");
            return false;
        }
        return true;

    }
}
