package database;

import utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static String url = ConfigLoader.getProperty("db.url");
    private static String username = ConfigLoader.getProperty("db.username");
    private static String password  = ConfigLoader.getProperty("db.password");

    // Method to establish and return a database connection
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }
}
