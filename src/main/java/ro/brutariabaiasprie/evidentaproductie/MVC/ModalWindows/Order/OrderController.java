package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Order;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class OrderController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private OrderModel model;
    private OrderView view;


    public OrderController(Stage owner, WINDOW_TYPE type) {
        initStage(owner, type, new Order());
    }

    public OrderController(Stage owner, WINDOW_TYPE type, Order order) {
        initStage(owner, type, order);
    }

    private void initStage(Stage owner, WINDOW_TYPE type, Order order) {
        this.type = type;
        this.stage = new Stage();
        this.model = new OrderModel();
        this.model.setOrder(order);
        this.runDatabaseTask(model::loadProducts);
        this.view = new OrderView(this.model, type, this::onWindowAction);
        this.view.setDeleteOrderHandler(this::deleteOrder);
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        switch (type) {
            case ADD:
                stage.setTitle("Adaugare comanda");
                break;
            case EDIT:
                stage.setTitle("Editare comanda");
                break;
            case VIEW:
                stage.setTitle("Vizualizare comanda");
                break;
        }

        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    private void deleteOrder() {
        String message = "Sunteti sigur ca doriti sa stergeti comanda %d?\n\n" +
                "COMANDA ACEASTA VA FI STEARSA DE LA TOATE REALIZARILE\n" +
                "ASOCIATE LA ACEASTA GRUPA\n\n" +
                "!!! ACEASTA ACTIUNE ESTE IREVERSIBILA !!!";
        if(new ConfirmationController(stage, "Confirmati stergerea",
                String.format(message, model.getOrder().getId())).isSUCCESS()) {
            model.deleteOrder();
            stage.close();
        }
    }

    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            if(!isInputValid(type)) {
                return;
            }

            model.getOrder().setProduct(view.getProduct());
            model.getOrder().setQuantity(Double.parseDouble(view.getQuantityInput()));
            model.getOrder().setClosed(view.isClosed());

            if(type == WINDOW_TYPE.ADD) {
                model.addOrder();
            } else if (type == WINDOW_TYPE.EDIT) {
                if(model.hasRecords()) {
                    String message = "Sunteti sigur ca doriti sa modificati produsul?\n\n" +
                            "EXISTA REALIZARI ASOCIATE ACESTEI COMENZI CU PRODUSUL INITIAL\n" +
                            "SCHIMBAREA PRODUSUL VA STERGE COMANDA CURENTA DE LA REALIZARILE RESPECTIVE\n\n" +
                            "!!! ACEASTA ACTIUNE ESTE IREVERSIBILA !!!";
                    if(!new ConfirmationController(stage, "Confirmati modificarea produsului",
                            String.format(message, model.getOrder().getId())).isSUCCESS()) {
                        return;
                    }
                    model.updateOrder(true);
                } else {
                    model.updateOrder(false);
                }
            }
        }
        stage.close();
    }

    private boolean isInputValid(WINDOW_TYPE type) {
        if(view.getProduct() == null) {
            new WarningController(stage, "Selectati produsul pentru care doriti sa plasati comanda!");
            return false;
        }
        if(view.getQuantityInput() == null) {
            new WarningController(stage, "Introduceti cantitatea!");
            return false;
        }
        if(view.getQuantityInput().isEmpty()) {
            new WarningController(stage, "Introduceti cantitatea!");
            return false;
        }
        double quantity = Double.parseDouble(view.getQuantityInput());
        if(quantity <= 0) {
            new WarningController(stage, "Cantitatea trebuie sa fie mai mare de 0!");
            return false;
        }
        return true;
    }

    /***
     * Runs a task on a different thread for database operations
     * @param runnable the runnable that will be run by the task
     */
    private void runDatabaseTask(Runnable runnable) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}
