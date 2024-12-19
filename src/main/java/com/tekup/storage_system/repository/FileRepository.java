package com.tekup.storage_system.repository;

import com.tekup.storage_system.model.File;
import com.tekup.storage_system.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByFolder(Folder folder);
}
