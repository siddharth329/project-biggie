package com.club69.commons.util;

import com.club69.commons.model.MediaFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for file operations.
 * This class provides common methods for handling files across different services.
 */
public class FileUtils {

    /**
     * Generates a unique filename based on the original filename.
     * 
     * @param originalFilename the original filename
     * @return a unique filename
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String sanitizedBaseName = sanitizeFilename(baseName);
        
        return sanitizedBaseName + "_" + UUID.randomUUID() + (StringUtils.isNotBlank(extension) ? "." + extension : "");
    }
    
    /**
     * Sanitizes a filename by removing special characters.
     * 
     * @param filename the filename to sanitize
     * @return the sanitized filename
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        
        // Replace spaces with underscores and remove special characters
        return filename.trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9_.-]", "");
    }
    
    /**
     * Determines the MediaFile.MediaType based on the content type.
     * 
     * @param contentType the content type
     * @return the MediaType
     */
    public static MediaFile.MediaType determineMediaType(String contentType) {
        if (contentType == null) {
            return MediaFile.MediaType.OTHER;
        }
        
        if (contentType.startsWith("video/")) {
            return MediaFile.MediaType.VIDEO;
        } else if (contentType.startsWith("audio/")) {
            return MediaFile.MediaType.AUDIO;
        } else if (contentType.startsWith("image/")) {
            return MediaFile.MediaType.IMAGE;
        } else if (contentType.equals(MediaType.APPLICATION_PDF_VALUE) || 
                contentType.startsWith("text/") || 
                contentType.equals("application/msword") || 
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return MediaFile.MediaType.DOCUMENT;
        } else {
            return MediaFile.MediaType.OTHER;
        }
    }
    
    /**
     * Creates a MediaFile object from a MultipartFile.
     * 
     * @param file the MultipartFile
     * @param bucketName the S3 bucket name
     * @param objectKey the S3 object key
     * @param filename name of the file to store in database
     * @return the MediaFile
     * @throws IOException if an I/O error occurs
     */
    public static MediaFile createMediaFileFromMultipartFile(
            MultipartFile file, String bucketName, String objectKey, String filename) throws IOException {
        
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = Files.probeContentType(Path.of(
                Optional
                    .ofNullable(file.getOriginalFilename())
                    .orElse(RandomStringUtils.random(20, true, true))
            ));
        }
        
        return MediaFile.builder()
                .id(UUID.randomUUID())
                .filename(filename)
                .originalFilename(file.getOriginalFilename())
                .contentType(contentType)
                .size(file.getSize())
                .bucketName(bucketName)
                .objectKey(objectKey)
                .status(MediaFile.MediaStatus.PENDING)
                .type(determineMediaType(contentType))
                .available(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Checks if a file is a video file based on its content type.
     * 
     * @param contentType the content type
     * @return true if the file is a video file, false otherwise
     */
    public static boolean isVideoFile(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }
    
    /**
     * Checks if a file is an audio file based on its content type.
     * 
     * @param contentType the content type
     * @return true if the file is an audio file, false otherwise
     */
    public static boolean isAudioFile(String contentType) {
        return contentType != null && contentType.startsWith("audio/");
    }
    
    /**
     * Checks if a file is an image file based on its content type.
     * 
     * @param contentType the content type
     * @return true if the file is an image file, false otherwise
     */
    public static boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}