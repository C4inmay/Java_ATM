package com.atm.repository;

import com.atm.config.DBConnection;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBTransactionRepository {

    public void save(Transaction tx) {
        String sql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tx.getAccountNumber());
            stmt.setString(2, tx.getType().name());
            stmt.setBigDecimal(3, tx.getAmount());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> findByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Transaction tx = new Transaction(
                        rs.getString("account_number"),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("amount"),
                        null // timestamp optional for now
                );

                list.add(tx);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
