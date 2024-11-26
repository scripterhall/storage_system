package com.tekup.storage_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tekup.storage_system.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
