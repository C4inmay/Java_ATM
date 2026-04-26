package com.atm.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private String accountNumber;
    private String pin;
    private BigDecimal balance;

    private int failedLoginAttempts = 0;
    private boolean locked = false;

    private List<Transaction> transactions = new ArrayList<>();


    public Account(String accountNumber, String pin, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public Account(String accountNumber,
                   String pin,
                   BigDecimal balance,
                   int failedLoginAttempts,
                   boolean locked,
                   List<Transaction> transactions) {

        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.failedLoginAttempts = failedLoginAttempts;
        this.locked = locked;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
    public List<Transaction> getTransactionHistory() {
        return transactions;
    }

    public boolean verifyPin(String inputPin) {
        return this.pin.equals(inputPin);
    }
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void recordFailedLoginAttempt(int maxAttempts) {
        failedLoginAttempts++;

        if (failedLoginAttempts >= maxAttempts) {
            locked = true;
        }
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid withdraw amount");
        }

        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        balance = balance.subtract(amount);
    }

  
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
