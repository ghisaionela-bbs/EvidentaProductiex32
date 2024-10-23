package ro.brutariabaiasprie.evidentaproductie.MVC.Widgets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.util.function.Consumer;

/**
 * Widget for creating warning scenes
 */
public class WarningView extends Parent implements Builder<Region> {
    private final String message;
    private Consumer<Runnable> actionHandler;

    public WarningView(String message, Consumer<Runnable> actionHandler) {
        this.message = message;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);

        Label lblMessage = new Label(message);
        Button btnOk = new Button("OK");
        btnOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actionHandler.accept(() -> {});
            }
        });

        container.setSpacing(10);
        container.getChildren().addAll(lblMessage, btnOk);

        return container;
    }
}
