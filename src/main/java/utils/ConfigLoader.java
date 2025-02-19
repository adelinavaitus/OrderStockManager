package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// Loads configuration properties from application.properties
public class ConfigLoader {

    // Properties object to hold the loaded properties
    private static final Properties properties = new Properties();

    // Static block to load properties from the application.properties file
    static {
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieves a property value by its key
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
