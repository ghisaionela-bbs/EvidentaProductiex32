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
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.Globals;
import ro.brutariabaiasprie.evidentaproductie.Domain.UserRole;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.function.Consumer;


public class MainWindowView extends Parent implements Builder<Region> {
    private final Stage PARENT_STAGE;
    public Stage getPARENT_STAGE() {
        return PARENT_STAGE;
    }
    private final MainWindowModel model;

    private final BorderPane root = new BorderPane();
    private VBox navigationMenu;
    private Button accountButton;
    private Button managerButton;
    private Button productionButton;
    private Button dashBoardButton;

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
        dashBoardButton = createNavBarMenuButton("Tablou\nde bord", SceneType.DASHBOARD);
        dashBoardButton.setGraphic(new FontIcon("mdi2m-monitor-dashboard"));
        accountButton = createNavBarMenuButton("\uD83D\uDC64\nCont", SceneType.ACCOUNT);
        managerButton = createNavBarMenuButton("\uD83D\uDCCA\nAdministrare", SceneType.MANAGER);
        productionButton = createNavBarMenuButton("\uD83C\uDFED\nProductie", SceneType.PRODUCTION);
        if(PARENT_STAGE.getWidth() < Globals.MINIMIZE_WIDTH) {
            accountButton.setText("\uD83D\uDC64");
            managerButton.setText("\uD83D\uDCCA");
            productionButton.setText("\uD83C\uDFED");
            dashBoardButton.setText("");
        } else {
            accountButton.setText("\uD83D\uDC64\nCont");
            managerButton.setText("\uD83D\uDCCA\nAdministrare");
            productionButton.setText("\uD83C\uDFED\nProductie");
            dashBoardButton.setText("Tablou\nde bord");
        }

        navigationMenu.getChildren().add(accountButton);
        navigationMenu.getChildren().add(managerButton);
        navigationMenu.getChildren().add(productionButton);
        root.setLeft(navigationMenu);
        //SETTING UP THE WINDOW RESIZE LISTENERS
        createStageResizeListeners();
        //SETTING UP THE STYLES
        navigationMenu.getStyleClass().add("main-navigation-menu");
        root.getStyleClass().add("main-window");
        return root;
    }

    private void createStageResizeListeners() {
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            if(PARENT_STAGE.getWidth() < Globals.MINIMIZE_WIDTH) {
                accountButton.setText("\uD83D\uDC64");
                managerButton.setText("\uD83D\uDCCA");
                productionButton.setText("\uD83C\uDFED");
                dashBoardButton.setText("");
            } else {
                accountButton.setText("\uD83D\uDC64\nCont");
                managerButton.setText("\uD83D\uDCCA\nAdministrare");
                productionButton.setText("\uD83C\uDFED\nProductie");
                dashBoardButton.setText("Tablou\nde bord");
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

    public void setCenter(Node content) {
        content.getStyleClass().add("main-window-content");
        root.setCenter(content);
    }

    public void openDefaultTab() {
        switch (model.getCONNECTED_USER().getID_ROLE()){
            case 1:
            case 2:
                managerButton.fire();
                break;
            default:
                productionButton.fire();
                break;
        }
    }

    public void onProductionShortcut(){
        for(int i = 0; i < navigationMenu.getChildren().size(); i++) {
            navigationMenu.getChildren().get(i).getStyleClass().remove("active");
        }
        productionButton.getStyleClass().add("active");
    }
}
