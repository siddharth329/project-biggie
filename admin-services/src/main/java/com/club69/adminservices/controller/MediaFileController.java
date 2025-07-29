package com.club69.adminservices.controller;

import com.club69.commons.response.ApiResponse;
import com.club69.adminservices.service.MediaFileService;
import com.club69.commons.model.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${api.prefix}/mediaFiles")
public class MediaFileController {
    private final MediaFileService mediaFileService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getMediaFiles(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<MediaFile> filePage = mediaFileService.getMediaFiles(pageable);
        return ResponseEntity.ok(new ApiResponse("Success", filePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMediaFileById(@PathVariable UUID id) {
        MediaFile file = mediaFileService.getMediaFileById(id);
        return ResponseEntity.ok(new ApiResponse("Success", file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        mediaFileService.deleteMediaFile(id);
        return ResponseEntity.ok(new ApiResponse("Success", null));
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile uploadedFile) {
        MediaFile file = mediaFileService.uploadMediaFile(filename, uploadedFile);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Uploaded Successfully", file));
    }

    @PostMapping("/changeAvailability/{id}")
    public ResponseEntity<ApiResponse> setAvailability(
            @PathVariable UUID id,
            @RequestParam("isAvailable") Boolean isAvailable) {
        MediaFile file = mediaFileService.changeMediaFileAvailable(id, isAvailable);
        return ResponseEntity.ok(new ApiResponse("Success", file));
    }
}
