package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport;

import javafx.beans.property.SimpleObjectProperty;
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

import java.util.function.Consumer;

public class ExcelImportView extends Parent implements Builder<Region> {
    private final ExcelImportModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final Runnable browseActionHandler;
    private BorderPane root;
    private final Stage PARENT_STAGE;

    private TextField fileNameTextField;
    private Spinner<Integer> sheetNumberSpinner;
    private Spinner<Integer> productNameColumnSpinner;
    private Spinner<Integer> unitMeasurementColumnSpinner;
    private Spinner<Integer> batchColumnSpinner;
    private Spinner<Integer> startingRowSpinner;
    private int SCENE_NO = -1;
    private final int LAST_SCENE_NO = 4;

    public ExcelImportView(ExcelImportModel model, Stage owner, Runnable browseActionHandler, Consumer<ACTION_TYPE> actionHandler) {
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
        TextFlow info = new TextFlow(new Text("Configurati coloanele si randul de la care se va incepe citirea fisierului excel\n(fara cap de tabel):"));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);

        Label productNameLabel = new Label("Coloana denumire:");
        Label unitMeasurementLabel = new Label("Coloana um:");

        fileNameTextField = new TextField();
        fileNameTextField.setPromptText("Cauta fisierul");
        fileNameTextField.textProperty().bind(model.filenameProperty());
        fileNameTextField.setEditable(false);

        Button browseFileButton = new Button("Cauta");
        browseFileButton.setOnAction(event -> browseActionHandler.run());

        sheetNumberSpinner = new Spinner<>(1, 1000, 1);
        sheetNumberSpinner.getValueFactory().setValue(1);
        VBox sheetNumberSection = new VBox(new Label("Numar sheet:"), sheetNumberSpinner);
        sheetNumberSection.getStyleClass().add("section");
        sheetNumberSection.getStyleClass().add("vbox-layout");

        productNameColumnSpinner = new Spinner<>(1,1000,1);
        productNameColumnSpinner.getValueFactory().setValue(1);
        VBox productSection = new VBox(productNameLabel, productNameColumnSpinner);
        productSection.getStyleClass().add("section");
        productSection.getStyleClass().add("vbox-layout");

        batchColumnSpinner = new Spinner<>(1, 1000, 1);
        batchColumnSpinner.getValueFactory().setValue(2);
        VBox batchSection = new VBox(new Label("Coloana sarja:"), batchColumnSpinner);
        batchSection.getStyleClass().add("section");
        batchSection.getStyleClass().add("vbox-layout");

        unitMeasurementColumnSpinner = new Spinner<>(1,1000,1);
        unitMeasurementColumnSpinner.getValueFactory().setValue(3);
        VBox unitMeasurementSection = new VBox(unitMeasurementLabel, unitMeasurementColumnSpinner);
        unitMeasurementSection.getStyleClass().add("section");
        unitMeasurementSection.getStyleClass().add("vbox-layout");

        Label startingRowLabel = new Label("Randul de inceput (fara cap de tabel):");
        startingRowLabel.setWrapText(true);
        GridPane.setFillWidth(startingRowLabel, true);
        startingRowSpinner = new Spinner<>(1,1000,1);
        startingRowSpinner.getValueFactory().setValue(1);
        VBox startingRowSection = new VBox(startingRowLabel, startingRowSpinner);
        startingRowSection.getStyleClass().add("section");
        startingRowSection.getStyleClass().add("vbox-layout");

        gridPane.add(fileNameTextField, 0, 0, 2, 1);
        gridPane.add(browseFileButton, 2, 0);

        gridPane.add(new Label("Numar sheet:"), 0, 1);
        gridPane.add(sheetNumberSpinner, 1, 1);

        gridPane.add(productNameLabel, 0, 2);
        gridPane.add(productNameColumnSpinner, 1, 2);

        gridPane.add(new Label("Coloana sarja:"), 0, 3);
        gridPane.add(batchColumnSpinner, 1, 3);

//        gridPane.add(sheetNumberSection, 0, 1);
//        gridPane.add(productSection, 0, 2);
//        gridPane.add(batchSection, 0, 3);
//        gridPane.add(unitMeasurementSection, 0, 4);
//        gridPane.add(startingRowSection, 0, 5);
        gridPane.add(unitMeasurementLabel, 0, 4);
        gridPane.add(unitMeasurementColumnSpinner, 1, 4);

        gridPane.add(startingRowLabel, 0, 5);
        gridPane.add(startingRowSpinner, 1, 5);

        gridPane.getStyleClass().add("grid-form");

        VBox.setVgrow(gridPane, Priority.ALWAYS);

        root.setCenter(new VBox(info, gridPane));
        root.setBottom(createWindowActionButtons());
//        root.getChildren().clear();
//        root.getChildren().addAll(info, gridPane, createWindowActionButtons());
    }


    private void showDataPreview() {
        Label infoLabel = new Label("Datele care vor fi importate sunt cele evidentiate mai jos:");

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
                    } else {
                        if(item instanceof String) {
                            setText((String) item);
                        } else if (item instanceof Double) {
                            setText(String.format("%.2f", item));
                        }
                    }
                    if(getIndex() >= startingRowSpinner.getValue() &&
                            (colIndex == productNameColumnSpinner.getValue()
                                    || colIndex == batchColumnSpinner.getValue()
                                    || colIndex == unitMeasurementColumnSpinner.getValue())) {
                        setStyle("-fx-background-color: COLOR_WHITE; -fx-border-color: derive(COLOR_WHITE, -20%)");
                    } else {
                        setStyle("-fx-background-color: derive(COLOR_WHITE, -10%); -fx-border-color: derive(COLOR_WHITE, -20%)");
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
        return sheetNumberSpinner.getValue() - 1;
    }

    public int getProductNameColumn(){
        return productNameColumnSpinner.getValue();
    }

    public int getBatchValueColumn() {
        return batchColumnSpinner.getValue();
    }

    public int getUnitMeasurementColumn(){
        return unitMeasurementColumnSpinner.getValue();
    }

    public int getStartingRow(){
        return startingRowSpinner.getValue();
    }


}
