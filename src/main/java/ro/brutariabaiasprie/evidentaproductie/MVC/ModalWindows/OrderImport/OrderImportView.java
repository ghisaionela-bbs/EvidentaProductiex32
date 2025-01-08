package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderImport;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

public class OrderImportView extends Parent implements Builder<Region> {
    private final OrderImportModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final Runnable browseActionHandler;
    private BorderPane root;
    private final Stage PARENT_STAGE;

    private TextField fileNameTextField;
    private Spinner<Integer> sheetSpinner;
    private Spinner<Integer> productNameColumnSpinner;
    private Spinner<Integer> quantityColumnSpinner;
    private Spinner<Integer> dateColumnSpinner;
    private Spinner<Integer> timeColumnSpinner;
    private Spinner<Integer> startingRowSpinner;
    private int SCENE_NO = -1;
    private final int LAST_SCENE_NO = 4;

    public OrderImportView(OrderImportModel model, Stage owner, Runnable browseActionHandler, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.browseActionHandler = browseActionHandler;
        this.actionHandler = actionHandler;
        this.PARENT_STAGE = owner;
    }

    @Override
    public Region build() {
        root = new BorderPane();
        root.setPrefHeight(PARENT_STAGE.getHeight() * 0.5);
        SCENE_NO = 0;
        showConfigurationOptions();
        root.getStyleClass().add("modal-window");
        root.getStyleClass().add("entry-view");

        return root;
    }

    public void switchScene(int step) {
        if(step == -1 && SCENE_NO > 0) {
            SCENE_NO --;
        } else if (step == 1 && SCENE_NO < LAST_SCENE_NO) {
            SCENE_NO ++;
        }
        switch (SCENE_NO) {
            case 0:
                showConfigurationOptions();
                break;
            case 1:
                showDataPreview();
                break;
            case 2:
                showValidationScene();
                break;
        }
    }

    private Node createWindowActionButtons() {
        HBox buttonsContainer = new HBox();
        if(SCENE_NO == 0) {
            SceneButton continueButton = new SceneButton("Continua", ACTION_TYPE.CONFIRMATION);
            continueButton.setOnAction(event -> actionHandler.accept(continueButton.getActionType()));
            SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
            cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

            buttonsContainer.getChildren().addAll(continueButton, cancelButton);
            buttonsContainer.setSpacing(8);
            buttonsContainer.setAlignment(Pos.CENTER);
        }
        else if(SCENE_NO > 0 && SCENE_NO < LAST_SCENE_NO){
            SceneButton continueButton = new SceneButton("Continua", ACTION_TYPE.CONFIRMATION);
            continueButton.setOnAction(event -> actionHandler.accept(continueButton.getActionType()));
            SceneButton returnButton = new SceneButton("Inapoi", ACTION_TYPE.RETURN);
            returnButton.setOnAction(event -> actionHandler.accept(returnButton.getActionType()));
            SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
            cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

            buttonsContainer.getChildren().addAll(continueButton, returnButton, cancelButton);
            buttonsContainer.setSpacing(8);
            buttonsContainer.setAlignment(Pos.CENTER);
        } else {
            SceneButton confirmButton = new SceneButton("Continua", ACTION_TYPE.CONFIRMATION);
            confirmButton.setOnAction(event -> actionHandler.accept(confirmButton.getActionType()));

            SceneButton cancelButton = new SceneButton("Anuleaza", ACTION_TYPE.CANCELLATION);
            cancelButton.setOnAction(event -> actionHandler.accept(cancelButton.getActionType()));

            buttonsContainer.getChildren().addAll(confirmButton, cancelButton);
            buttonsContainer.setSpacing(8);
            buttonsContainer.setAlignment(Pos.CENTER);
        }

        return buttonsContainer;
    }

