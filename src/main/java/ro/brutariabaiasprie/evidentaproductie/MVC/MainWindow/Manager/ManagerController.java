package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Manager;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ModifiedTableData;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.MVC.SceneController;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ManagerController implements SceneController {
    private final Stage PARENT_STAGE;
    private final ManagerModel model = new ManagerModel();
    private ManagerView view;

    public ManagerController(Stage parentStage, Consumer<Order> productionShortcutHandler) {
        this.PARENT_STAGE = parentStage;
        this.view = new ManagerView(this.model, parentStage, productionShortcutHandler, this::reloadOrders, this::filterOrders, this::updateFilters);
        Platform.runLater(() -> {
            model.loadProducts();
            model.loadOrders();
            model.loadGroups2();
//            model.loadRecords();
            model.loadUsers();
            model.loadGroupFilterList();
            view.setGroupFilter();
            model.loadSubgroupFilterList();
            view.setSubgroupFilter();
        });
        DBConnectionService.getModifiedTables().addListener((MapChangeListener<String, ModifiedTableData>) change -> {
            if (change.wasAdded()) {
                String key = change.getKey();
                Platform.runLater(() -> {
                    model.loadGroups2();
                    model.loadProducts();
                    model.loadOrders();
//                    model.loadRecords();
                    model.loadUsers();
                    if(key.equals("GRUPE_PRODUSE")) {
                        model.loadGroupFilterList();
                        view.setGroupFilter();
                        model.loadSubgroupFilterList();
                        view.setSubgroupFilter();
                        filterOrders();
                    }

                });

            }
        });
    }

    private void updateFilters() {
        ArrayList<Group> checked_groups = new ArrayList<>();
        for(int i : view.getOrderGroupFilter().getCheckModel().getCheckedIndices()) {
            if (i != -1) {
                checked_groups.add(view.getOrderGroupFilter().getCheckModel().getItem(i));
            }
        }
        model.setOrderGroupFilter(checked_groups);
        model.loadSubgroupFilterList();
        view.setSubgroupFilter();
    }

    private void filterOrders() {
        model.setOrderStatusFilter(view.getOrderStatusFilter());
        ArrayList<Group> checked_subgroups = new ArrayList<>();
        for(int i : view.getOrderSubgroupFilter().getCheckModel().getCheckedIndices()) {
            if (i != -1) {
                checked_subgroups.add(view.getOrderSubgroupFilter().getCheckModel().getItem(i));
            }
        }
        model.setOrderSubgroupFilter(checked_subgroups);
        Platform.runLater(model::loadOrders);
    }

    private void reloadOrders(Boolean showClosedOrders) {
        Platform.runLater(model::loadOrders);
    }

    @Override
    public Region getView() {
        return view.build();
    }

}
