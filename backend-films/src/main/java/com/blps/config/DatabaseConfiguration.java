package com.blps.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfiguration {

    private DatabaseConfiguration() {}

    private static final String URL = "jdbc:postgresql://localhost:5432/Movies";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            DriverManager.getConnection(URL);
            System.out.println("Database connection initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Getting database connection");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}