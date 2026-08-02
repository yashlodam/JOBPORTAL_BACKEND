package com.jobportal.utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.exception.JobPortalException;

/**
 * Local disk implementation of FileStorageService.
 * Files are stored under ${file.upload.base-dir}/{subDir}/{uuid}_{originalName}.
 */
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private final String uploadBaseDir;

    public LocalFileStorageServiceImpl(
            @Value("${file.upload.base-dir}") String uploadBaseDir) {
        this.uploadBaseDir = uploadBaseDir;
    }

    @Override
    public String store(MultipartFile file, String subDir) throws Exception {
        if (file == null || file.isEmpty()) {
            throw JobPortalException.badRequest("File must not be empty");
        }

        String contentType = file.getContentType();
        boolean isResume = "resume".equals(subDir);
        Set<String> allowed = isResume ? ALLOWED_DOCUMENT_TYPES : ALLOWED_IMAGE_TYPES;

        if (contentType == null || !allowed.contains(contentType)) {
            String types = isResume ? "PDF, DOC, DOCX" : "JPEG, PNG, WEBP, GIF";
            throw JobPortalException.badRequest("Invalid file type. Allowed: " + types);
        }

        String directory = uploadBaseDir + "/" + subDir + "/";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String sanitizedName = sanitize(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "_" + sanitizedName;
        Path destinationPath = Paths.get(directory + fileName);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return subDir + "/" + fileName;
    }

    @Override
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Path path = Paths.get(uploadBaseDir + "/" + relativePath);
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
            // Non-critical — log in production
        }
    }

    private String sanitize(String originalFilename) {
        if (originalFilename == null) return "file";
        return originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
