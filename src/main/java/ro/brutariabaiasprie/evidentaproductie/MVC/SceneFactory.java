package ro.brutariabaiasprie.evidentaproductie.MVC;

import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnController;
import ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.MainWindowController;
import ro.brutariabaiasprie.evidentaproductie.MVC.Manager.ManagerController;
import ro.brutariabaiasprie.evidentaproductie.MVC.Production.ProductionController;
import ro.brutariabaiasprie.evidentaproductie.MVC.UserConn.UserConnController;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.util.Objects;

/**
 * Handles content of primary stage, creates scenes and switches between scenes
 */
public class SceneFactory {
    private SceneController controller;
    private Stage primaryStage;
    private Scene scene;
//    private DBConnController dbConnController = new DBConnController(primaryStage, this::switchScene);

    /**
     * Constructs a scene factory instance
     * @param primaryStage - the primary stage of the application
     */
    public SceneFactory(Stage primaryStage) {
        this.primaryStage = primaryStage;
        scene = new Scene(new DBConnController(primaryStage, this::switchScene).getView());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());
        primaryStage.setTitle("Evidenta Productie");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if(DBConnectionService.getConnection() != null) {
                    DBConnectionService.close();
                }
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
        primaryStage.setMaximized(true);
    }

    /**
     * Creates and switches current scene with a new one
     * @param runnable - post GUI action
     * @param sceneType - key for determining which kind of scene will be created
     */
    private void switchScene(Runnable runnable, SceneType sceneType) {
        switch (sceneType) {
            case DBCONN:
                controller = new DBConnController(primaryStage, this::switchScene);
                break;
            case PRODUCTION:
                controller = new ProductionController(primaryStage, this::switchScene);
                break;
            case USERCONN:
                controller = new UserConnController(primaryStage, this::switchScene);
                break;
            case MANAGER:
                controller = new ManagerController(primaryStage);
                break;
            case MAIN_WINDOW:
                controller = new MainWindowController(primaryStage, this::disconnect);
                break;
            case DEFAULT:
                int user_role = ((User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name())).getID_ROLE();
                if(user_role == 1) {
                    controller = new ProductionController(primaryStage, this::switchScene);
                } else {
                    controller = new ProductionController(primaryStage, this::switchScene);
                }
        }

        primaryStage.getScene().setRoot(controller.getView());
        runnable.run();
//        if(Platform.isFxApplicationThread()){
//            primaryStage.getScene().setRoot(productionController.getView());
//            runnable.run();}
//        else Platform.runLater(() -> {
//            primaryStage.getScene().setRoot(productionController.getView());
//            runnable.run();
//
//        });


    }

    private Runnable disconnect() {
        ConfigApp.deleteConfig(CONFIG_KEY.APPUSER.name());
        ConfigApp.write_config();
        primaryStage.getScene().setRoot(new UserConnController(primaryStage, this::switchScene).getView());
        return null;
    }

//    private void disconnect(SceneType sceneType) {
//        ConfigApp.deleteConfig(ConfigApp);
//    }
}