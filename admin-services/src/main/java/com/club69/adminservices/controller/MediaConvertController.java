package com.club69.adminservices.controller;

import com.club69.adminservices.dto.ConversionQueueRequestDto;
import com.club69.adminservices.service.MediaConvertService;
import com.club69.commons.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mediaConvert")
public class MediaConvertController {
    private final MediaConvertService mediaConvertService;

    @PostMapping("/convert/{mediaFileId}")
    public ResponseEntity<ApiResponse> convert(@PathVariable UUID mediaFileId) {
        ConversionQueueRequestDto requestDto = mediaConvertService.convertMediaFile(mediaFileId);
        return ResponseEntity.ok(new ApiResponse("Success", requestDto));
    }
}
