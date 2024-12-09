package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.*;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.time.LocalDate;
import java.util.function.Consumer;

public class OrderExportView extends Parent implements Builder<Region> {
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private final Consumer<ACTION_TYPE> actionHandler;

    private final Spinner<Integer> fromHourStartSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> fromMinuteStartSpinner = new Spinner<>(0, 59, 0);
    private final Spinner<Integer> fromHourEndSpinner = new Spinner<>(0, 23, 23);
    private final Spinner<Integer> fromMinuteEndSpinner = new Spinner<>(0, 59, 59);

    private final Spinner<Integer> toHourStartSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> toMinuteStartSpinner = new Spinner<>(0, 59, 0);
    private final Spinner<Integer> toHourEndSpinner = new Spinner<>(0, 23, 23);
    private final Spinner<Integer> toMinuteEndSpinner = new Spinner<>(0, 59, 59);

    public OrderExportView(Consumer<ACTION_TYPE> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();

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

//        HBox fromTimeStartSection = new HBox(new Label("De la:"), fromHourStartSpinner, fromMinuteStartSpinner);
//        fromTimeStartSection.setAlignment(Pos.CENTER_RIGHT);
//        fromTimeStartSection.setSpacing(8);
//        HBox fromTimeEndSection = new HBox(new Label("La:"), fromHourEndSpinner, fromMinuteEndSpinner);
//        fromTimeEndSection.setAlignment(Pos.CENTER_RIGHT);
//        fromTimeEndSection.setSpacing(8);
//        VBox fromTimeSection = new VBox(hourLabel,
//                fromTimeStartSection,
//                fromTimeEndSection);
//        fromTimeStartSection.setSpacing(8);
        double spinnerPrefWidth = 100;

        GridPane fromTimeSection = new GridPane();
        fromTimeSection.add(new FontIcon("mdi2c-clock-start"), 0, 0);
        fromTimeSection.add(fromHourStartSpinner, 1, 0);
        fromHourStartSpinner.setPrefWidth(spinnerPrefWidth);
        fromTimeSection.add(fromMinuteStartSpinner, 2, 0);
        fromMinuteStartSpinner.setPrefWidth(spinnerPrefWidth);
        fromTimeSection.add(new FontIcon("mdi2c-clock-end"), 0, 1);
        fromTimeSection.add(fromHourEndSpinner, 1, 1);
        fromHourEndSpinner.setPrefWidth(spinnerPrefWidth);
        fromTimeSection.add(fromMinuteEndSpinner, 2, 1);
        fromMinuteEndSpinner.setPrefWidth(spinnerPrefWidth);
        fromTimeSection.setHgap(8);
        fromTimeSection.setVgap(4);
        fromTimeSection.getStyleClass().add("section");
        fromTimeSection.getStyleClass().add("vbox-layout");

        GridPane toTimeSection = new GridPane();
        toTimeSection.add(new FontIcon("mdi2c-clock-start"), 0, 0);
        toTimeSection.add(toHourStartSpinner, 1, 0);
        toHourStartSpinner.setPrefWidth(spinnerPrefWidth);
        toTimeSection.add(toMinuteStartSpinner, 2, 0);
        toMinuteStartSpinner.setPrefWidth(spinnerPrefWidth);
        toTimeSection.add(new FontIcon("mdi2c-clock-end"), 0, 1);
        toTimeSection.add(toHourEndSpinner, 1, 1);
        toHourEndSpinner.setPrefWidth(spinnerPrefWidth);
        toTimeSection.add(toMinuteEndSpinner, 2, 1);
        toMinuteEndSpinner.setPrefWidth(spinnerPrefWidth);
        toTimeSection.setHgap(8);
        toTimeSection.setVgap(4);
        toTimeSection.getStyleClass().add("section");
        toTimeSection.getStyleClass().add("vbox-layout");

        gridPane.add(lblFromDate, 0, 0);
        gridPane.add(fromDatePicker, 1, 0);
        gridPane.add(clearFromDateButton, 2, 0);
        gridPane.add(fromTimeSection, 0, 1, 3, 1);
        gridPane.add(lblToDate, 0, 2);
        gridPane.add(toDatePicker, 1, 2);
        gridPane.add(clearToDateButton, 2, 2);
        gridPane.add(toTimeSection, 0, 3, 3, 1);


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

        root.setCenter(gridPane);
        root.setBottom(buttonsContainer);
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        root.getStyleClass().add("entry-view");
        return root;
    }

    public LocalDate getFromDateValue() {
        return fromDatePicker.getValue();
    }

    public LocalDate getToDateValue() {
        return toDatePicker.getValue();
    }
}
