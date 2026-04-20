package com.atm;

import com.atm.model.Account;
import com.atm.repository.FileAccountRepository;
import com.atm.service.ATMService;
import com.atm.service.AuthenticationService;
import com.atm.ui.ATMConsoleUI;

import java.math.BigDecimal;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        FileAccountRepository accountRepository = new FileAccountRepository(Paths.get("data"));

        if (accountRepository.findAll().isEmpty()) {
            accountRepository.save(new Account("1001001", "1234", new BigDecimal("1500.00")));
            accountRepository.save(new Account("1001002", "4321", new BigDecimal("2450.50")));
            accountRepository.save(new Account("1001003", "1111", new BigDecimal("500.00")));
        }

        AuthenticationService authenticationService = new AuthenticationService(accountRepository, 3);
        ATMService atmService = new ATMService(accountRepository);

        ATMConsoleUI ui = new ATMConsoleUI(authenticationService, atmService);
        ui.start();
    }
}
