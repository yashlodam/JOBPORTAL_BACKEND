package com.jobportal.utility;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for file storage operations.
 * Use LocalFileStorageServiceImpl for local disk.
 * Can be swapped for S3FileStorageServiceImpl for production cloud storage.
 */
public interface FileStorageService {

    /**
     * Stores a file and returns its relative URL path.
     *
     * @param file    the multipart file to store
     * @param subDir  subdirectory within the upload base (e.g., "profile", "logo", "resume")
     * @return relative URL path suitable for serving (e.g., "profile/uuid_filename.jpg")
     */
    String store(MultipartFile file, String subDir) throws Exception;

    /**
     * Deletes a previously stored file by its relative path.
     * Silently does nothing if the file does not exist.
     */
    void delete(String relativePath);
}
