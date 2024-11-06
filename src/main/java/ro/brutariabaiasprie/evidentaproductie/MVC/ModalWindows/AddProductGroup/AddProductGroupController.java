package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddProductGroup;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport.ExcelExportView;

import java.util.Objects;

public class AddProductGroupController {
    private Stage stage;
    private AddProductGroupModel model;
    private AddProductGroupView view;


    public AddProductGroupController(Stage owner) {
        stage = new Stage();
        model = new AddProductGroupModel();
        view = new AddProductGroupView(model);
        Scene scene = new Scene(view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        stage.setTitle("Adauga o grupa de produs");
        Image icon16x16 = new Image("app-icon-16x16.png");
        Image icon32x32 = new Image("app-icon-32x32.png");
        Image icon64x64 = new Image("app-icon-64x64.png");
        stage.getIcons().addAll(icon16x16, icon32x32, icon64x64);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
