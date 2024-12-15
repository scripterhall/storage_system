package com.tekup.storage_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekup.storage_system.model.Password;
import com.tekup.storage_system.repository.PasswordRepository;

@Service
public class PasswordService {
    @Autowired
    PasswordRepository passwordRepository;

    public List<Password> getAllPasswords() {
        return passwordRepository.findAll();
    }

    public Password getPasswordById(long id) {
        return passwordRepository.findById(id).orElse(null);
    }

    public Password savePassword(Password a) {
        return passwordRepository.save(a);
    }

    public Password updatePassword(Password a) {
        if (!a.equals(passwordRepository.findById(a.getId()).get()))
        return passwordRepository.saveAndFlush(a);
    else
        return a;
    }

    public void deletePassword(long id) {
        passwordRepository.delete(passwordRepository.findById(id).get());
    }
}
