package com.blps.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfiguration {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .systemProperties()
            .load();

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        String tempUrl = null;
        String tempUser = null;
        String tempPassword = null;
        try {
            Class.forName("org.postgresql.Driver");
            tempUrl = dotenv.get("DB_URL");
            tempUser = dotenv.get("DB_USER");
            tempPassword = dotenv.get("DB_PASSWORD");

            System.out.println("🔹 DatabaseConfiguration initialized");
            System.out.println("🔹 DB_URL = " + tempUrl);
            System.out.println("🔹 DB_USER = " + tempUser);
            System.out.println("🔹 DB_PASSWORD = " + (tempPassword != null ? "******" : null));

        } catch (Exception e) {
            e.printStackTrace();
        }

        URL = tempUrl;
        USER = tempUser;
        PASSWORD = tempPassword;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
