package com.atm.repository;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileAccountRepository implements AccountRepository {
    private static final String ACCOUNTS_HEADER = "accountNumber,pin,balance,failedLoginAttempts,locked";
    private static final String TRANSACTIONS_HEADER = "accountNumber,type,amount,timestamp";

    private final Map<String, Account> accountStore;
    private final Path accountsFile;
    private final Path transactionsFile;

    public FileAccountRepository(Path dataDirectory) {
        this.accountStore = new ConcurrentHashMap<>();
        this.accountsFile = dataDirectory.resolve("accounts.csv");
        this.transactionsFile = dataDirectory.resolve("transactions.csv");
        initializeStorage(dataDirectory);
        loadFromFiles();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountStore.get(accountNumber));
    }

    @Override
    public List<Account> findAll() {
        return accountStore.values().stream()
                .sorted((a, b) -> a.getAccountNumber().compareTo(b.getAccountNumber()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public synchronized void save(Account account) {
        accountStore.put(account.getAccountNumber(), account);
        persistAll();
    }

    private void initializeStorage(Path dataDirectory) {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create data directory: " + dataDirectory, e);
        }
    }

    private void loadFromFiles() {
        if (!Files.exists(accountsFile)) {
            return;
        }

        Map<String, List<Transaction>> transactionMap = loadTransactions();

        try {
            List<String> lines = Files.readAllLines(accountsFile);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty() || i == 0) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 5) {
                    continue;
                }

                String accountNumber = parts[0].trim();
                String pin = parts[1].trim();
                BigDecimal balance = new BigDecimal(parts[2].trim());
                int failedAttempts = Integer.parseInt(parts[3].trim());
                boolean locked = Boolean.parseBoolean(parts[4].trim());

                List<Transaction> transactions = transactionMap.getOrDefault(accountNumber, List.of());
                Account account = new Account(accountNumber, pin, balance, failedAttempts, locked, transactions);
                accountStore.put(accountNumber, account);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read account data from file.", e);
        }
    }

    private Map<String, List<Transaction>> loadTransactions() {
        Map<String, List<Transaction>> transactionMap = new HashMap<>();

        if (!Files.exists(transactionsFile)) {
            return transactionMap;
        }

        try {
            List<String> lines = Files.readAllLines(transactionsFile);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty() || i == 0) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    continue;
                }

                String accountNumber = parts[0].trim();
                TransactionType type = TransactionType.valueOf(parts[1].trim());
                BigDecimal amount = new BigDecimal(parts[2].trim());
                LocalDateTime timestamp = LocalDateTime.parse(parts[3].trim());

                transactionMap.computeIfAbsent(accountNumber, key -> new ArrayList<>())
                        .add(new Transaction(type, amount, timestamp));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read transaction data from file.", e);
        }

        return transactionMap;
    }

    private void persistAll() {
        List<String> accountLines = new ArrayList<>();
        accountLines.add(ACCOUNTS_HEADER);

        List<String> transactionLines = new ArrayList<>();
        transactionLines.add(TRANSACTIONS_HEADER);

        List<Account> accounts = findAll();
        for (Account account : accounts) {
            accountLines.add(String.join(",",
                    account.getAccountNumber(),
                    account.getPin(),
                    account.getBalance().toPlainString(),
                    String.valueOf(account.getFailedLoginAttempts()),
                    String.valueOf(account.isLocked())
            ));

            for (Transaction transaction : account.getTransactionHistory()) {
                transactionLines.add(String.join(",",
                        account.getAccountNumber(),
                        transaction.getType().name(),
                        transaction.getAmount().toPlainString(),
                        transaction.getTimestamp().toString()
                ));
            }
        }

        try {
            Files.write(accountsFile, accountLines);
            Files.write(transactionsFile, transactionLines);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to persist account and transaction data.", e);
        }
    }
}
