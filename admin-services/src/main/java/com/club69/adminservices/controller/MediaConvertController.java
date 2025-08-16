package com.club69.adminservices.controller;

import com.club69.adminservices.dto.ConversionQueueRequestDto;
import com.club69.adminservices.service.MediaConvertService;
import com.club69.commons.mediaconvert.serialize.FFmpegProbeResult;
import com.club69.commons.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mediaConvert")
public class MediaConvertController {
    private final MediaConvertService mediaConvertService;

    @PostMapping("/convert/{mediaFileId}")
    public ResponseEntity<ApiResponse<ConversionQueueRequestDto>> convert(@PathVariable UUID mediaFileId) {
        ConversionQueueRequestDto requestDto = mediaConvertService.convertMediaFile(mediaFileId);
        return ResponseEntity.ok(new ApiResponse<>("Success", requestDto));
    }

    @PostMapping("/information/{mediaFileId}")
    public ResponseEntity<ApiResponse<FFmpegProbeResult>> information(
            @PathVariable UUID mediaFileId,
            @RequestParam(value = "showStreams", required = false, defaultValue = "true") boolean showStreams,
            @RequestParam(value = "showFormats", required = false, defaultValue = "false") boolean showFormats) {
        FFmpegProbeResult probeResult = mediaConvertService.mediaFileInformation(mediaFileId, showFormats, showStreams);
        return ResponseEntity.ok(new ApiResponse<>("Success", probeResult));
    }
}
