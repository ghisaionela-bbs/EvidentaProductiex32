package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;

import java.util.Objects;

public class ExcelExportController extends ModalWindow {
    private final ExcelExportModel model = new ExcelExportModel();
    private final ExcelExportView view;
    private final Stage stage;
    private final Stage PARENT_STAGE;

    public ExcelExportController(Stage owner) {
        PARENT_STAGE = owner;
        stage = new Stage();
        view = new ExcelExportView(this::onWindowAction);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Exporta in excel");
        stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            Task<Void> taskDBSelect = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        model.export(view.getFromDateValue(), view.getToDateValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            taskDBSelect.setOnSucceeded(evt -> stage.close());
            taskDBSelect.setOnFailed(evt -> {
                stage.close();
                Platform.runLater(() -> new WarningController(this.PARENT_STAGE, "Exportul excel a esuat!"));
            });
            Thread dbTaskThread = new Thread(taskDBSelect);
            dbTaskThread.start();
        } else {
            stage.close();
        }
    }

}
