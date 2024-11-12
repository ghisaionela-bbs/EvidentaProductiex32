package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.DTO.Group;
import ro.brutariabaiasprie.evidentaproductie.DTO.Product;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupModel;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Group.GroupView;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

public class ProductController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private ProductModel model;
    private ProductView view;

    public ProductController(Stage owner, WINDOW_TYPE type) {
        initStage(owner, type, new Product());
    }

    public ProductController(Stage owner, WINDOW_TYPE type, Product group) {
        initStage(owner, type, group);
    }

    private void initStage(Stage owner, WINDOW_TYPE type, Product group) {
        this.type = type;
        this.stage = new Stage();
        this.model = new ProductModel();
        this.model.setProduct(group);
        this.view = new ProductView(this.model, type, this::onWindowAction);
        this.view.setDeleteProductHandler(this::deleteProduct);
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        switch (type) {
            case ADD:
                stage.setTitle("Adaugare grupa");
                break;
            case EDIT:
                stage.setTitle("Editare grupa");
                break;
            case VIEW:
                stage.setTitle("Vizualizare grupa");
                break;
        }

        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    private void deleteProduct() {
        if(new ConfirmationController(stage, "Confirmati stergerea",
                String.format("Sunteti sigur ca doriti sa stergeti produsul %s?", model.getProduct().getName())).isSUCCESS()) {
            model.deleteProduct();
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
                model.updateProduct();
            }
        }
        stage.close();
    }
}
