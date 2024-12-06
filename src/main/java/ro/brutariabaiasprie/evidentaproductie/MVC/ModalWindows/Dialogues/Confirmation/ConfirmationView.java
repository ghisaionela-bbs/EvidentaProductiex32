package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Confirmation;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class ConfirmationView extends Parent implements Builder<Region> {
    private String message;
    private Consumer<ACTION_TYPE> actionHandler;

    public ConfirmationView(String message, Consumer<ACTION_TYPE> actionHandler) {
        this.message = message;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        HBox buttonsContainer = createButtonsContainter();

        root.setSpacing(10);
        root.getChildren().addAll(messageLabel, buttonsContainer);
        root.getStyleClass().add("modal-window");
        return root;
    }

    private HBox createButtonsContainter() {
        SceneButton confirmButton = new SceneButton("Confirma", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }
}
