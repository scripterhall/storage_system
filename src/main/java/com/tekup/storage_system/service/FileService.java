package com.tekup.storage_system.service;

import com.tekup.storage_system.model.File;
import com.tekup.storage_system.model.Folder;
import com.tekup.storage_system.model.User;
import com.tekup.storage_system.repository.FileRepository;
import com.tekup.storage_system.repository.FolderRepository;
import com.tekup.storage_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FolderRepository folderRepository;


    @Autowired
    private UserRepository userRepository;
    private static final String DEFAULT_FOLDER_NAME = "All";
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public void uploadFile(MultipartFile file, String username, Long folderId) throws IOException {

        Folder folder;
        if (folderId != null) {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé"));
        } else {
            folder = getOrCreateDefaultFolderForUser(username);
        }

        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR + folder.getName());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            File uploadedFile = new File();
            uploadedFile.setName(fileName);
            uploadedFile.setRenamedFile(fileName);
            uploadedFile.setSize(file.getSize() / 1024 + "KB");
            uploadedFile.setType(file.getContentType());
            uploadedFile.setPath("/uploads/" + folder.getName() + "/" + fileName);
            uploadedFile.setFolder(folder);
            System.out.println("uploadedFile"+uploadedFile);
            fileRepository.save(uploadedFile);
        } else {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
    }
    public List<File> getFilesByFolder(Folder folder) {
        return fileRepository.findByFolder(folder);
    }

    public Folder getOrCreateDefaultFolderForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Folder folder = folderRepository.findByNameAndUser(DEFAULT_FOLDER_NAME, user);
        if (folder == null) {
            folder = new Folder();
            folder.setName(DEFAULT_FOLDER_NAME);
            folder.setUser(user);
            folderRepository.save(folder);
        }
        return folder;
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    public Folder createFolder(String folderName ,  String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setUser(user);
        return folderRepository.save(folder);
    }

    public List<Folder> getAllFoldersForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return folderRepository.findByUser(user);
    }

    public void deleteFile(Long fileId) {
        fileRepository.deleteById(fileId);
    }

    public void renameFile(Long id, String newName) {
        File file = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("File not found"));
        file.setRenamedFile(newName);
        fileRepository.save(file);
    }
    public void changeFolder(Long idfolder, Long IdFile) {
        Folder folder;
        File file = fileRepository.findById(IdFile).orElseThrow(() -> new IllegalArgumentException("File not found"));
         folder = folderRepository.findById(idfolder) .orElseThrow(() -> new IllegalArgumentException("Dossier non trouvé"));
        file.setFolder(folder);
        file.setPath("/uploads/" + folder.getName() + "/" + file.getName());
        fileRepository.save(file);
    }


}