package ro.brutariabaiasprie.evidentaproductie;

import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;

public class MainWrapper {
    public static void main(String[] args) {
//        Properties properties = System.getProperties();
//        properties.forEach((k, v) -> System.out.println(k + ":" + v));
        ConfigApp.check_config();
        EvidentaProductie.main(args);
    }
}
