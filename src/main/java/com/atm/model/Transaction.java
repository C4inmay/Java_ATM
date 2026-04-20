package com.atm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(TransactionType type, BigDecimal amount, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
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
