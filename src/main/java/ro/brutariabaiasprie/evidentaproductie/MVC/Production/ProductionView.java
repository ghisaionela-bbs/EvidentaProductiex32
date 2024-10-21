package ro.brutariabaiasprie.evidentaproductie.MVC.Production;

import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class ProductionView extends Parent implements Builder<Region> {
    @Override
    public Region build() {
        Region region = new Region();
        region.setStyle("-fx-background-color: red;");
        return region;
    }
}
