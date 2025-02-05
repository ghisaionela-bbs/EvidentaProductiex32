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
        this.view = new ManagerView(this.model, parentStage, productionShortcutHandler,
                this::reloadOrders, this::filterOrders, this::updateOrdersFilters,
                this::filterProducts, this::updateProductsFilter);
        Platform.runLater(() -> {
            model.loadProducts();
            model.loadOrders();
            model.loadGroups2();
//            model.loadRecords();
            model.loadUsers();
            // Filters
            model.loadGroupFilterList();
            view.setGroupFilter();
            model.loadSubgroupFilterList();
            view.setSubgroupFilter();
            model.loadProductGroupFilterList();
            view.setProductGroupFilter();
            model.loadProductSubgroupFilterList();
            view.setProductSubgroupFilter();
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
                        model.loadProductGroupFilterList();
                        view.setProductGroupFilter();
                        model.loadProductSubgroupFilterList();
                        view.setProductSubgroupFilter();
                        filterProducts();
                    }

                });

            }
        });
    }

    private void updateProductsFilter() {
        ArrayList<Group> checked_groups = new ArrayList<>();
        for(int i : view.getProductGroupFilter().getCheckModel().getCheckedIndices()) {
            if (i != -1) {
                checked_groups.add(view.getProductGroupFilter().getCheckModel().getItem(i));
            }
        }
        model.setProductGroupFilter(checked_groups);
        model.loadProductSubgroupFilterList();
        view.setProductSubgroupFilter();
    }

    private void filterProducts() {
        ArrayList<Group> checked_subgroups = new ArrayList<>();
        for(int i : view.getProductSubgroupFilter().getCheckModel().getCheckedIndices()) {
            if (i != -1) {
                checked_subgroups.add(view.getProductSubgroupFilter().getCheckModel().getItem(i));
            }
        }
        model.setProductSubgroupFilter(checked_subgroups);
        Platform.runLater(model::loadProducts);
    }

    private void updateOrdersFilters() {
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
