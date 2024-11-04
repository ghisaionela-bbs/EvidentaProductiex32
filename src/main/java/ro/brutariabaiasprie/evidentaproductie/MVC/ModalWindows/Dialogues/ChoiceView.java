package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public class ChoiceView extends Parent implements Builder<Region> {
    private String message;
    private Consumer<Object> actionHandler;
    private Map<Object, String> options;

    public ChoiceView(String message, Map<Object, String> options, Consumer<Object> actionHandler) {
        this.message = message;
        this.options = options;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);

        root.getChildren().addAll(messageLabel, createButtonsContainter());
        root.setSpacing(10);
        root.getStyleClass().add("modal-window");
        return root;
    }

    private FlowPane createButtonsContainter() {
        FlowPane buttonsContainer = new FlowPane();
        buttonsContainer.setHgap(8);
        buttonsContainer.setVgap(8);
        buttonsContainer.setAlignment(Pos.CENTER);

        for (var option : options.entrySet()) {
            SceneButton button = new SceneButton(option.getValue(), ACTION_TYPE.CONFIRMATION);
            button.setOnAction(event -> actionHandler.accept(option.getKey()));
            buttonsContainer.getChildren().add(button);
        }
        return buttonsContainer;
    }
}