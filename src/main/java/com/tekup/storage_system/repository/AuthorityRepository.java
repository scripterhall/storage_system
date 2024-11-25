package com.tekup.storage_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tekup.storage_system.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
