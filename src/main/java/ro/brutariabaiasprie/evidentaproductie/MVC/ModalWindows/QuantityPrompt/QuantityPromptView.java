package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.QuantityPrompt;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuantityPromptView extends Parent implements Builder<Region> {
    private final QuantityPromptModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private TextField quantityTextField;

    public QuantityPromptView(QuantityPromptModel model, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();

        Label promptText = new Label("Introduceti cantitatea pentru produsul\n" + model.getProduct().getName());

        HBox promptContainer = new HBox(createInputPrompt(), new Label(model.getProduct().getUnitMeasurement()));
        promptContainer.setSpacing(4);
        promptContainer.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(promptText, promptContainer, createWindowActionButtons());
        root.getStyleClass().add("modal-window");
        return root;
    }

    private Node createInputPrompt() {
        quantityTextField = new TextField();
        quantityTextField.setPromptText("-.--");
        quantityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (newValue != null && !newValue.isEmpty()) {
                    String filteredValue = newValue.replaceAll(" ", "");
                    Pattern pattern = Pattern.compile("(\\d{1,10}\\.\\d{1,2}|\\d{1,10}\\.|\\.\\d{1,2}|\\d{1,10})");
                    Matcher matcher = pattern.matcher(filteredValue);
                    if (matcher.find()) {
                        quantityTextField.setText(matcher.group(0));
                    } else {
                        quantityTextField.clear();
                    }
                } else {
                    quantityTextField.clear();
                }
            }
        });
        HBox.setHgrow(quantityTextField, Priority.ALWAYS);
        return quantityTextField;
    }

    private Node createWindowActionButtons() {
        HBox buttonsContainer = new HBox();
        SceneButton continueButton = new SceneButton("Continua", ACTION_TYPE.CONFIRMATION);
        continueButton.setOnAction(event -> actionHandler.accept(continueButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        buttonsContainer.getChildren().addAll(continueButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(buttonsContainer, Priority.ALWAYS);
        return buttonsContainer;
    }

    public String getQuantity() {
        return quantityTextField.getText();
    }
}
