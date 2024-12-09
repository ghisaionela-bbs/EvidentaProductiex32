package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.*;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.time.LocalDate;
import java.util.function.Consumer;

public class ExcelExportView extends Parent implements Builder<Region> {
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private final Consumer<ACTION_TYPE> actionHandler;

    private final Spinner<Integer> fromHourStartSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> fromMinuteStartSpinner = new Spinner<>(0, 59, 0);
    private final Spinner<Integer> fromHourEndSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> fromMinuteEndSpinner = new Spinner<>(0, 59, 0);

    public ExcelExportView(Consumer<ACTION_TYPE> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-conn");

        Label lblFromDate = new Label("Din:");
        fromDatePicker = new DatePicker(LocalDate.now());

        Button clearFromDateButton = new Button("\uD83D\uDDD9");
        clearFromDateButton.setOnAction(event -> fromDatePicker.setValue(null));

        Label lblToDate = new Label("Pana in:");
        toDatePicker = new DatePicker(LocalDate.now());

        Button clearToDateButton = new Button("\uD83D\uDDD9");
        clearToDateButton.setOnAction(event -> toDatePicker.setValue(null));

        Label hourLabel = new Label("Interval orar:");
        VBox fromTimeStartSection = new VBox(hourLabel, new HBox(fromHourStartSpinner, fromMinuteStartSpinner));
        fromTimeStartSection.getStyleClass().add("section");
        fromTimeStartSection.getStyleClass().add("vbox-layout");
        VBox fromTimeEndSection = new VBox(hourLabel, new HBox(fromHourEndSpinner, fromMinuteEndSpinner));
        fromTimeEndSection.getStyleClass().add("section");
        fromTimeEndSection.getStyleClass().add("vbox-layout");


        gridPane.add(lblFromDate, 0, 0);
        gridPane.add(fromDatePicker, 1, 0);
        gridPane.add(clearFromDateButton, 2, 0);
        gridPane.add(fromTimeStartSection, 0, 1, 3, 1);
        gridPane.add(fromTimeEndSection, 0, 2, 3, 1);
        gridPane.add(lblToDate, 0, 3);
        gridPane.add(toDatePicker, 1, 3);
        gridPane.add(clearToDateButton, 2, 3);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(60);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(-1), col2, new ColumnConstraints(-1));

        SceneButton confirmButton = new SceneButton("OK", ACTION_TYPE.CONFIRMATION);
        confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));
        SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
        cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

        HBox buttonsContainer = new HBox(confirmButton, cancelButton);
        buttonsContainer.setSpacing(8);
        buttonsContainer.setAlignment(Pos.CENTER);

        root.setSpacing(16);
        root.getChildren().addAll(gridPane, buttonsContainer);
        root.getStyleClass().add("modal-window");

        return root;
    }

    public LocalDate getFromDateValue() {
        return fromDatePicker.getValue();
    }

    public LocalDate getToDateValue() {
        return toDatePicker.getValue();
    }
}
