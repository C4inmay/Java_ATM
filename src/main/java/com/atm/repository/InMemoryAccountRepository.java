package com.atm.repository;

import com.atm.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accountStore;

    public InMemoryAccountRepository() {
        this.accountStore = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountStore.get(accountNumber));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accountStore.values());
    }

    @Override
    public void save(Account account) {
        accountStore.put(account.getAccountNumber(), account);
    }
}
