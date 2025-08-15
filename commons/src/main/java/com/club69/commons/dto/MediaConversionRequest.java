package com.club69.commons.dto;


import com.club69.commons.mediaconvert.options.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaConversionRequest {
    @NotBlank(message = "Media file ID is required") private UUID mediaFileId;
    @NotBlank(message = "Bucket Name is required") private String inputBucketName;
    @NotBlank(message = "Object Key is required") private String objectKey;
    @NotBlank(message = "Bucket Name is required") private String outputBucketName;
    @NotBlank(message = "Object Key is required") private String outputPrefix;

    @NotBlank(message = "Profile is required for conversion")
    @Size(min = 1, max = 10, message = "Total profiles should be between 1 to 10")
    private List<Profile> profile;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Profile {
        @NotBlank(message = "Filename cannot be blank") public String filename;
        @NotNull(message = "Video Profile cannot be null") public VideoProfile videoProfile;
        @NotBlank(message = "Audio Profile cannot be null") public AudioProfile audioProfile;
        public String movFlags;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoProfile {
        public Boolean scaleOutput;
        public VideoScaleType scaleType;
        public Integer scaleValue;

        public VideoCodec videoCodec;
        public VideoConstantRateFactor crf;
        public VideoTune tune;
        public VideoProfileOption profile;
        public String level;

        public String maxRate;
        public String bufferSize;

        public Integer keyIntMin;
        public Integer groupOfPicturesSize;
        public Integer scThreshold;
        public Double frameRate;

        public VideoPixelFormat pixelFormat;

        public enum VideoScaleType { WIDTH, HEIGHT }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AudioProfile {
        public String name;
        public String audioCodec;
        public Long bitrate;
        public Integer sampleRate;
        public Integer audioChannel;
    }
}

// -s 1280x720 -c:v h264 -crf 24 -tune film -profile:v main -level:v 4.0 \
// -maxrate 2500k -bufsize 5000k -r 25 -keyint_min 25 -g 50 -sc_threshold 0 \
// -c:a aac -ar 44100 -b:a 128k -ac 2 -pix_fmt yuv420p -movflags +faststart \
// 720.mp4 \


// Validation Service

//
//
//public class MediaConversionController {
//    private MediaConversionValidationService validationService;
//    private FFmpegCommandGeneratorService commandGeneratorService;
//
//    @PostMapping("/validate")
//    public ResponseEntity<ValidationResult> validateRequest(
//            @RequestBody MediaConversionRequest request,
//            @RequestParam(defaultValue = "SOFTWARE") HardwareAcceleration acceleration) {
//
//        ValidationResult result = validationService.validateRequest(request, acceleration);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/generate-command")
//    public ResponseEntity<?> generateCommand(
//            @RequestBody MediaConversionRequest request,
//            @RequestParam(defaultValue = "SOFTWARE") HardwareAcceleration acceleration) {
//
//        // Validate first
//        ValidationResult validation = validationService.validateRequest(request, acceleration);
//        if (validation.hasErrors()) {
//            return ResponseEntity.badRequest().body(validation);
//        }
//
//        // Generate commands for each profile
//        Map<String, List<String>> commands = new HashMap<>();
//        for (MediaConversionRequest.Profile profile : request.getProfile()) {
//            List<String> command = commandGeneratorService.generateCommand(request, profile, acceleration);
//            commands.put(profile.getFilename(), command);
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("validation", validation);
//        response.put("commands", commands);
//        response.put("acceleration", acceleration);
//
//        return ResponseEntity.ok(response);
//    }
//}