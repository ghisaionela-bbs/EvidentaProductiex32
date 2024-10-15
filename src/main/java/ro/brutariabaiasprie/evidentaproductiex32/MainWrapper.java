package ro.brutariabaiasprie.evidentaproductiex32;

import ro.brutariabaiasprie.evidentaproductiex32.Data.User;

import java.util.Properties;

public class MainWrapper {
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        // Java 8
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
        ConfigApp.check_config();
        EvidentaProductie.main(args);
    }
}
