package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

public class OrderExportView extends Parent implements Builder<Region> {
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private final Consumer<ACTION_TYPE> actionHandler;

    private final Spinner<Integer> hourStartSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> minuteStartSpinner = new Spinner<>(0, 59, 0);
    private final Spinner<Integer> hourEndSpinner = new Spinner<>(0, 23, 23);
    private final Spinner<Integer> minuteEndSpinner = new Spinner<>(0, 59, 59);

    public OrderExportView(Consumer<ACTION_TYPE> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();

        Label titleLabel  = new Label("Export excel comenzi si realizari");
        titleLabel.getStyleClass().add("title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-conn");

        Label lblFromDate = new Label("Din:");
        lblFromDate.minWidth(200);
        lblFromDate.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        fromDatePicker = new DatePicker(LocalDate.now());
        fromDatePicker.setShowWeekNumbers(false);

        Button clearFromDateButton = new Button();
        clearFromDateButton.setGraphic(new FontIcon("mdi2c-close"));
        clearFromDateButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clearFromDateButton.setOnAction(event -> fromDatePicker.setValue(null));
        clearFromDateButton.getStyleClass().add("filled-button");

        Label lblToDate = new Label("Pana in:");
        lblToDate.minWidth(300);
        GridPane.setHgrow(lblToDate, Priority.ALWAYS);
        toDatePicker = new DatePicker(LocalDate.now());
        toDatePicker.setShowWeekNumbers(false);

        Button clearToDateButton = new Button();
        clearToDateButton.setGraphic(new FontIcon("mdi2c-close"));
        clearToDateButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clearToDateButton.setOnAction(event -> toDatePicker.setValue(null));
        clearToDateButton.getStyleClass().add("filled-button");

        GridPane dateSection = new GridPane();
        dateSection.getStyleClass().add("grid-conn");
        dateSection.add(lblFromDate, 0,0);
        dateSection.add(fromDatePicker, 1, 0);
        dateSection.add(clearFromDateButton, 2, 0);
        dateSection.add(lblToDate, 0, 1);
        dateSection.add(toDatePicker, 1, 1);
        dateSection.add(clearToDateButton, 2, 1);
        dateSection.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints colDateLabel = new ColumnConstraints();
        colDateLabel.setHgrow(Priority.ALWAYS);
        colDateLabel.setMinWidth(100);
        dateSection.getColumnConstraints().add(colDateLabel);

        double spinnerPrefWidth = 100;
        GridPane timeSpinnerContainer = new GridPane();
        FontIcon startTimeIcon = new FontIcon("mdi2c-clock-start");
        startTimeIcon.getStyleClass().add("standalone-icon");
        timeSpinnerContainer.add(startTimeIcon, 0, 0);
        timeSpinnerContainer.add(hourStartSpinner, 1, 0);
        hourStartSpinner.setPrefWidth(spinnerPrefWidth);
        timeSpinnerContainer.add(minuteStartSpinner, 2, 0);
        minuteStartSpinner.setPrefWidth(spinnerPrefWidth);
        FontIcon endTimeIcon = new FontIcon("mdi2c-clock-end");
        endTimeIcon.getStyleClass().add("standalone-icon");
        timeSpinnerContainer.add(endTimeIcon, 0, 1);
        timeSpinnerContainer.add(hourEndSpinner, 1, 1);
        hourEndSpinner.setPrefWidth(spinnerPrefWidth);
        timeSpinnerContainer.add(minuteEndSpinner, 2, 1);
        minuteEndSpinner.setPrefWidth(spinnerPrefWidth);
        timeSpinnerContainer.setHgap(8);
        timeSpinnerContainer.setVgap(4);
        VBox timeSection = new VBox(new Label("Interval orar:"), timeSpinnerContainer);
        timeSection.getStyleClass().add("section");
        timeSection.getStyleClass().add("vbox-layout");

        gridPane.add(titleLabel, 0, 0);
        gridPane.add(dateSection, 0, 1);
        gridPane.add(timeSection, 0, 2);

//        ColumnConstraints col2 = new ColumnConstraints();
//        col2.setPercentWidth(60);
//        gridPane.getColumnConstraints().addAll(new ColumnConstraints(-1), col2, new ColumnConstraints(-1));

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

    public LocalTime getTimeStartValue() {
        return LocalTime.of(hourStartSpinner.getValue(), minuteStartSpinner.getValue());
    }

    public LocalTime getTimeEndValue() {
        return LocalTime.of(hourEndSpinner.getValue(), minuteEndSpinner.getValue());
    }
}
