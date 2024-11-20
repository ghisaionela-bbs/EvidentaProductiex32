package ro.brutariabaiasprie.evidentaproductie.Services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Properties;

public class AppProperties {
    public static final Properties properties = new Properties();

    public static void load() {
        String appConfigPath = Objects.requireNonNull(DBConnectionService.class.getClassLoader().getResource("app.properties")).getPath();
        try {
            properties.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void store() {
        StringWriter stringWriter = new StringWriter();
        try {
            properties.store(stringWriter, "Properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void put(Object key, Object value) {
        properties.put(key, value);
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}

