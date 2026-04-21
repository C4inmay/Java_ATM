package com.atm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private String accountNumber;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Transaction(String accountNumber, TransactionType type, BigDecimal amount, LocalDateTime timestamp) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}