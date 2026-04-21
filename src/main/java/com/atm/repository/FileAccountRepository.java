package com.atm.repository;

import com.atm.model.Account;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;

public class FileAccountRepository implements AccountRepository {

    private final Path dataDir;
    private final Path accountsFile;

    public FileAccountRepository(Path dataDir) {
        this.dataDir = dataDir;
        this.accountsFile = dataDir.resolve("accounts.csv");

        try {
            if (!dataDir.toFile().exists()) {
                dataDir.toFile().mkdirs();
            }

            if (!accountsFile.toFile().exists()) {
                accountsFile.toFile().createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return findAll().stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    @Override
    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(accountsFile.toFile()))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 3) {
                    String accNo = parts[0];
                    String pin = parts[1];
                    BigDecimal balance = new BigDecimal(parts[2]);

                    list.add(new Account(accNo, pin, balance));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void save(Account account) {
        List<Account> all = findAll();

        boolean updated = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getAccountNumber().equals(account.getAccountNumber())) {
                all.set(i, account);
                updated = true;
                break;
            }
        }

        if (!updated) {
            all.add(account);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(accountsFile.toFile()))) {
            for (Account acc : all) {
                bw.write(acc.getAccountNumber() + "," +
                        acc.getPin() + "," +
                        acc.getBalance());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}