package com.club69.commons.mediaconvert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaInformationRequest {
    @NotBlank(message = "Media file ID is required") private UUID mediaFileId;
    @NotBlank(message = "Bucket Name is required") private String inputBucketName;
    @NotBlank(message = "Object Key is required") private String objectKey;
    @NotNull(message = "Information Request is required") private InformationRequest informationRequest;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InformationRequest {
        private boolean showFormats;
        private boolean showStreams;
    }
}
