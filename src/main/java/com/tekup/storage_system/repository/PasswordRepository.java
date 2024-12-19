package com.tekup.storage_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tekup.storage_system.model.Password;

public interface PasswordRepository extends JpaRepository<Password, Long> {

}
