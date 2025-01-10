package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

/**
 * Widget for creating warning scenes
 */
public class WarningView extends Parent implements Builder<Region> {
    private final String message;
    private Consumer<ACTION_TYPE> actionHandler;

    public WarningView(String message, Consumer<ACTION_TYPE> actionHandler) {
        this.message = message;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Label lblMessage = new Label(message);
        SceneButton confirmButton = new SceneButton("OK", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));

        root.setSpacing(10);
        root.getChildren().addAll(lblMessage, confirmButton);
        root.getStyleClass().add("modal-window");

        return root;
    }
}
