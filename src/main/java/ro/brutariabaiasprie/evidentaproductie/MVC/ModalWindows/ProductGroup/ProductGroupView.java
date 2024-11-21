package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ProductGroup;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.UserRole;
import ro.brutariabaiasprie.evidentaproductie.MVC.Components.SceneButton;

import java.util.function.Consumer;

public class ProductGroupView extends Parent implements Builder<Region> {
    private final Consumer<ACTION_TYPE> actionHandler;
    private final WINDOW_TYPE type;
    private final ProductGroupModel model;

    private Runnable deleteGroupHandler;
    private TextArea nameTextArea;


    public ProductGroupView(ProductGroupModel model, WINDOW_TYPE type, Consumer<ACTION_TYPE> actionHandler) {
        this.model = model;
        this.type = type;
        this.actionHandler = actionHandler;
    }

    public void setDeleteGroupHandler(Runnable deleteGroupHandler) {
        this.deleteGroupHandler = deleteGroupHandler;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setCenter(createContentSection());
        root.setBottom(createSceneButtons());
        root.getCenter().getStyleClass().add("center");
        root.getBottom().getStyleClass().add("bottom");
        root.getStyleClass().add("modal-window");
        root.getStyleClass().add("entry-view");
        return root;
    }

    private Node createContentSection() {
        //  Title
        Label groupIdLabel  = new Label();
        groupIdLabel.getStyleClass().add("title");

        // Username section
        Label nameLabel = new Label("Denumire:");
        nameTextArea = new TextArea();
        nameTextArea.setPrefSize(250, 200);
        VBox usernameSection = new VBox(nameLabel, nameTextArea);
        usernameSection.getStyleClass().add("section");
        usernameSection.getStyleClass().add("vbox-layout");

        // Setting up the container
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("grid-form");
        // Adding the controls
        if(type == WINDOW_TYPE.EDIT) {
            Button deleteButton = new Button("Stergere");
            deleteButton.setOnAction(event -> deleteGroupHandler.run());
            deleteButton.getStyleClass().add("filled-button");
            deleteButton.setStyle("-fx-background-color: red;");
            GridPane.setHalignment(deleteButton, HPos.RIGHT);
            gridPane.add(deleteButton, 4, 0);
        }
        gridPane.add(groupIdLabel, 0, 0, 2, 1);
        gridPane.add(usernameSection, 0, 1, 3, 1);
        gridPane.getStyleClass().add("grid-layout");


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
}
