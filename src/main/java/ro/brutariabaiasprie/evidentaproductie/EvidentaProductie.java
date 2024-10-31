package ro.brutariabaiasprie.evidentaproductie;

import javafx.application.Application;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneFactory;

/**
 * The main class of the javafx app.
 */
public class EvidentaProductie extends Application {
    /**
     * The main entry point for the javafx app.
     * @param stage - primary stage of the app, once it is closed, the application is closed
     * @throws Exception - when any runtime exception occurs
     */
    public void start(Stage stage) throws Exception {
        ConfigApp.check_config();
        SceneFactory sceneFactory = new SceneFactory(stage);
    }

    /**
     * Launches the javafx app.
     * @param args - main args
     */
    public static void main(String[] args) {
        launch();
    }
}