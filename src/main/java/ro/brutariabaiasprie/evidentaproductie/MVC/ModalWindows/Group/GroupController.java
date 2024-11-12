package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group;

import javafx.collections.MapChangeListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.Group;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class GroupController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private GroupModel model;
    private GroupView view;

    public GroupController(Stage owner, WINDOW_TYPE type) {
        initStage(owner, type, new Group());
    }

    public GroupController(Stage owner, WINDOW_TYPE type, Group group) {
        initStage(owner, type, group);
    }

    private void initStage(Stage owner, WINDOW_TYPE type, Group group) {
        this.type = type;
        this.stage = new Stage();
        this.model = new GroupModel();
        this.model.setGroup(group);
        this.view = new GroupView(this.model, type, this::onWindowAction);
        this.view.setDeleteGroupHandler(this::deleteGroup);
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        switch (type) {
            case ADD:
                stage.setTitle("Adaugare grupa");
                break;
            case EDIT:
                stage.setTitle("Editare grupa");
                break;
            case VIEW:
                stage.setTitle("Vizualizare grupa");
                break;
        }

        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(type == WINDOW_TYPE.ADD) {
                model.addGroup(view.getName());
            } else if (type == WINDOW_TYPE.EDIT) {
                model.getGroup().setName(view.getName());
                model.updateGroup();
            }
        }
        stage.close();
    }

    private void deleteGroup() {
        if(new ConfirmationController(stage, "Confirmati stergerea",
                String.format("Sunteti sigur ca doriti sa stergeti grupa %s?", model.getGroup().getName())).isSUCCESS()) {
            model.deleteGroup();
            stage.close();
        }
    }
}
