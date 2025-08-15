package com.club69.mediaconvert.model;

import com.club69.commons.dto.MediaConversionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a response for a media conversion operation.
 * This model contains information about the result of a conversion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ConversionQueue {
    @Id @GeneratedValue private UUID id;

    // Request Input Details
    @Column(name = "mediaFileId") private UUID mediaFileId;
    @Column(name = "inputBucketName") private String inputBucketName;
    @Column(name = "objectKey") private String objectKey;
    @Column(name = "outputBucketName") private String outputBucketName;
    @Column(name = "outPrefix") private String outPrefix;

    @Enumerated(EnumType.STRING)
    @Column(name = "status") private ConversionStatus status;

    // Request Execution Details
    @Column(name = "startTime") private LocalDateTime startTime;
    @Column(name = "endTime") private LocalDateTime endTime;
    @Column(name = "lockedBy") private String lockedBy;
    @Column(name = "lockedAt") private LocalDateTime lockedAt;
    @Column(name = "maxRetries") private Integer maxRetries;
    @Column(name = "retryCount") private Integer retryCount;
    @Column(name = "errorMessage") private String errorMessage;

    // Request Processing Details
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conversionRequest", columnDefinition = "jsonb")
    private MediaConversionRequest mediaConversionRequest;
    private String metadata;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    public enum ConversionStatus {
        PENDING("PENDING"),
        IN_PROGRESS("IN_PROGRESS"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED"),
        RETRYING("RETRYING"),
        CANCELLED("CANCELLED");

        private String value;
        ConversionStatus(String value) {
            this.value = value;
        }
    }
}