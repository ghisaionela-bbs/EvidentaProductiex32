package ro.brutariabaiasprie.evidentaproductie;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnController;
import ro.brutariabaiasprie.evidentaproductie.MVC.Production.ProductionController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.util.Objects;

public class SceneFactory {
    private SceneController controller;
    private Stage primaryStage;
    private Scene scene;
    private DBConnController dbConnController = new DBConnController(this::switchScene);

    private void switchScene(Runnable runnable, SceneType sceneType) {
        switch (sceneType) {
            case DBCONN:
                controller = new DBConnController(this::switchScene);
                break;
            case PRODUCTION:
                controller = new ProductionController(this::switchScene, primaryStage);
                break;
            case DASHBOARD:
                break;
        }
        primaryStage.getScene().setRoot(new ProductionController(this::switchScene, primaryStage).getView());
//        if(Platform.isFxApplicationThread()){
//            primaryStage.getScene().setRoot(productionController.getView());
//            runnable.run();}
//        else Platform.runLater(() -> {
//            primaryStage.getScene().setRoot(productionController.getView());
//            runnable.run();
//
//        });
    }


    public SceneFactory(Stage primaryStage) {
        this.primaryStage = primaryStage;
        scene = new Scene(dbConnController.getView());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

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
}