package com.tekup.storage_system.restController;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekup.storage_system.model.Account;
import com.tekup.storage_system.model.Password;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.service.AccountService;
import com.tekup.storage_system.service.PasswordService;

@RestController
@RequestMapping(path = "accountpassrest")
public class AccountPasswordController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordService passwordService;

    @GetMapping("/generatepassword")
    public String generateRandomPassword() {

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialCharacters = "!@#$%^&*()-_+=<>?";

        String allCharacters = upperCase + lowerCase + digits + specialCharacters;

        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        for (int i = 4; i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        return shuffleString(password.toString(), random);
    }

    private String shuffleString(String input, SecureRandom random) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return new String(array);
    }

    @GetMapping("accounts")
    public List<Account> getAccountsByUser(User user) {
        return accountService.getAllAccountsByIdUser(user.getId());
    }

    @GetMapping("accounts/{id}/passwords")
    public List<Password> getAccountPasswords(@PathVariable long id) {
        return accountService.getAccountById(id).getPasswords();
    }
    @DeleteMapping("accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable long id) {
        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("accounts/{id}/passwords")
    public ResponseEntity<Void> deletePasswordsAccount(@PathVariable long id) {
        Account accountById = accountService.getAccountById(id);
        accountById.getPasswords().stream().forEach(
                password -> {
                    passwordService.deletePassword(password.getId());
                });
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("passwords/{id}")
    public ResponseEntity<Void> deletePassowrd(@PathVariable long id) {
        passwordService.deletePassword(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
