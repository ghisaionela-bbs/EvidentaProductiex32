package ro.brutariabaiasprie.evidentaproductie.MVC.Widgets;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Widget for creating a modal warning stage
 */
public class WarningController {
    public WarningController(Stage owner, String message) {
        Stage warningStage = new Stage();
        WarningView warningView = new WarningView(message,
                new Consumer<Runnable>() {
                    @Override
                    public void accept(Runnable runnable) {
                        warningStage.close();
                    }
                });
        Scene warningScene = new Scene(warningView.build());
        warningScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        warningStage.setTitle("Atentie!");
        warningStage.setScene(warningScene);
        warningStage.initOwner(owner);
        warningStage.initModality(Modality.APPLICATION_MODAL);
        warningStage.showAndWait();
    }

}
