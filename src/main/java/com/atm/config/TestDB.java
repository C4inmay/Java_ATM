package com.atm.config;

import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println(" Connected to SQL Server!");
        } catch (Exception e) {
            System.out.println(" Connection Failed");
            e.printStackTrace();
        }
    }
}