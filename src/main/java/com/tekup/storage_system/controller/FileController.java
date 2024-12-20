package com.tekup.storage_system.controller;

import com.tekup.storage_system.model.File;
import com.tekup.storage_system.model.Folder;
import com.tekup.storage_system.repository.FolderRepository;
import com.tekup.storage_system.service.FileService;
import com.tekup.storage_system.service.UserService;
import com.tekup.storage_system.utils.PasswordChanger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private  FolderRepository folderRepository;



    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    static {
        // PDF
        MIME_TYPE_MAP.put("application/pdf", "pdf");

        // Word
        MIME_TYPE_MAP.put("application/msword", "word");
        MIME_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "word");

        // Excel
        MIME_TYPE_MAP.put("application/vnd.ms-excel", "excel");
        MIME_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "excel");

        // Compressed files
        MIME_TYPE_MAP.put("application/x-zip-compressed", "compressed");
        MIME_TYPE_MAP.put("application/zip", "compressed");
        MIME_TYPE_MAP.put("application/x-rar-compressed", "compressed");
        MIME_TYPE_MAP.put("application/x-7z-compressed", "compressed");

        // Vidéos
        MIME_TYPE_MAP.put("video/mp4", "video");
        MIME_TYPE_MAP.put("video/x-msvideo", "video");

        // Audio
        MIME_TYPE_MAP.put("audio/mpeg", "audio");
        MIME_TYPE_MAP.put("audio/wav", "audio");

        // Images
        MIME_TYPE_MAP.put("image/jpeg", "image");
        MIME_TYPE_MAP.put("image/png", "image");
        MIME_TYPE_MAP.put("image/gif", "image");

        // CSV
        MIME_TYPE_MAP.put("text/csv", "csv");

        // Par défaut, type inconnu
    }

    // Méthode pour simplifier le type MIME
    private String simplifyMimeType(String mimeType) {
        return MIME_TYPE_MAP.getOrDefault(mimeType, "unknown");
    }

    @RequestMapping("/Files")
    public String getFiles(@RequestParam(value = "folderId", required = false) Long folderId,
                           Authentication authentication,
                           Model model) {
        String username = authentication.getName();

        List<Folder> folders = fileService.getAllFoldersForUser(username);
        model.addAttribute("folders", folders);

        // If no folder is selected, get files from the "All" folder
        List<File> files;
        if (folderId == null) {
            Folder allFolder = fileService.getOrCreateDefaultFolderForUser(username);
            files = fileService.getFilesByFolder(allFolder);
        } else {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
            files = fileService.getFilesByFolder(folder);
        }

        files.forEach(file -> file.setType(simplifyMimeType(file.getType())));

        model.addAttribute("files", files);
        model.addAttribute("user", userService.getUserByUsername(username));
        model.addAttribute("pageTitle", "Files");
        model.addAttribute("content", "/media-library/Files");

        return "base";
    }

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("fileUpload") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            System.out.println("folderId"+folderId);
            fileService.uploadFile(file, username, folderId);
            redirectAttributes.addFlashAttribute("successMessage", "Fichier uploadé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'upload : " + e.getMessage());
        }
        return "redirect:/Files";
    }

    @PostMapping("/newFolder")
    public String createFolder(@RequestParam("folderName")   String folderName,Authentication authentication,
                               RedirectAttributes redirectAttributes) {
           String username = authentication.getName();
            Folder folder = fileService.createFolder(folderName, username);

            return "redirect:/Files"; // Redirect after successful folder creation

    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam("fileId") Long id) {
        fileService.deleteFile(id);
        return "redirect:/Files";  // Redirect after deletion
    }

    @PostMapping("/rename")
    public String renameFile(@RequestParam("idFileRename") Long id ,@RequestParam("fileRename") String fileRename) {
        fileService.renameFile(id,fileRename);
        return "redirect:/Files";  // Redirect after deletion
    }


}
