package com.tekup.storage_system.restController;

import com.tekup.storage_system.model.Note;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.service.NoteService;
import com.tekup.storage_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
public class NoteRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @GetMapping("/my-notes")
    public List<Note> getNotes() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return noteService.getNonArchivedNotesByUser(user);
        }
        return null;
    }
}
