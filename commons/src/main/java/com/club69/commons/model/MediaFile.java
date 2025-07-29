package com.club69.commons.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a media file in the system.
 * This model is used across multiple services for consistent representation of media files.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MediaFile {
    
    @Id @GeneratedValue private UUID id;
    @Column(name = "filename") private String filename;
    @Column(name = "originalFilename") private String originalFilename;
    @Column(name = "contentType") private String contentType;
    @Column(name = "size") private long size;
    @Column(name = "bucketName") private String bucketName;
    @Column(name = "objectKey") private String objectKey;
    @Column(name = "url") private String url;
    @Column(name = "status") private MediaStatus status;
    @Column(name = "type") private MediaType type;
    @Column(name = "available") private boolean available;
    @Column(name = "createdAt") private LocalDateTime createdAt;
    @Column(name = "updatedAt") private LocalDateTime updatedAt;
    @Column(name = "createdBy") private String createdBy;
    @Column(name = "metadata") private String metadata;
    
    /**
     * Enum representing the status of a media file in the system.
     */
    public enum MediaStatus {
        PENDING,
        UPLOADING,
        UPLOADED,
        PROCESSING,
        PROCESSED,
        FAILED,
        DELETED
    }
    
    /**
     * Enum representing the type of media file.
     */
    public enum MediaType {
        VIDEO,
        AUDIO,
        IMAGE,
        DOCUMENT,
        OTHER
    }
}