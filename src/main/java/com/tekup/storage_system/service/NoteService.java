package com.tekup.storage_system.service;


import com.tekup.storage_system.model.Note;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public List<Note> getNonArchivedNotesByUser(User user) {
        return noteRepository.findByUserAndArchivedFalse(user);
    }

    public List<Note> getArchivedNotesByUser(User user) {
        return noteRepository.findByUserAndArchivedTrue(user);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public Note updateArchivedStatus(Long noteId) {
        Note note = noteRepository.findById(noteId).orElse(null);
        if (note != null) {
            if (note.isArchived()) {
                note.setArchived(false);
            } else {
                note.setArchived(true);
            }
            noteRepository.save(note);
        }
        return note;
    }


}
