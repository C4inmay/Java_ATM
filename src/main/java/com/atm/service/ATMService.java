package com.atm.service;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;
import com.atm.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ATMService {
    private final AccountRepository accountRepository;

    public ATMService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BigDecimal getBalance(Account account) {
        return account.getBalance();
    }

    public void deposit(Account account, BigDecimal amount) throws ATMOperationException {
        try {
            account.deposit(amount);
            account.addTransaction(TransactionType.DEPOSIT, amount);
            accountRepository.save(account);
        } catch (IllegalArgumentException e) {
            throw new ATMOperationException(e.getMessage());
        }
    }

    public void withdraw(Account account, BigDecimal amount) throws ATMOperationException {
        try {
            account.withdraw(amount);
            account.addTransaction(TransactionType.WITHDRAW, amount);
            accountRepository.save(account);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ATMOperationException(e.getMessage());
        }
    }

    public List<Transaction> getTransactionHistory(Account account) {
        return account.getTransactionHistory();
    }

    public List<Transaction> getRecentTransactions(Account account, int count) {
        List<Transaction> allTransactions = account.getTransactionHistory();
        if (allTransactions.isEmpty()) {
            return Collections.emptyList();
        }

        int fromIndex = Math.max(0, allTransactions.size() - count);
        return allTransactions.subList(fromIndex, allTransactions.size());
    }
}
