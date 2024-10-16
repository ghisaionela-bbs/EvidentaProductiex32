module ro.brutariabaiasprie.evidentaproductiex32 {
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

    opens ro.brutariabaiasprie.evidentaproductiex32 to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductiex32;
    exports ro.brutariabaiasprie.evidentaproductiex32.DTO;
    opens ro.brutariabaiasprie.evidentaproductiex32.DTO to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductiex32.Data;
    opens ro.brutariabaiasprie.evidentaproductiex32.Data to javafx.fxml;
}