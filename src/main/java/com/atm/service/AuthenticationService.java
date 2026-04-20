package com.atm.service;

import com.atm.model.Account;
import com.atm.repository.AccountRepository;

import java.util.Optional;

public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final int maxAttempts;

    public AuthenticationService(AccountRepository accountRepository, int maxAttempts) {
        this.accountRepository = accountRepository;
        this.maxAttempts = maxAttempts;
    }

    public Account authenticate(String accountNumber, String pin) throws AuthenticationException {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isEmpty()) {
            throw new AuthenticationException("Invalid account number or PIN.");
        }

        Account account = accountOptional.get();
        if (account.isLocked()) {
            throw new AuthenticationException("Account is locked due to repeated failed login attempts.");
        }

        if (account.verifyPin(pin)) {
            account.resetFailedLoginAttempts();
            accountRepository.save(account);
            return account;
        }

        account.recordFailedLoginAttempt(maxAttempts);
        accountRepository.save(account);

        if (account.isLocked()) {
            throw new AuthenticationException("Account locked after " + maxAttempts + " failed attempts.");
        }

        int remainingAttempts = maxAttempts - account.getFailedLoginAttempts();
        throw new AuthenticationException("Invalid account number or PIN. Remaining attempts for this account: " + remainingAttempts);
    }
}
