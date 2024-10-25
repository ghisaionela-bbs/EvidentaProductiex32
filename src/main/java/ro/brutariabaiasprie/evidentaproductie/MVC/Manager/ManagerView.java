package ro.brutariabaiasprie.evidentaproductie.MVC.Manager;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.apache.poi.ss.formula.functions.T;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;

public class ManagerView extends Parent implements Builder<Region> {
    private Stage PARENT_STAGE;
    private ManagerModel model;
    private VBox root;

    public ManagerView(ManagerModel model, Stage parentStage) {
        this.model = model;
        this.PARENT_STAGE = parentStage;
    }

    @Override
    public Region build() {
        root = new VBox();
        root.setFillWidth(true);
        root.getChildren().add(createTabs());
        return root;
    }

    private Node createTabs() {
        TabPane tabPane =  new TabPane();
        tabPane.getTabs().add(createProductsTab());
        tabPane.getTabs().add(createOrdersTab());
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return tabPane;
    }

    private Tab createProductsTab() {
        Tab productsTab =  new Tab("Produse");

        VBox contentContainer = new VBox();

        Label productsSectionTitle = new Label("Produse");
        productsSectionTitle.getStyleClass().add("tab-section-title");

        TableView<ProductDTO> productsTableView = new TableView<>();
        productsTableView.setPlaceholder(new Label("Nu exista produse."));
        VBox.setVgrow(productsTableView, Priority.ALWAYS);

        contentContainer.getChildren().addAll(productsSectionTitle, productsTableView);
        productsTab.setContent(contentContainer);

        return productsTab;
    }

    private Tab createOrdersTab() {
        Tab ordersTab = new Tab("Comenzi");

        VBox contentContainer = new VBox();

        Label ordersSectionTitle = new Label("Comenzi");
        ordersSectionTitle.getStyleClass().add("tab-section-title");

        TableView<ProductDTO> ordersTableView = new TableView<>();
        ordersTableView.setPlaceholder(new Label("Nu exista comenzi."));
        VBox.setVgrow(ordersTableView, Priority.ALWAYS);

        contentContainer.getChildren().addAll(ordersSectionTitle, ordersTableView);
        ordersTab.setContent(contentContainer);

        return ordersTab;
    }



}
