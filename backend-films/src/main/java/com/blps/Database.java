package com.blps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private Database() {}

    private static final String URL = "jdbc:postgresql://localhost:5432/Movies";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}