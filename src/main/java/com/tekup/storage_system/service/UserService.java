package com.tekup.storage_system.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tekup.storage_system.model.Authority;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.repository.AuthorityRepository;
import com.tekup.storage_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/pictures/";

    public User registerUser(User user, MultipartFile profilePicture) throws IOException {
        // Vérifiez si le répertoire d'upload existe, sinon créez-le
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        if (!profilePicture.isEmpty()) {
            // Nom unique pour l'image (timestamp) mouch uuid
            String fileName = System.currentTimeMillis() + "_" + profilePicture.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // Sauvegarder le fichier dans le répertoire
            Files.write(filePath, profilePicture.getBytes());
            user.setPicture("/pictures/" + fileName);
        } else {
            user.setPicture("/pictures/default.png");
        }

        Authority authority = new Authority();
        authority.setName("ROLE_USER");

        String hashPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPwd);
        User newUser = userRepository.save(user);

        authority.setUser(newUser);
        this.authorityRepository.save(authority);

        return newUser;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
