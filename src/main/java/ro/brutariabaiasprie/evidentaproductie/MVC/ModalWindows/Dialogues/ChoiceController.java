package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Map;
import java.util.Objects;

public class ChoiceController {
    private final Stage stage;
    private Object chosenOption;

    public ChoiceController(Stage owner, String windowTitle, String message, Map<Object, String> options){
        stage = new Stage();
        ChoiceView view = new ChoiceView(message, options, this::onWindowAction);

        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle(windowTitle);

        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    protected void onWindowAction(Object option) {
        this.chosenOption = option;
        stage.close();
    }

    public Object getChosenOption() {
        return chosenOption;
    }
}
