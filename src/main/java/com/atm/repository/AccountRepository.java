package com.atm.repository;

import com.atm.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    // 🔍 Find account by account number
    Optional<Account> findByAccountNumber(String accountNumber);

    // 📋 Get all accounts
    List<Account> findAll();

    // 💾 Save or update account
    void save(Account account);
}