package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;
import java.time.ZonedDateTime;

public class EvidentaProductie extends Application {
    private static Scene scene;
    Connection connection;
    FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("database-connection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        DatabaseConnectionController databaseConnectionController = fxmlLoader.getController();
        databaseConnectionController.setController(stage);
        stage.setTitle("Evidenta productie Brutaria Baia Sprie");
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