    private void showConfigurationOptions() {
        //  Title
        Label titleLabel  = new Label("Import excel comenzi");
        titleLabel.getStyleClass().add("title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        HBox titleContainer = new HBox(titleLabel);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        TextFlow info = new TextFlow(new Text("Configurati coloanele si randul de la care se va incepe citirea fisierului excel\n(fara cap de tabel):"));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);

        Label productNameLabel = new Label("Coloana denumire:");
        Label unitMeasurementLabel = new Label("Coloana cantitate:");

        fileNameTextField = new TextField();
        fileNameTextField.setPromptText("Cauta fisierul");
        fileNameTextField.textProperty().bind(model.filenameProperty());
        fileNameTextField.setEditable(false);

        Button browseFileButton = new Button("Cauta");
        browseFileButton.setOnAction(event -> browseActionHandler.run());
        browseFileButton.getStyleClass().add("filled-button");

        sheetSpinner = new Spinner<>(1, 1000, 1);
        sheetSpinner.getValueFactory().setValue(1);

        productNameColumnSpinner = new Spinner<>(1,1000,1);
        productNameColumnSpinner.getValueFactory().setValue(1);

        quantityColumnSpinner = new Spinner<>(1,1000,1);
        quantityColumnSpinner.getValueFactory().setValue(2);

        dateColumnSpinner = new Spinner<>(1,1000,1);
        dateColumnSpinner.getValueFactory().setValue(3);

        timeColumnSpinner = new Spinner<>(1,1000,1);
        timeColumnSpinner.getValueFactory().setValue(4);

        Label startingRowLabel = new Label("Randul de inceput (fara cap de tabel):");
        startingRowLabel.setWrapText(true);
        GridPane.setFillWidth(startingRowLabel, true);
        startingRowSpinner = new Spinner<>(1,1000,1);
        startingRowSpinner.getValueFactory().setValue(1);

        gridPane.add(titleContainer, 0, 0, 2, 1);
        gridPane.add(info, 0, 1, 3, 1);

        gridPane.add(fileNameTextField, 0, 2, 2, 1);
        gridPane.add(browseFileButton, 2, 2);

        gridPane.add(new Label("Numar sheet: "), 0, 3);
        gridPane.add(sheetSpinner, 1, 3);

        gridPane.add(productNameLabel, 0, 4);
        gridPane.add(productNameColumnSpinner, 1, 4);

        gridPane.add(unitMeasurementLabel, 0, 5);
        gridPane.add(quantityColumnSpinner, 1, 5);

        gridPane.add(new Label("Coloana data:"), 0, 6);
        gridPane.add(dateColumnSpinner, 1, 6);

        gridPane.add(new Label("Coloana ora:"), 0, 7);
        gridPane.add(timeColumnSpinner, 1, 7);

        gridPane.add(startingRowLabel, 0, 8, 1, 2);
        gridPane.add(startingRowSpinner, 1, 8);

        gridPane.getStyleClass().add("grid-form");

        VBox.setVgrow(gridPane, Priority.ALWAYS);

        root.setCenter(gridPane);
        root.setBottom(createWindowActionButtons());

    }


    private void showDataPreview() {
        Label infoLabel = new Label("Date sheet " + sheetSpinner.getValue());

        TableView<Object[]> excelDataView = new TableView<>();
        excelDataView.setSelectionModel(null);
        excelDataView.getStyleClass().add("excel-import-table");
        VBox.setVgrow(excelDataView, Priority.ALWAYS);
        for (int columnIndex = 0; columnIndex <= model.getNumCols(); columnIndex++) {
            TableColumn<Object[], Object> column = new TableColumn<>();
            final int colIndex = columnIndex;
            column.setCellValueFactory(stringCellDataFeatures -> new SimpleObjectProperty<>((stringCellDataFeatures.getValue()[colIndex])));
            column.setCellFactory(tableColumn -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(null);
                    } else {
                        if(getIndex() >= startingRowSpinner.getValue() && (
                                colIndex == productNameColumnSpinner.getValue() ||
                                colIndex == quantityColumnSpinner.getValue() ||
                                colIndex == dateColumnSpinner.getValue() ||
                                colIndex == timeColumnSpinner.getValue())
                        ) {
                            setStyle("-fx-background-color: COLOR_WHITE; -fx-border-color: derive(COLOR_WHITE, -20%)");
                        } else {
                            setStyle("-fx-background-color: derive(COLOR_WHITE, -10%); -fx-border-color: derive(COLOR_WHITE, -20%)");
                        }

                        if(getIndex() >= startingRowSpinner.getValue()
                                && colIndex == dateColumnSpinner.getValue()
                                && !(item instanceof LocalDate)) {
                            setStyle("-fx-background-color: RED; -fx-text-fill: COLOR_WHITE");
                        }

                        if(getIndex() >= startingRowSpinner.getValue()
                                && colIndex == timeColumnSpinner.getValue()
                                && !(item instanceof LocalTime)) {
                            setStyle("-fx-background-color: RED; -fx-text-fill: COLOR_WHITE");
                        }

                        if(item instanceof LocalDate) {
                            try{
                                setText(model.dateFormatter.format((LocalDate) item));
                            } catch (Exception e) {
                                setText(item.toString());
                                setStyle("-fx-background-color: RED; -fx-text-fill: COLOR_WHITE");
                            }
                            return;
                        }

                        if(item instanceof LocalTime) {
                            try {
                                setText(model.timeFormatter.format((LocalTime) item));
                            } catch (Exception e) {
                                setText(item.toString());
                                setStyle("-fx-background-color: RED; -fx-text-fill: COLOR_WHITE");
                            }
                            return;
                        }

                        setText(item.toString());
                    }
                }
            });
            excelDataView.getColumns().add(column);
        }
        excelDataView.setItems(model.getData());

        root.setCenter(new VBox(infoLabel, excelDataView));
        root.setBottom(createWindowActionButtons());
//        root.getChildren().clear();
//        root.getChildren().addAll(infoLabel, excelDataView, createWindowActionButtons());
    }

    private void showValidationScene() {
        Label infoLabel = new Label("Se valideaza datele");
        VBox.setVgrow(infoLabel, Priority.ALWAYS);
        infoLabel.prefHeight(Double.MAX_VALUE);

        root.setCenter(infoLabel);
        root.setBottom(createWindowActionButtons());

//        root.getChildren().clear();
//        root.getChildren().addAll(infoLabel, createWindowActionButtons());
    }

    public int getSheetNumber() {
        return sheetSpinner.getValue() - 1;
    }

    public int getProductNameColumn(){
        return productNameColumnSpinner.getValue();
    }

    public int getQuantityColumn(){
        return quantityColumnSpinner.getValue();
    }

    public int getDateColumn() {
        return dateColumnSpinner.getValue();
    }

    public int getTimeColumn() {
        return timeColumnSpinner.getValue();
    }

    public int getStartingRow(){
        return startingRowSpinner.getValue();
    }
}
