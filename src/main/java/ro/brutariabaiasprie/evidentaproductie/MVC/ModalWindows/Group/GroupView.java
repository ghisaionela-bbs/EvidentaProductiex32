package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class GroupView extends Parent implements Builder<Region> {
    private final GroupModel model;
    private final Consumer<ACTION_TYPE> actionHandler;
    private final WINDOW_TYPE type;

    private Runnable deleteGroupHandler;
    private TextArea groupNameTextArea;

    public GroupView(GroupModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.type = type;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setCenter(createContentSection());
        root.setBottom(createSceneButtons());
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        return root;
    }

    public void setDeleteGroupHandler(Runnable deleteGroupHandler) {
        this.deleteGroupHandler = deleteGroupHandler;
    }

    /***
     * Creates the product information section with possibility of editing depending on the WINDOW_TYPE type.
     * @return GridPane with the product information
     */
    private GridPane createContentSection() {
        //  Title
        Label groupIdLabel  = new Label();
        // Name
        Label groupNameLabel = new Label("Denumire:");
        groupNameTextArea = new TextArea();
        groupNameTextArea.setPrefSize(400, 200);
        groupNameTextArea.setWrapText(true);

        // Setting up the values and properties of controls
        switch (type) {
            case ADD:
                groupIdLabel.setText("Adaugare grupa nou");
                break;
            case VIEW:
                groupNameTextArea.setDisable(true);
            case EDIT:
                groupIdLabel.setText("Grupa " + model.getGroup().getId());
                groupNameTextArea.setText(model.getGroup().getName());
                break;
        }

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        // Adding the controls
        gridPane.add(groupIdLabel, 0, 0);
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteGroupHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            gridPane.add(deleteButton, 1, 0);
        }
        gridPane.add(groupNameLabel, 0, 1, 2, 1);
        gridPane.add(groupNameTextArea, 0, 2, 2, 1);
        // adding constraints
        for (int i = 0 ; i < gridPane.getRowCount(); i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(row);
        }
        for (int j = 0 ; j < gridPane.getColumnCount(); j++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(col);
        }

        return gridPane;
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

    public String getName() {
        return groupNameTextArea.getText();
    }
}
