package com.atm.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=ATM_DB;encrypt=true;trustServerCertificate=true;integratedSecurity=true;";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}