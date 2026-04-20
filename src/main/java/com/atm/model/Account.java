package com.atm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final String pin;
    private BigDecimal balance;
    private final List<Transaction> transactions;
    private int failedLoginAttempts;
    private boolean locked;

    public Account(String accountNumber, String pin, BigDecimal initialBalance) {
        this(accountNumber, pin, initialBalance, 0, false, List.of());
    }

    public Account(
            String accountNumber,
            String pin,
            BigDecimal initialBalance,
            int failedLoginAttempts,
            boolean locked,
            List<Transaction> transactions
    ) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>(transactions);
        this.failedLoginAttempts = failedLoginAttempts;
        this.locked = locked;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public String getPin() {
        return pin;
    }

    public synchronized boolean isLocked() {
        return locked;
    }

    public synchronized int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public synchronized void recordFailedLoginAttempt(int maxAttempts) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= maxAttempts) {
            locked = true;
        }
    }

    public synchronized void resetFailedLoginAttempts() {
        failedLoginAttempts = 0;
    }

    public synchronized void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        balance = balance.add(amount);
    }

    public synchronized void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance.");
        }
        balance = balance.subtract(amount);
    }

    public synchronized void addTransaction(TransactionType type, BigDecimal amount) {
        transactions.add(new Transaction(type, amount, LocalDateTime.now()));
    }

    public synchronized List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }
}
