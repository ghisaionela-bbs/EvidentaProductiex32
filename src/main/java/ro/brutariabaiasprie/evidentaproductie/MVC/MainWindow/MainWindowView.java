package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.function.Consumer;


public class MainWindowView extends Parent implements Builder<Region> {
    private final Stage PARENT_STAGE;
    public Stage getPARENT_STAGE() {
        return PARENT_STAGE;
    }
    private final MainWindowModel model;

    private final BorderPane layout = new BorderPane();
    private VBox navigationMenu;
    private Button accountButton;
    private Button managerButton;
    private Button productionButton;

    private final Consumer<SceneType> actionHandler;

    public MainWindowView(MainWindowModel model, Stage parentStage, Consumer<SceneType> actionHandler) {
        this.model = model;
        this.PARENT_STAGE = parentStage;
        this.actionHandler = actionHandler;
    }

    @Override
    public Region build() {
        //SETTING UP THE NAVIGATION MENU
        navigationMenu = new VBox();
        accountButton = createNavBarMenuButton("\uD83D\uDC64\nCont", SceneType.ACCOUNT);
        managerButton = createNavBarMenuButton("\uD83D\uDCCA\nAdministrare", SceneType.MANAGER);
        productionButton = createNavBarMenuButton("\uD83C\uDFED\nProductie", SceneType.PRODUCTION);
        navigationMenu.getChildren().add(accountButton);
        if(model.getCONNECTED_USER().getID_ROLE() == 0 || model.getCONNECTED_USER().getID_ROLE() == 1) {
            navigationMenu.getChildren().add(managerButton);
        }
        navigationMenu.getChildren().add(productionButton);
        layout.setLeft(navigationMenu);
        //SETTING UP THE WINDOW RESIZE LISTENERS
        createStageResizeListeners();
        //SETTING UP THE STYLES
        navigationMenu.getStyleClass().add("main-navigation-menu");
        layout.getStyleClass().add("main-window");
        return layout;
    }

    private void createStageResizeListeners() {
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            System.out.println(PARENT_STAGE.getWidth());
            if(PARENT_STAGE.getWidth() < 1000.0) {
                accountButton.setText("\uD83D\uDC64");
                managerButton.setText("\uD83D\uDCCA");
                productionButton.setText("\uD83C\uDFED");
            } else {
                accountButton.setText("\uD83D\uDC64\nCont");
                managerButton.setText("\uD83D\uDCCA\nAdministrare");
                productionButton.setText("\uD83C\uDFED\nProductie");
            }
        };

        PARENT_STAGE.widthProperty().addListener(stageSizeListener);
        PARENT_STAGE.heightProperty().addListener(stageSizeListener);
    }

    private Button createNavBarMenuButton(String text, SceneType sceneType) {
        Button button = new Button(text);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for(int i = 0; i < navigationMenu.getChildren().size(); i++) {
                    navigationMenu.getChildren().get(i).getStyleClass().remove("active");
                }
                button.getStyleClass().add("active");
                actionHandler.accept(sceneType);
            }
        });
        return button;
    }

    public void setCenter(Node node) {
        node.getStyleClass().add("main-window-content");
        layout.setCenter(node);
    }

    public void openDefaultTab() {
        productionButton.fire();
    }
}
