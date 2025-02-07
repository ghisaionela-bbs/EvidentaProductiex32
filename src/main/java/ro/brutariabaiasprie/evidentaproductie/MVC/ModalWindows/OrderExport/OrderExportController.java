package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class OrderExportController extends ModalWindow {
    private final OrderExportModel model = new OrderExportModel();
    private final OrderExportView view;
    private final Stage stage;
    private final Stage PARENT_STAGE;

    public OrderExportController(Stage owner) {
        PARENT_STAGE = owner;
        stage = new Stage();
        view = new OrderExportView(this.model, this::onWindowAction, this::updateSubgroups);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        Platform.runLater( () -> {
            model.loadGroups();
            view.setGroupFilter();
            model.loadSubgroups(FXCollections.observableArrayList());
            view.setSubgroupFilter();
        });

        stage.setTitle("Exporta in excel");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void updateSubgroups() {
        ObservableList<Group> checkedGroups  = view.getCheckedGroups();
        Platform.runLater( () ->{model.loadSubgroups(checkedGroups);});
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            Task<Void> taskDBSelect = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        model.setDateFrom(view.getFromDateValue());
                        model.setDateTo(view.getToDateValue());
                        model.setTimeStart(view.getTimeStartValue());
                        model.setTimeEnd(view.getTimeEndValue());
                        model.setCheckedGroups(view.getCheckedGroups());
                        model.setCheckedSubgroups(view.getCheckedSubgroups());
                        model.export();
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
