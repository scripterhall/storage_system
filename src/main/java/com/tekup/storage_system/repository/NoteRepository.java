package com.tekup.storage_system.repository;

import com.tekup.storage_system.model.Note;
import com.tekup.storage_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserAndArchivedFalse(User user);
    List<Note> findByUserAndArchivedTrue(User user);
}
