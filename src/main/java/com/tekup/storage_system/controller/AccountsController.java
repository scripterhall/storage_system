package com.tekup.storage_system.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tekup.storage_system.model.Account;
import com.tekup.storage_system.model.Password;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.service.AccountService;
import com.tekup.storage_system.service.PasswordService;
import com.tekup.storage_system.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final UserService userService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/")
    public String listPage(Authentication auth, Model model) {
        model.addAttribute("content", "accounts/list");
        User currentUser = userService.getUserByUsername(auth.getName());
        model.addAttribute("user", currentUser);
        List<Account> accounts = accountService.getAllAccountsByIdUser(currentUser.getId());
        accounts.stream().forEach(account -> {
            account.setPasswords(account.getPasswords().stream().filter(Password::isActive).toList());
        });
        model.addAttribute("accounts", accounts);
        Account account = new Account();
        account.setPasswords(new ArrayList<>());
        model.addAttribute("account", account);
        return "base";
    }

    @GetMapping(path = "/{id}")
    public String displayDetailAccount(@PathVariable long id, Authentication auth, Model model) {
        Account account = accountService.getAccountById(id);
        User currentUser = userService.getUserByUsername(auth.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("content", "accounts/detail");
        model.addAttribute("account", account);
        return "base";
    }

    @PostMapping("/save")
    public String saveAccount(Authentication auth, @ModelAttribute("account") Account account, Model model) {

        User currentUser = userService.getUserByUsername(auth.getName());

        account.setUser(currentUser);
        Account accountSaved = accountService.saveAccount(account);
        List<Password> passwords = account.getPasswords();
        Date currentDate = new Date(System.currentTimeMillis());

        passwords.stream().forEach(a -> {
            a.setAccount(accountSaved);
            a.setDateCreation(currentDate);
            passwordService.savePassword(a);
        });
        return "redirect:/accounts/";
    }

    @PostMapping("/update/{id}")
    public String modifyAccount(Authentication auth, @PathVariable long id, Account entity) {
        User currentUser = userService.getUserByUsername(auth.getName());
        entity.setUser(currentUser);
        Date currentDate = new Date(System.currentTimeMillis());
        entity.getPasswords().stream().forEach(password -> {
            // If password exist
            if (password.getValue() == null || password.getValue().isEmpty()) {
                passwordService.deletePassword(password.getId());
                return;
            } else if (password.getAccount() != null) {
                passwordService.updatePassword(password);
            } else {
                password.setDateCreation(currentDate);
                password.setAccount(entity);
                passwordService.savePassword(password);
            }
        });
        accountService.updateAccount(entity);

        return "redirect:/accounts/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteAccount(@PathVariable long id) {
        Account accountById = accountService.getAccountById(id);
        ;
        accountById.getPasswords().stream().forEach(pass -> {
            passwordService.deletePassword(pass.getId());
        });
        accountService.deleteAccount(id);
        return "redirect:/accounts/";
    }

}
