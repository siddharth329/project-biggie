package com.club69.mediaconvert.dto;

import com.club69.mediaconvert.model.ConversionQueue;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public class MediaConversionStatus {

    @Data
    public static class Request {
        private UUID conversionId;
    }

    @Data @Builder
    public static class Response {
        private UUID conversionId;
        private UUID mediaFileId;
        private ConversionQueue.ConversionStatus status;
        private Integer retryCount;
        private String errorMessage;
    }
}
