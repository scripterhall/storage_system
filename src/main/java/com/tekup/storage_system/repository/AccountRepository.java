package com.tekup.storage_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekup.storage_system.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}
