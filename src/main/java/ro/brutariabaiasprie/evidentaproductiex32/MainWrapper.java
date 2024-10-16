package ro.brutariabaiasprie.evidentaproductiex32;

import ro.brutariabaiasprie.evidentaproductiex32.Data.ConfigApp;
import java.util.Properties;

public class MainWrapper {
    public static void main(String[] args) {
//        Properties properties = System.getProperties();
//        properties.forEach((k, v) -> System.out.println(k + ":" + v));
        ConfigApp.check_config();
        EvidentaProductie.main(args);
    }
}
