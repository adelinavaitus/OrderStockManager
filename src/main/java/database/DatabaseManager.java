package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static String url;
    private static String username;
    private static String password;

    static {
        try(InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("application.properties")){
            if(input == null){
                System.out.println("(Sorry, unable to find application.properties");
            }
            Properties properties = new Properties();
            properties.load(input);

            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }
}
