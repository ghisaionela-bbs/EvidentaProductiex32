package ro.brutariabaiasprie.evidentaproductie.MVC;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnController;
import ro.brutariabaiasprie.evidentaproductie.MVC.Production.ProductionController;

import java.util.Objects;

public class SceneFactory {
    private SceneController controller;
    private Stage primaryStage;
    private Scene scene;

    public SceneFactory(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createScene(SceneType.DBCONN);
    }

//    public void setScene() {
//        if(controller == null) {
//            createScene(SceneType.DBCONN);
//        }
//        primaryStage.setScene(new Scene(controller.getView()));
//    }

    private void createScene(SceneType sceneType) {
        switch (sceneType) {
            case DBCONN:
                controller = new DBConnController(this::switchScene);
                break;
            case PRODUCTION:
                controller = new ProductionController(this::switchScene);
                break;
            case DASHBOARD:
                break;
        }

        primaryStage.setScene(new Scene(Objects.requireNonNull(controller).getView()));
        System.out.println(getClass().getPackageName());
        primaryStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("..\\..\\..\\..\\..\\resources\\ro\\brutariabaiasprie\\evidentaproductie\\styles.css")).toExternalForm());
        primaryStage.show();
    }

    private void switchScene(Runnable runnable, SceneType sceneType) {
        if(Platform.isFxApplicationThread()){
            System.out.println("here");
            createScene(sceneType);
            runnable.run();}
        else Platform.runLater(() -> {
            System.out.println("oh no");
            runnable.run();
            createScene(sceneType);
        });
    }
}
