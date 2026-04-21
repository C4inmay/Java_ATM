package com.atm.service;
import com.atm.repository.DBTransactionRepository;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;
import com.atm.repository.AccountRepository;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ATMService {

    private final AccountRepository accountRepository;
    private DBTransactionRepository txRepo = new DBTransactionRepository();



    public ATMService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // 💰 CHECK BALANCE
    public BigDecimal getBalance(Account account) {
        return account.getBalance();
    }

    // 💰 DEPOSIT
    public void deposit(Account account, BigDecimal amount) throws ATMOperationException {
        try {
            account.deposit(amount);
            accountRepository.save(account);

            txRepo.save(new Transaction(
                    account.getAccountNumber(),
                    TransactionType.DEPOSIT,
                    amount,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            throw new ATMOperationException("Deposit failed");
        }
    }

    // 💸 WITHDRAW
    public void withdraw(Account account, BigDecimal amount) throws ATMOperationException {
        try {
            account.withdraw(amount);
            accountRepository.save(account);

            txRepo.save(new Transaction(
                    account.getAccountNumber(),
                    TransactionType.WITHDRAW,
                    amount,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            throw new ATMOperationException("Withdrawal failed");
        }
    }

    // 📜 TRANSACTION HISTORY
    public List<Transaction> getTransactionHistory(Account account) {
        if (account.getTransactions() == null) {
            return Collections.emptyList();
        }
        return account.getTransactions();
    }

    public List<Transaction> getRecentTransactions(Account account, int count) {

        List<Transaction> allTransactions = account.getTransactions();

        if (allTransactions == null || allTransactions.isEmpty()) {
            return new ArrayList<>();
        }

        int size = allTransactions.size();

        // return last 'count' transactions
        return allTransactions.subList(
                Math.max(0, size - count),
                size
        );
    }
}