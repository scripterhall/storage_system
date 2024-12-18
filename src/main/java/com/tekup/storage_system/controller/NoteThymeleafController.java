package com.tekup.storage_system.controller;

import com.tekup.storage_system.model.Categorie;
import com.tekup.storage_system.model.Note;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.service.NoteService;
import com.tekup.storage_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NoteThymeleafController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public String getNotesPage(Model model, @RequestParam(defaultValue = "false") boolean archived, HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        List<Note> notes;

        if (archived) {
            notes = noteService.getArchivedNotesByUser(user);
        } else {
            notes = noteService.getNonArchivedNotesByUser(user);
        }

        String rappelMessage = "";
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        if ("/notes".equals(requestURI) && (queryString == null || queryString.isEmpty())) {
            rappelMessage = checkAndGetReminderMessage(user);
        }

        model.addAttribute("rappelMessage", rappelMessage);

        model.addAttribute("notes", notes);
        model.addAttribute("user", user);
        model.addAttribute("categories", com.tekup.storage_system.model.Categorie.values());
        model.addAttribute("pageTitle", "notes");
        model.addAttribute("content", "/notes/user-notes");
        return "base";
    }

    private String checkAndGetReminderMessage(User user) {
        List<Note> activeReminders = noteService.getNonArchivedNotesByUser(user);

        LocalDateTime now = LocalDateTime.now();

        List<Note> filteredNotes = activeReminders.stream()
                .filter(note -> note.getDateRappel() != null &&
                        note.getDateRappel().toLocalDate().equals(now.toLocalDate()) &&
                        note.getDateRappel().toLocalTime().isAfter(now.toLocalTime()))
                .collect(Collectors.toList());

        filteredNotes.forEach(note -> System.out.println("ID de la note: " + note.getId()));

        return filteredNotes.stream()
                .map(note -> " Your note '" + note.getObjet()+ "' is to be done today at "
                        + note.getDateRappel().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .findFirst()
                .orElse("");
    }

    @PostMapping("/notes/add")
    public String addNote(
            @RequestParam String objet,
            @RequestParam String description,
            @RequestParam Categorie categorie,
            @RequestParam(required = false, defaultValue = "false") boolean important,
            @RequestParam(required = false, defaultValue = "false") boolean rappelActive,
            @RequestParam(required = false) String rappelDate,
            Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            Note note = new Note();
            note.setObjet(objet);
            note.setDescription(description);
            note.setCategorie(categorie);
            note.setImportant(important);
            note.setDateCreation(LocalDateTime.now());
            note.setRappelActive(rappelActive);
            note.setUser(user);
            if (rappelDate != null && !rappelDate.isEmpty()) {
                note.setDateRappel(LocalDateTime.parse(rappelDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            noteService.save(note);
        }
        return "redirect:/notes";
    }

    @RequestMapping(value = "/notes/archive/{noteId}", method = RequestMethod.POST)
    public String updateNoteArchiveStatus(@PathVariable Long noteId) {
        Note updatedNote = noteService.updateArchivedStatus(noteId);

        if (updatedNote.isArchived() == true) {
            return "redirect:/notes?archived=true";
        } else {
            return "redirect:/notes?archived=false";
        }
    }

}
