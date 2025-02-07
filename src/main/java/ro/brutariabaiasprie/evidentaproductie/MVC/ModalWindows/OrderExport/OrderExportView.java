package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

public class OrderExportView extends Parent implements Builder<Region> {
    private final OrderExportModel model;

    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final Runnable updateSubgroups;

    private final Spinner<Integer> hourStartSpinner = new Spinner<>(0, 23, 0);
    private final Spinner<Integer> minuteStartSpinner = new Spinner<>(0, 59, 0);
    private final Spinner<Integer> hourEndSpinner = new Spinner<>(0, 23, 23);
    private final Spinner<Integer> minuteEndSpinner = new Spinner<>(0, 59, 59);
    private CheckComboBox<Group> groupCheckComboBox;
    private CheckComboBox<Group> subgroupCheckComboBox;

    public OrderExportView(OrderExportModel model, Consumer<ACTION_TYPE> actionHandler, Runnable updateSubgroups) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.updateSubgroups = updateSubgroups;
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

        Label groupFilterLabel = new Label("Grupa:");
        groupCheckComboBox = new CheckComboBox<>(model.getGroups());
        groupCheckComboBox.setMaxWidth(200);
        groupCheckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Group group) {
                if(group == null) {
                    return "Toate";
                }
                return group.getName();
            }

            @Override
            public Group fromString(String s) {
                return null;
            }
        });
        groupCheckComboBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
            private boolean changing = false;
            @Override
            public void onChanged(Change<? extends Integer> change) {
                groupCheckComboBox.setTitle("");
                if (!changing) {
                    change.next();
                    if (change.wasRemoved() && change.getRemoved().contains(0)) {
                        changing = true;
                        groupCheckComboBox.getCheckModel().clearChecks();
                        changing = false;
                    } else if (change.wasAdded() && change.getList().contains(0)) {
                        changing = true;
                        groupCheckComboBox.getCheckModel().checkAll();
                        changing = false;
                    } else if (change.getList().size() < groupCheckComboBox.getItems().size()) {
                        changing = true;
                        groupCheckComboBox.getCheckModel().clearCheck(0);
                        changing = false;
                    }
                    updateSubgroups.run();
                }
            }
        });
        HBox groupFilterContainer = new HBox(groupFilterLabel, groupCheckComboBox);
        groupFilterContainer.setAlignment(Pos.CENTER_LEFT);
        groupFilterContainer.setSpacing(4);
        Label subgroupFilterLabel = new Label("Subgrupa:");
        subgroupCheckComboBox = new CheckComboBox<>(model.getSubgroups());
        subgroupCheckComboBox.setMaxWidth(200);
        subgroupCheckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Group group) {
                if(group == null) {
                    return "Toate";
                }
                return group.getName();
            }

            @Override
            public Group fromString(String s) {
                return null;
            }
        });
        subgroupCheckComboBox.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
            private boolean changing = false;
            @Override
            public void onChanged(Change<? extends Integer> change) {
                subgroupCheckComboBox.setTitle("");
                if (!changing) {
                    change.next();
                    if (change.wasRemoved() && change.getRemoved().contains(0)) {
                        changing = true;
                        subgroupCheckComboBox.getCheckModel().clearChecks();
                        changing = false;
                    } else if (change.wasAdded() && change.getList().contains(0)) {
                        changing = true;
                        subgroupCheckComboBox.getCheckModel().checkAll();
                        changing = false;
                    } else if (change.getList().size() < subgroupCheckComboBox.getItems().size()) {
                        changing = true;
                        subgroupCheckComboBox.getCheckModel().clearCheck(0);
                        changing = false;
                    }
                }
            }
        });
        HBox subgroupFilterContainer = new HBox(subgroupFilterLabel, subgroupCheckComboBox);
        subgroupFilterContainer.setAlignment(Pos.CENTER_LEFT);
        subgroupFilterContainer.setSpacing(4);
        GridPane groupsAndSubgroupsFilters = new GridPane();
        groupsAndSubgroupsFilters.add(groupFilterLabel, 0, 0);
        groupsAndSubgroupsFilters.add(groupCheckComboBox, 1, 0);
        groupsAndSubgroupsFilters.add(subgroupFilterLabel, 0, 1);
        groupsAndSubgroupsFilters.add(subgroupCheckComboBox, 1, 1);
        groupsAndSubgroupsFilters.setHgap(8);
        groupsAndSubgroupsFilters.setVgap(4);

        Label lblFromDate = new Label("Din:");
        lblFromDate.minWidth(50);
        lblFromDate.prefHeight(50);
//        lblFromDate.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        fromDatePicker = new DatePicker(LocalDate.now());
        fromDatePicker.setShowWeekNumbers(false);

        Button clearFromDateButton = new Button();
        clearFromDateButton.setGraphic(new FontIcon("mdi2c-close"));
        clearFromDateButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clearFromDateButton.setOnAction(event -> fromDatePicker.setValue(null));
        clearFromDateButton.getStyleClass().add("filled-button");

        Label lblToDate = new Label("Pana in:");
        lblToDate.minWidth(50);
        lblToDate.minWidth(50);
//        GridPane.setHgrow(lblToDate, Priority.ALWAYS);
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
        dateSection.setStyle("-fx-alignment: CENTER-LEFT");

        ColumnConstraints colDateLabel = new ColumnConstraints();
//        colDateLabel.setHgrow(Priority.ALWAYS);
        colDateLabel.setMinWidth(75);
        colDateLabel.setPrefWidth(75);
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
        gridPane.add(groupsAndSubgroupsFilters, 0, 1);
        gridPane.add(new Separator(), 0, 2);
        gridPane.add(dateSection, 0, 3);
        gridPane.add(new Separator(), 0, 4);
        gridPane.add(timeSection, 0, 5);
        gridPane.setAlignment(Pos.CENTER_LEFT);


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

    public ObservableList<Group> getCheckedGroups() {
        return groupCheckComboBox.getCheckModel().getCheckedItems();
    }

    public ObservableList<Group> getCheckedSubgroups() {
        return subgroupCheckComboBox.getCheckModel().getCheckedItems();
    }
}
