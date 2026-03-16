package com.example.jdbc;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/jdbc_demo";
    private static final String USER = "name";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}