package ro.brutariabaiasprie.evidentaproductie;

import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Services.AppProperties;

/**
 * Main class of any .jar executable
 */
public class MainWrapper {
    /**
     * Launches the app through the .jar executable
     * @param args - main args
     */
    public static void main(String[] args) {
//        Properties properties = System.getProperties();
//        properties.forEach((k, v) -> System.out.println(k + ":" + v));
//        AppProperties.load();
        ConfigApp.check_config();
        EvidentaProductie.main(args);
    }
}
