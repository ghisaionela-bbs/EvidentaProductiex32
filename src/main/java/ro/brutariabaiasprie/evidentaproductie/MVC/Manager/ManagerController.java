package ro.brutariabaiasprie.evidentaproductie.MVC.Manager;

import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;

public class ManagerController implements SceneController {
    private final Stage PARENT_STAGE;
    private final ManagerModel model = new ManagerModel();
    private ManagerView view;

    public ManagerController(Stage parentStage) {
        this.PARENT_STAGE = parentStage;
        this.view = new ManagerView(this.model, parentStage);
    }

    @Override
    public Region getView() {
        return view.build();
    }
}
