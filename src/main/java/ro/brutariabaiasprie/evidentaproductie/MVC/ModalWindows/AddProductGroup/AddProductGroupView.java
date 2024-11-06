package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddProductGroup;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;


public class AddProductGroupView extends Parent implements Builder<Region> {
    private final AddProductGroupModel model;

    public AddProductGroupView(AddProductGroupModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        VBox root = new VBox();

        GridPane gridPane = new GridPane();

        return root;
    }
}
