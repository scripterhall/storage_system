package com.tekup.storage_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekup.storage_system.model.Account;
import com.tekup.storage_system.repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    public List<Account> getAllAccountsByIdUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account getAccountById(long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account saveAccount(Account a) {
        return accountRepository.save(a);
    }

    public Account updateAccount(Account a) {
        return accountRepository.saveAndFlush(a);
    }

    public void deleteAccount(long id) {
        accountRepository.deleteById(id);
    }
}
