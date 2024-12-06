package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.NumericInput;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class NumericInputController extends ModalWindow {
    private NumericInputView view;
    private Stage stage;
    private boolean SUCCESS = false;

    public NumericInputController(Stage owner) {
        initStage(owner, 0.00);
    }

    public NumericInputController(Stage owner, Double defaultValue) {
        initStage(owner, defaultValue);
    }

    private void initStage(Stage owner, Double defaultValue) {
        this.stage = new Stage();
        String numericFieldValue = "";
        if (defaultValue != 0) {
            numericFieldValue = defaultValue.toString();
        }
        this.view = new NumericInputView(this::onWindowAction, numericFieldValue);
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());
        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setTitle("Introduceti cantitatea");
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }


    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if (actionType == ACTION_TYPE.CONFIRMATION) {
            if(view.getInput() == null) {
                new WarningController(stage, "Introduceti cantitatea!");
                return;
            }
            if(view.getInput().isEmpty()) {
                new WarningController(stage, "Introduceti cantitatea!");
                return;
            }
            SUCCESS = true;
        }
        stage.close();
    }

    public boolean isSUCCESS() {
        return SUCCESS;
    }

    public String getInput() {
        return view.getInput();
    }

}
