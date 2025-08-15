package com.club69.mediaconvert.controller;

import com.club69.commons.dto.MediaInformationRequest;
import com.club69.commons.response.ApiResponse;
import com.club69.mediaconvert.mediaconvert.ffprobe.serialize.FFmpegProbeResult;
import com.club69.mediaconvert.service.MediaInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mediaInformation")
public class MediaInformationController {
    private final MediaInformationService mediaInformationService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse> getMediaInformation(@RequestBody MediaInformationRequest mediaInformationRequest){
        FFmpegProbeResult information = mediaInformationService.getMediaInformation(mediaInformationRequest);
        return ResponseEntity.ok(new ApiResponse("Success", information));
    }
}
