package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class EvidentaProductie extends Application {
    private static Scene scene;
    Connection connection;
    FXMLLoader fxmlLoader;
    public static Map<String, Scene> sceneMap = new Hashtable<>();

//    public void loadScenes(){
//        sceneMap.put("database-connection.fxml")
//    }

    @Override
    public void start(Stage stage) throws IOException {
        // get screensize of monitor
//        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();


        FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("database-connection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        DatabaseConnectionController databaseConnectionController = fxmlLoader.getController();
        databaseConnectionController.setController(stage);
        stage.setTitle("Evidenta productie Brutaria Baia Sprie");
        String css = Objects.requireNonNull(this.getClass().getResource("stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    if(databaseConnectionController.connection != null) {
                        databaseConnectionController.connection.close();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}