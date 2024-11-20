package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.User;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product.ProductModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product.ProductView;

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
        this.runDatabaseTask(model::loadGroups);
        this.view = new UserView(this.model, type, this::onWindowAction);;
        this.view.setDeleteUserHandler(this::deleteUser);
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

    private void runDatabaseTask(Runnable runnable) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
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

    }
}
