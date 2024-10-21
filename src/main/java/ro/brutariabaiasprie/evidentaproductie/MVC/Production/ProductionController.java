package ro.brutariabaiasprie.evidentaproductie.MVC.Production;

import javafx.scene.layout.Region;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.DBConn.DBConnView;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneType;

import java.util.function.BiConsumer;

public class ProductionController implements SceneController {
    private final ProductionView view;
    private final ProductionModel model = new ProductionModel();
    private final BiConsumer<Runnable, SceneType> sceneSwitActionHandler;

    public ProductionController(BiConsumer<Runnable, SceneType> sceneSwitActionHandler) {
        this.view = new ProductionView();
        this.sceneSwitActionHandler = sceneSwitActionHandler;
    }

    @Override
    public Region getView() {
        return view.build();
    }
}
