package com.tekup.storage_system.repository;


import com.tekup.storage_system.model.Folder;
import com.tekup.storage_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Folder findByNameAndUser(String name, User user);
    List<Folder> findByUser(User user);
}
