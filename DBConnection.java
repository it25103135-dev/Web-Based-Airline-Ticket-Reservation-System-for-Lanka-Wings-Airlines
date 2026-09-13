package com.lankawings.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=LankaWingsBookingManagementDB;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "Lankawings";
    private static final String DB_PASSWORD = "Lankawings123";

    private DBConnection() {}

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Microsoft SQL Server JDBC driver was not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
