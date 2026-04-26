package com.atm.repository;

import com.atm.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

   
    Optional<Account> findByAccountNumber(String accountNumber);

 
    List<Account> findAll();

    void save(Account account);
}
