package ro.brutariabaiasprie.evidentaproductie.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.brutariabaiasprie.evidentaproductie.Domain.UserRole;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class ConfigApp {
    public static Map<String, Object> configuration = new Hashtable<>();
    private static File fileConfig;
    private static final String fileConfigPath = System.getProperty("user.dir") + "\\configApp.txt";

    public static void check_config() {
        try {
            fileConfig = new File(fileConfigPath);
            // create the file on disk
            if (fileConfig.createNewFile()) {
                init_default_config();
                write_config();
            }
            // file already exists
            else {
                read_config();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read_config() {
        try {
            Scanner myReader = new Scanner(fileConfig);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                int index = line.indexOf("=");
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
                switch (key) {
                    case "APPUSER":
                        ObjectMapper objectMapper = new ObjectMapper();
                        configuration.put(key, objectMapper.readValue(value, User.class));
                        break;
                    case "USER": case "USER_ROLE":
                        break;
                    default:
                        configuration.put(line.substring(0, index), line.substring(index + 1));
                        break;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write_config() {
        try {
            FileWriter myWriter = new FileWriter(fileConfigPath);
            for (Map.Entry<String, Object> entry : configuration.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if(key.equals("APPUSER")){
                    ObjectMapper objectMapper = new ObjectMapper();
                    myWriter.write(key + "=" + objectMapper.writeValueAsString(val) + "\n");
                } else {
                    myWriter.write(key + "=" + val.toString() + "\n");
                }
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init_default_config() {
        String appDirPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\EvidentaProductie";
        setConfig(CONFIG_KEY.DBURL.name(), "jdbc:sqlserver://192.168.3.145;databaseName=DB_EVIDENTA_PRODUCTIE;encrypt=false;");
        setConfig(CONFIG_KEY.DBUSER.name(), "sa");
        setConfig(CONFIG_KEY.DBPASS.name(), "sqlserverstatia51");
        setConfig(CONFIG_KEY.ERRLOG_PATH.name(), appDirPath + "\\ErrorLog.txt");
        setConfig(CONFIG_KEY.EXCEL_EXPORT_PATH.name(), appDirPath + "\\Rapoarte excel");
        setConfig(CONFIG_KEY.APPDIR.name(), appDirPath);
    }

    public static Object getConfig(String key) {
        return configuration.get(key);
    }

    public static void setConfig(String key, Object value) {
        configuration.put(key, value);
    }

    public static void deleteConfig(String key) {
        configuration.remove(key);
    }

    public static User getUser() {
        return (User) configuration.get(CONFIG_KEY.APPUSER.name());
    }

    public static UserRole getRole() {
        return (UserRole) configuration.get(CONFIG_KEY.USER_ROLE.name());
    }

}
