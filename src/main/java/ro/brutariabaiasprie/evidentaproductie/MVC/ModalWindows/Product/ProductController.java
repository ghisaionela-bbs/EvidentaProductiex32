package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Product;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Data.ACTION_TYPE;
import ro.brutariabaiasprie.evidentaproductie.Data.Images;
import ro.brutariabaiasprie.evidentaproductie.Data.WINDOW_TYPE;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Confirmation.ConfirmationController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.Dialogues.Warning.WarningController;
import ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ModalWindow;

import java.util.Objects;

/***
 * The ProductController class used to manage product information
 * It can be used either for adding a new product, editing or viewing a given product
 */
public class ProductController extends ModalWindow {
    private WINDOW_TYPE type;
    private Stage stage;
    private ProductModel model;
    private ProductView view;

    /***
     * Constructs an instance of ProductController for creating a new product
     * @param owner the owner for the new scene that will be created
     */
    public ProductController(Stage owner) {
        initStage(owner, WINDOW_TYPE.ADD, new Product());
    }

    /***
     * Constructs an instance of ProductController for editing or viewing an existing product
     * @param owner the owner for the new scene that will be created
     * @param type the type of the view that we want. It can be ADD, EDIT or VIEW
     * @param product the product that will be edited or viewed
     */
    public ProductController(Stage owner, WINDOW_TYPE type, Product product) throws RuntimeException {
        initStage(owner, type, product);
    }

    /***
     * Initializes the model, view, scene and stage of the controller
     * @param owner the owner for the new scene that will be created
     * @param type the type of the view that we want. It can be ADD, EDIT or VIEW
     * @param product the product of the model.
     *                When the type is ADD, it will be a new instance, ready to be set by the user.
     *                When the type is EDIT or VIEW, the product will be an already existing instance.
     */
    private void initStage(Stage owner, WINDOW_TYPE type, Product product) {
        this.type = type;
        this.stage = new Stage();
        this.model = new ProductModel();
        this.model.setProduct(product);
        this.view = new ProductView(this.model, this.stage, type, this::onWindowAction);
        this.view.setDeleteProductHandler(this::deleteProduct);
        this.view.groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(oldValue != newValue) {
                if(newValue == null) {
                    this.view.subgroupComboBox.getSelectionModel().clearSelection();
                    this.view.subgroupComboBox.setDisable(true);
                } else if(newValue.getId() == 0) {
                    this.view.subgroupComboBox.setDisable(true);
                } else {
                    this.view.subgroupComboBox.setDisable(false);
                }
                model.loadSubgroups(newValue);
            }
        });
        this.runDatabaseTask(() -> {
            model.loadGroups();
            model.loadSubgroups(product.getGroup());
            view.loadProductData();
        });
        Scene scene = new Scene(this.view.build());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ro/brutariabaiasprie/evidentaproductie/styles.css")).toExternalForm());

        switch (type) {
            case ADD:
                stage.setTitle("Adaugare produs");
                break;
            case EDIT:
                stage.setTitle("Editare produs");
                break;
            case VIEW:
                stage.setTitle("Vizualizare produs");
                break;
        }

        this.stage.getIcons().addAll(Images.icon16x16, Images.icon32x32, Images.icon64x64);
        this.stage.setScene(scene);
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    /***
     * Deletes the product loaded in the model.
     * Can be called only when the WINDOW_TYPE is EDIT.
     */
    private void deleteProduct() {
        if(new ConfirmationController(stage, "Confirmati stergerea",
                String.format("Sunteti sigur ca doriti sa stergeti produsul %s?", model.getProduct().getName())).isSUCCESS()) {
            model.deleteProduct();
            stage.close();
        }
    }

    /***
     * The handler for the main buttons of the scene
     * @param actionType the type of action that was triggered. It can be either CONFIRMATION OR CANCELLATION.
     */
    @Override
    protected void onWindowAction(ACTION_TYPE actionType) {
        if(actionType == ACTION_TYPE.CONFIRMATION) {
            // Validate the data from the view
            if(!this.isInputValid(type)) {
                return;
            }

            double batchValue = 0.00;
            if(!view.getBatchValue().isEmpty()) {
                batchValue = Double.parseDouble(view.getBatchValue());
            }
            if(batchValue == 0.00) {
                if(!new ConfirmationController(stage, "Atentie!",
                        "Daca nu setati cantitatea unei sarje, nu se va afisa procentajul de completare a comenzilor pe acest produs.\n" +
                                "Continuati?").isSUCCESS()) {
                    return;
                }
            }
            // Set the data in the model
            model.getProduct().setName(view.getName());
            model.getProduct().setBatchValue(batchValue);
            model.getProduct().setUnitMeasurement(view.getUnitMeasurement());
            model.getProduct().setGroup(view.getGroup());
            model.getProduct().setSubgroupId(view.getSubgroup());

            // If the type of view is ADD, when the CONFIRMATION button is pressed,
            // the product will be added in the database
            if(type == WINDOW_TYPE.ADD) {
                model.addProduct();
            }
            // If the type of view is EDIT, when the CONFIRMATION button is pressed,
            // the product will be updated in the database
            else if (type == WINDOW_TYPE.EDIT) {
                model.updateProduct();
            }
        }
        stage.close();
    }

    /***
     * Validates the data of the controls from the ProductView
     * @return returns true if it gets past checks
     */
    private boolean isInputValid(WINDOW_TYPE type) {
        if(view.getName().isEmpty()) {
            new WarningController(stage, "Completati denumirea produsului!");
            return false;
        }
        if(view.getUnitMeasurement().isEmpty()) {
            new WarningController(stage, "Selectati unitatea de masura a produsului!");
            return false;
        }
        if(view.getGroup() == null) {
            String message = "";
            if(type == WINDOW_TYPE.ADD) {
                message = "Sunteti sigur ca doriti sa introduce produsul fara o grupa?";
            } else if (type == WINDOW_TYPE.EDIT) {
                message = "Sunteti sigur ca doriti sa actualizati produsul fara o grupa?";
            }
            if(!new ConfirmationController(stage, "Confirmare", message).isSUCCESS()) {
                return false;
            }
        }
        return true;
    }
}
