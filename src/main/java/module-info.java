module ro.brutariabaiasprie.evidentaproductie {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires jxl;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.sql;

    opens ro.brutariabaiasprie.evidentaproductie to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductie;
    exports ro.brutariabaiasprie.evidentaproductie.DTO;
    opens ro.brutariabaiasprie.evidentaproductie.DTO to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductie.Data;
    opens ro.brutariabaiasprie.evidentaproductie.Data to javafx.fxml;
}