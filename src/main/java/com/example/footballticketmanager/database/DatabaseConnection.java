package com.example.footballticketmanager.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try (InputStream is = DatabaseConnection.class.getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(is);
            URL      = props.getProperty("db.url");
            USER     = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger config.properties", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
