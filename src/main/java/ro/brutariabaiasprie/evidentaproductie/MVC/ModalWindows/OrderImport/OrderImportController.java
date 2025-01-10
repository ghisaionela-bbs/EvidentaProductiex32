package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderImport;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.io.File;
import java.util.Objects;

public class OrderImportController extends ModalWindow {
    private final OrderImportModel model = new OrderImportModel();
    private OrderImportView view;
    private final Stage PARENT_STAGE;
    private final Stage stage;

    private enum STEP {
        CONFIG,
        PREVIEW,
    }
    private STEP currentStep = STEP.CONFIG;

    private Boolean SUCCESS = false;

    public OrderImportController(Stage owner) {
        this.PARENT_STAGE = owner;
        view = new OrderImportView(model, owner, this::onBrowseFileAction, this::onWindowAction);

        stage = new Stage();
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Importa din excel");
        stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
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
        }
    }

    private void continueToNextStep() {
        switch (currentStep) {
            case CONFIG:
                currentStep = STEP.PREVIEW;
                if(model.getFile() == null) {
                    new WarningController(PARENT_STAGE, "Selectati fisierul excel pentru a continua!");
                    return;
                }
                if(view.getProductNameColumn() == view.getQuantityColumn()) {
                    new WarningController(PARENT_STAGE, "Setati cate o coloana diferita pentru denumire, cantitate, data si ora!");
                    return;
                }
                if(view.getQuantityColumn() == view.getDateColumn()) {
                    new WarningController(PARENT_STAGE, "Setati cate o coloana diferita pentru denumire, cantitate, data si ora!");
                    return;
                }
                if(view.getDateColumn() == view.getTimeColumn()) {
                    new WarningController(PARENT_STAGE, "Setati cate o coloana diferita pentru denumire, cantitate, data si ora!");
                    return;
                }
                if(view.getProductNameColumn() == view.getTimeColumn()) {
                    new WarningController(PARENT_STAGE, "Setati cate o coloana diferita pentru denumire, cantitate, data si ora!");
                    return;
                }
                model.setSheetNumber(view.getSheetNumber());
                model.setStartRow(view.getStartingRow());
                model.setProdNameCol(view.getProductNameColumn());
                model.setQuantityCol(view.getQuantityColumn());
                model.setDateCol(view.getDateColumn());
                model.setTimeCol(view.getTimeColumn());
                model.readWorkbook();
                Platform.runLater(() -> view.switchScene(1));
                break;
            case PREVIEW:
                String validationErrors = model.validateData();
                if(!validationErrors.isEmpty()) {
                    String message = validationErrors + "\nCorectati erorile din excel si reincercati.";
                    new WarningController(PARENT_STAGE, message);
                    stage.close();
                } else {
                    Task<Void> taskDBInsert = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try{
                                model.insertData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
                             new WarningController(this.PARENT_STAGE, "Importul excel a esuat!");

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
