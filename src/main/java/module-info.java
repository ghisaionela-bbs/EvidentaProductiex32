module ro.brutariabaiasprie.evidentaproductie {
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires jxl;
    requires com.fasterxml.jackson.databind;
    requires org.dhatim.fastexcel.reader;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires jdk.compiler;
    requires org.dhatim.fastexcel;
    requires java.desktop;

    opens ro.brutariabaiasprie.evidentaproductie to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductie;
    exports ro.brutariabaiasprie.evidentaproductie.DTO;
    opens ro.brutariabaiasprie.evidentaproductie.DTO to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductie.Data;
    opens ro.brutariabaiasprie.evidentaproductie.Data to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductie.MVC;
    opens ro.brutariabaiasprie.evidentaproductie.MVC to javafx.fxml;
    exports ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Account;
    opens ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Account to javafx.fxml;
}