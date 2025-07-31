package com.club69.mediaconvert.controller;

import com.club69.mediaconvert.dto.MediaConversionRequest;
import com.club69.mediaconvert.mediaconvert.ValidationResult;
import com.club69.commons.response.ApiResponse;
import com.club69.mediaconvert.config.FFmpegProcessingConfig;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.service.MediaConversionService;
import com.club69.mediaconvert.validation.MediaConversionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mediaConversion")
public class MediaConversionController {
    private final MediaConversionService mediaConversionService;
    private final MediaConversionValidationService validationService;
    private final FFmpegProcessingConfig ffmpegProcessingConfig;

    @PostMapping("/convert")
    public ResponseEntity<ApiResponse> convertMediaFile(@RequestBody MediaConversionRequest request){
        ConversionQueue queue = mediaConversionService.convertMediaFile(request);
        return ResponseEntity.ok(new ApiResponse("Success", queue));
    }

    @PostMapping("/generateCommand")
    public ResponseEntity<ApiResponse> generateCommand(@RequestBody MediaConversionRequest request){
        ValidationResult validationResult = validationService.validateRequest(request, ffmpegProcessingConfig.getAvailableHardwareAcceleration());
        if (!validationResult.isValid()) {
            return ResponseEntity.ok(new ApiResponse("Validation Failed", validationResult));
        }
        List<List<String>> commands = mediaConversionService.generateCommand(request);
        return ResponseEntity.ok(new ApiResponse("Success", commands));
    }

    @GetMapping("/getConversionsByMediaFileId/{mediaFileId}")
    public ResponseEntity<ApiResponse> getConversions(@PathVariable UUID mediaFileId){
        List<ConversionQueue> queues = mediaConversionService.getConversionsForMediaFile(mediaFileId);
        return ResponseEntity.ok(new ApiResponse("Success", queues));
    }

    @PostMapping("/cancelConversions/{conversionId}")
    public ResponseEntity<ApiResponse> cancelConversion(@PathVariable UUID conversionId){
        boolean result = mediaConversionService.cancelConversion(conversionId);
        return ResponseEntity.ok(new ApiResponse("Success", result));
    }

    @GetMapping("/getConversion/{conversionId}")
    public ResponseEntity<ApiResponse> getConversion(@PathVariable UUID conversionId){
        ConversionQueue conversionQueue = mediaConversionService.getConversionById(conversionId);
        return ResponseEntity.ok(new ApiResponse("Success", conversionQueue));
    }
}
