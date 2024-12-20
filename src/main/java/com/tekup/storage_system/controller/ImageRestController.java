package com.tekup.storage_system.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageRestController {

    @Value("${upload.dir}")
    private String uploadDir;

    // Endpoint to handle the file upload
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded.");
            }

            // Get the original filename
            String filename = StringUtils.cleanPath(file.getOriginalFilename());

            // Define the path to save the file
            Path targetLocation = Paths.get(uploadDir).resolve(filename);

            // Save the file to the target location
            Files.copy(file.getInputStream(), targetLocation);

            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("File upload failed.");
        }
    }

    // Endpoint to serve the image
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String filename) {
        try {
            // Locate the file on the server
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new FileSystemResource(filePath.toFile());

            // Check if the file exists
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Set the content type based on the file type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Return the image file as response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }
}


