package com.blps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = System.getenv().getOrDefault("DATABASE_URL", "jdbc:postgresql://localhost:5432/Movies");
    private static final String USER = System.getenv().getOrDefault("DATABASE_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("DATABASE_PASSWORD", "postgres");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}