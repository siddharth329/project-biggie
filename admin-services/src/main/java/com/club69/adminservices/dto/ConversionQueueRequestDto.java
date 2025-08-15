package com.club69.adminservices.dto;

import com.club69.commons.dto.MediaConversionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a response for a media conversion operation.
 * This model contains information about the result of a conversion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionQueueRequestDto {
    private UUID id;

    // Request Input Details
    private UUID mediaFileId;
    private String inputBucketName;
    private String objectKey;
    private String outputBucketName;
    private String outPrefix;

    private ConversionStatus status;

    // Request Execution Details
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String lockedBy;
    private LocalDateTime lockedAt;
    private Integer maxRetries;
    private Integer retryCount;
    private String errorMessage;

    // Request Processing Details
    private MediaConversionRequest mediaConversionRequest;
    private String metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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