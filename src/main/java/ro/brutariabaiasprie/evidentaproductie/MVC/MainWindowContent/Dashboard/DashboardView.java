package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Dashboard;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductie.DatabaseObjects.OProductGroup;
import ro.brutariabaiasprie.evidentaproductie.DatabaseObjects.OUser;

public class DashboardView extends Parent implements Builder<Region> {
    private final DashboardModel model;
    public Runnable addProductGroupHandler;

    public DashboardView(DashboardModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        TabPane tabPane =  new TabPane();

        tabPane.getTabs().add(createTablesPane());
        return tabPane;
    }

    private Tab createTablesPane() {
        Label tableLabel = new Label("Tabel: ");
        String[] table_names = {"Utilizatori", "Grupe produse"};
        ComboBox<String> tableComboBox = new ComboBox<>(FXCollections.observableArrayList(table_names));

        Button addButton = new Button();
        addButton.setGraphic(new FontIcon("mdi2p-plus-box-outline"));
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (tableComboBox.getSelectionModel().getSelectedItem()){
                    case "Grupe produse":
                        addProductGroupHandler.run();
                        break;
                }
            }
        });

        FlowPane headerSection = new FlowPane(tableLabel, tableComboBox, addButton);
        headerSection.setVgap(8);
        headerSection.setHgap(8);

        ListView<OUser> userListView = createUsersListView();
        ListView<OProductGroup> productGroupListView = createProductGroupListView();
        StackPane tablesStackPane = new StackPane(userListView, productGroupListView);
        VBox.setVgrow(tablesStackPane, Priority.ALWAYS);

        VBox content = new VBox(headerSection, tablesStackPane);

        tableComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                switch (newValue) {
                    case "Utilizatori":
                        userListView.toFront();
                        break;
                    case "Grupe produse":
                        productGroupListView.toFront();
                        break;
                }
            }
        });
        tableComboBox.getSelectionModel().selectFirst();

        Tab tablesTab = new Tab("Tabele");
        tablesTab.setContent(content);
        tablesTab.setClosable(false);
        return tablesTab;
    }

    private ListView<OUser> createUsersListView() {
        ListView<OUser> userListView = new ListView<>();
        return userListView;
    }

    private ListView<OProductGroup> createProductGroupListView() {
        ListView<OProductGroup> productGroupListView = new ListView<>();
        productGroupListView.setPlaceholder(new Label("Nu sunt grupe de produs de afisat."));

        productGroupListView.setItems(model.getProductGroups());
        return productGroupListView;
    }

}
