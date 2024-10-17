package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ro.brutariabaiasprie.evidentaproductiex32.DTO.ProductDTO;

public class ProductRecordController {
    @FXML
    VBox vBoxNumpad;

    @FXML
    ListView<ProductDTO> productDTOListView;
}
