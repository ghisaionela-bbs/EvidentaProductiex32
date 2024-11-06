package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.AddProductGroup;

import ro.brutariabaiasprie.evidentaproductie.DatabaseObjects.OProductGroup;

public class AddProductGroupModel {
    private final OProductGroup productGroup = new OProductGroup();

    public OProductGroup getProductGroup() {
        return productGroup;
    }
}
