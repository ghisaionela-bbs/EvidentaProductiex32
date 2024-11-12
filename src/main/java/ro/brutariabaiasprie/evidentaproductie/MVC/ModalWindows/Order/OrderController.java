package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.Order;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

public class OrderController extends ModalWindow {
    private final WINDOW_TYPE type;
    private final Stage stage;
    private final OrderModel model;
    private final OrderView view;

    public OrderController(Stage owner, WINDOW_TYPE type, Order order) {
        this.type = type;
        stage = new Stage();
        model = new OrderModel();
        model.setOrder(order);
        view = new OrderView(model, type, this::onWindowAction);
        view.setDeleteOrderHandler(this::deleteOrder);
    }

    private void deleteOrder() {
        if(new ConfirmationController(stage, "Confirmati stergerea",
                String.format("Sunteti sigur ca doriti sa stergeti grupa %d?", model.getOrder().getId())).isSUCCESS()) {
            model.deleteOrder();
            stage.close();
        }
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(type == WINDOW_TYPE.ADD) {
                var a = 1;
//                model.addOrder();
            } else if (type == WINDOW_TYPE.EDIT) {
//                model.getGroup().setName(view.getName());
                model.updateOrder();
            }
        }
        stage.close();
    }
}
