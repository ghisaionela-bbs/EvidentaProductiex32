package ro.brutariabaiasprie.evidentaproductiex32;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class ConfigApp {
    public static Map<String, Object> configuration = new Hashtable<>();
    private static File fileConfig;
    private static String fileConfigPath = "\\configApp.txt";

    public static void check_config() {
        fileConfigPath = System.getProperty("user.dir") + fileConfigPath;

        try {
            System.out.println("check config");
            fileConfig = new File(fileConfigPath);
            if (fileConfig.createNewFile()) {
                init_default_config();
                write_config();
            } else {
                System.out.println(fileConfig.getCanonicalPath());
                read_config();
                System.out.println("File already exists.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void read_config() {
        try {
            System.out.println("read config");
            Scanner myReader = new Scanner(fileConfig);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                int index = line.indexOf("=");
                configuration.put(line.substring(0, index), line.substring(index + 1));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void write_config() {
        try {
            System.out.println("write config");
            FileWriter myWriter = new FileWriter(fileConfigPath);
            for (Map.Entry<String, Object> entry : configuration.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                myWriter.write(key + "=" + val.toString() + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void init_default_config() {
        configuration.put("DBURL", "jdbc:sqlserver://192.168.3.145;databaseName=DB_EVIDENTA_PRODUCTIE;encrypt=false;");
        configuration.put("DBUSER", "sa");
        configuration.put("DBPASS", "sqlserverstatia51");
    }

    public static Object getConfig(String key) {
        return configuration.get(key);
    }
}
