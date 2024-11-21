package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductGroup;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User.UserModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User.UserView;

import java.util.Objects;

public class ProductGroupController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private ProductGroupModel model;
    private ProductGroupView view;

    public ProductGroupController(Stage owner) {
        initStage(owner, WINDOW_TYPE.ADD, new Group());
    }

    public ProductGroupController(Stage owner, WINDOW_TYPE type, Group group) throws RuntimeException {
        if(type == WINDOW_TYPE.ADD) {
            throw new RuntimeException("The WINDOW_TYPE cannot be ADD when an existing group is provided");
        }
        initStage(owner, type, group);
    }

    private void initStage(Stage owner, WINDOW_TYPE type, Group group) {
        this.type = type;
        this.stage = new Stage();
        this.model = new ProductGroupModel();
        this.model.setGroup(group);
        this.view = new ProductGroupView(this.model, type, this::onWindowAction);;
        this.view.setDeleteGroupHandler(this::deleteGroup);
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

    private void deleteGroup() {
        //TODO
    }


    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {

    }
}
