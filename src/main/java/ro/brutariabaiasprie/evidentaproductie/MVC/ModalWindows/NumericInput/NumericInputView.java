package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.NumericInput;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericInputView extends Parent implements Builder<Region> {
    private final Consumer<ACTION_TYPE> actionHandler;
    private final String defaultValue;

    private TextField quantityTextField;

    public NumericInputView(Consumer<ACTION_TYPE> actionHandler, String defaultValue) {
        this.actionHandler = actionHandler;
        this.defaultValue = defaultValue;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        quantityTextField = createQuantityField();
        GridPane numpad = createNumpad();
        VBox.setVgrow(numpad, Priority.ALWAYS);
        VBox quantitySection = new VBox(new Label("Cantitate:"), quantityTextField, numpad);
        quantitySection.setSpacing(8);
        root.setCenter(quantitySection);
        root.setBottom(createSceneButtons());
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        root.getStyleClass().add("entry-view");
        return root;
    }

    private TextField createQuantityField() {
        TextField quantityTextField = new TextField();
        quantityTextField.promptTextProperty().set("");

        quantityTextField.textProperty().set(defaultValue);

        quantityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue != null && !newValue.isEmpty()) {
                    String filteredValue = newValue.replaceAll(" ", "");
                    Pattern pattern = Pattern.compile("(\\d{1,10}\\.\\d{1,2}|\\d{1,10}\\.|\\.\\d{1,2}|\\d{1,10})");
                    Matcher matcher = pattern.matcher(filteredValue);
                    if (matcher.find())
                    {
                        quantityTextField.setText(matcher.group(0));
                    } else {
                        quantityTextField.clear();
                    }
                } else {
                    quantityTextField.clear();
                }
            }
        });
        quantityTextField.getStyleClass().add("txt-fld-quantity");
        quantityTextField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return quantityTextField;
    }

    private GridPane createNumpad() {
        GridPane numpad = new GridPane();
        Button num1 = new Button("1");
        num1.setOnAction(handleBtnNumpadOnAction());
        Button num2 = new Button("2");
        num2.setOnAction(handleBtnNumpadOnAction());
        Button num3 = new Button("3");
        num3.setOnAction(handleBtnNumpadOnAction());
        Button num4 = new Button("4");
        num4.setOnAction(handleBtnNumpadOnAction());
        Button num5 = new Button("5");
        num5.setOnAction(handleBtnNumpadOnAction());
        Button num6 = new Button("6");
        num6.setOnAction(handleBtnNumpadOnAction());
        Button num7 = new Button("7");
        num7.setOnAction(handleBtnNumpadOnAction());
        Button num8 = new Button("8");
        num8.setOnAction(handleBtnNumpadOnAction());
        Button num9 = new Button("9");
        num9.setOnAction(handleBtnNumpadOnAction());
        Button num0 = new Button("0");
        num0.setOnAction(handleBtnNumpadOnAction());
        Button numDot = new Button(".");
        numDot.setOnAction(handleBtnNumpadOnAction());
        Button numDel = new Button("⌫");
        numDel.setOnAction(handleBtnNumpadOnAction());

        numpad.add(num1, 0, 0);
        GridPane.setVgrow(num1, Priority.ALWAYS);
        GridPane.setHgrow(num1, Priority.ALWAYS);
        GridPane.setFillWidth(num1, true);
        GridPane.setFillHeight(num1, true);
        num1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num2, 1, 0);
        GridPane.setVgrow(num2, Priority.ALWAYS);
        GridPane.setHgrow(num2, Priority.ALWAYS);
        GridPane.setFillWidth(num2, true);
        GridPane.setFillHeight(num2, true);
        num2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num3, 2, 0);
        GridPane.setVgrow(num3, Priority.ALWAYS);
        GridPane.setHgrow(num3, Priority.ALWAYS);
        GridPane.setFillWidth(num3, true);
        GridPane.setFillHeight(num3, true);
        num3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num4, 0, 1);
        GridPane.setVgrow(num4, Priority.ALWAYS);
        GridPane.setHgrow(num4, Priority.ALWAYS);
        GridPane.setFillWidth(num4, true);
        GridPane.setFillHeight(num4, true);
        num4.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num5, 1, 1);
        GridPane.setVgrow(num5, Priority.ALWAYS);
        GridPane.setHgrow(num5, Priority.ALWAYS);
        GridPane.setFillWidth(num5, true);
        GridPane.setFillHeight(num5, true);
        num5.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num6, 2, 1);
        GridPane.setVgrow(num6, Priority.ALWAYS);
        GridPane.setHgrow(num6, Priority.ALWAYS);
        GridPane.setFillWidth(num6, true);
        GridPane.setFillHeight(num6, true);
        num6.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num7, 0, 2);
        GridPane.setVgrow(num7, Priority.ALWAYS);
        GridPane.setHgrow(num7, Priority.ALWAYS);
        GridPane.setFillWidth(num7, true);
        GridPane.setFillHeight(num7, true);
        num7.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num8, 1, 2);
        GridPane.setVgrow(num8, Priority.ALWAYS);
        GridPane.setHgrow(num8, Priority.ALWAYS);
        GridPane.setFillWidth(num8, true);
        GridPane.setFillHeight(num8, true);
        num8.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num9, 2, 2);
        GridPane.setVgrow(num9, Priority.ALWAYS);
        GridPane.setHgrow(num9, Priority.ALWAYS);
        GridPane.setFillWidth(num9, true);
        GridPane.setFillHeight(num9, true);
        num9.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(num0, 0, 3);
        GridPane.setVgrow(num0, Priority.ALWAYS);
        GridPane.setHgrow(num0, Priority.ALWAYS);
        GridPane.setFillWidth(num0, true);
        GridPane.setFillHeight(num0, true);
        num0.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(numDot, 1, 3);
        GridPane.setVgrow(numDot, Priority.ALWAYS);
        GridPane.setHgrow(numDot, Priority.ALWAYS);
        GridPane.setFillWidth(numDot, true);
        GridPane.setFillHeight(numDot, true);
        numDot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.add(numDel, 2, 3);
        GridPane.setVgrow(numDel, Priority.ALWAYS);
        GridPane.setHgrow(numDel, Priority.ALWAYS);
        GridPane.setFillWidth(numDel, true);
        GridPane.setFillHeight(numDel, true);
        numDel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        numpad.getStyleClass().add("numpad");
        GridPane.setHgrow(numpad, Priority.ALWAYS);
        GridPane.setVgrow(numpad, Priority.ALWAYS);
        return numpad;
    }

    private EventHandler<ActionEvent> handleBtnNumpadOnAction() {
        return new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                Button node = (Button) event.getSource() ;
                String value = node.getText();
                //Handle warning screen for no product selected
                if("0123456789.".contains(value)) {
                    String quantity = quantityTextField.getText();
                    quantityTextField.setText(quantity + value);
                } else if ("⌫".equals(value)) {
                    String quantity = quantityTextField.getText();
                    if (quantity.isEmpty()){
                        return;
                    }
                    quantityTextField.setText(quantity.substring(0, quantity.length() - 1));
                }
            }
        };
    }

    private HBox createSceneButtons() {
        SceneButton confirmButton = new SceneButton("OK", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);
        return buttonsContainer;
    }

    public String getInput() {
        return quantityTextField.getText();
    }

    public void setQuantity(String defaultValue) {
        quantityTextField.textProperty().set(defaultValue);
    }
}
