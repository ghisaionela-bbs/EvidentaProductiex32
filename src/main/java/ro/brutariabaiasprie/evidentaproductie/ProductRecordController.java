package ro.brutariabaiasprie.evidentaproductie;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ro.brutariabaiasprie.evidentaproductie.Controllers.IController;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;

public class ProductRecordController implements IController {
    @FXML
    VBox vBoxNumpad;

    @FXML
    ListView<ProductDTO> productDTOListView;
}
