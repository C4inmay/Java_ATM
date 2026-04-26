package com.atm.repository;

import com.atm.config.DBConnection;
import com.atm.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBAccountRepository implements AccountRepository {

   
    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {

        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Account account = new Account(
                        rs.getString("account_number"),
                        rs.getString("pin"),
                        rs.getBigDecimal("balance")
                );

                return Optional.of(account);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    @Override
    public List<Account> findAll() {

        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Account(
                        rs.getString("account_number"),
                        rs.getString("pin"),
                        rs.getBigDecimal("balance")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    
    @Override
    public void save(Account account) {

        String sql = "UPDATE accounts SET balance = ?, pin = ? WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, account.getBalance());
            stmt.setString(2, account.getPin());
            stmt.setString(3, account.getAccountNumber());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
