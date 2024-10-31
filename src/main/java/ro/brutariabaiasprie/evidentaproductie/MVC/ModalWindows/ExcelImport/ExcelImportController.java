package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;

import java.io.File;
import java.util.Objects;


public class ExcelImportController extends ModalWindow {
    private final ExcelImportModel model = new ExcelImportModel();
    private ExcelImportView view;
    private final Stage PARENT_STAGE;
    private final Stage stage;

    private enum STEP {
        CONFIG,
        PREVIEW,
    }
    private STEP currentStep = STEP.CONFIG;

    private Boolean SUCCESS = false;

    public ExcelImportController(Stage owner) {
        this.PARENT_STAGE = owner;
        view = new ExcelImportView(model, owner, this::onBrowseFileAction, this::onWindowAction);

        stage = new Stage();
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Importa din excel");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    }

    public Boolean getSUCCESS() {
        return SUCCESS;
    }

    private void onBrowseFileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Alege fisierul excel pentru import");
        if(model.getFile() == null) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {
            fileChooser.setInitialDirectory(new File(model.getFile().getParent()));
        }
        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("All files", "*.*"),
                new FileChooser.ExtensionFilter("XLSX", "*.xlsx","xls")
        );
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            model.setFilename(file.getName());
            model.setFile(file);
            model.readWorkbook();
        }
    }

    private void continueToNextStep() {
        switch (currentStep) {
            case CONFIG:
                currentStep = STEP.PREVIEW;
                if(model.getFile() == null) {
                    WarningController warningController = new WarningController(PARENT_STAGE, "Selectati fisierul excel pentru a continua!");
                    return;
                }
                if(view.getProductNameColumn() == view.getUnitMeasurementColumn()) {
                    WarningController warningController = new WarningController(PARENT_STAGE, "Coloana pentru denumire si cea pentru unitatea de masura trebuie sa fie diferite una de alta!");
                    return;
                }
                Platform.runLater(() -> view.switchScene(1));
                break;
            case PREVIEW:
                String validationErrors = model.validateData(view.getStartingRow(), view.getProductNameColumn(), view.getUnitMeasurementColumn());
                if(!validationErrors.isEmpty()) {
                    String message = validationErrors + "\nCorectati erorile din excel si reincercati.";
                    WarningController warningController = new WarningController(PARENT_STAGE, message);
                    stage.close();
                } else {
                    Task<Void> taskDBInsert = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            model.insertData(view.getStartingRow(), view.getProductNameColumn(), view.getUnitMeasurementColumn());
                            return null;
                        }
                    };
                    taskDBInsert.setOnSucceeded(evt -> {
                        SUCCESS = true;
                        stage.close();
                    });
                    taskDBInsert.setOnFailed(evt -> {
                        stage.close();
                        Platform.runLater(() -> {
                            WarningController warning = new WarningController(this.PARENT_STAGE, "Importul excel a esuat!");

                        });
                    });
                    Thread dbTaskThread = new Thread(taskDBInsert);
                    dbTaskThread.start();
                }
                break;
        }
    }

    private void returnToPreviousStep() {
        switch (currentStep) {
            case CONFIG:
                break;
            case PREVIEW:
                currentStep = STEP.CONFIG;
                Platform.runLater(() -> view.switchScene(-1));
                break;
        }
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        switch (actionType) {
            case CONFIRMATION:
                continueToNextStep();
                break;
            case RETURN:
                returnToPreviousStep();
                break;
            case CANCELLATION:
                stage.close();
                break;
        }
    }

}
