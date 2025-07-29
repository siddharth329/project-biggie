package com.club69.adminservices.service;

import com.club69.commons.model.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface MediaFileService {
    MediaFile getMediaFileById(UUID id);
    Page<MediaFile> getMediaFiles(Pageable pageable);
    MediaFile uploadMediaFile(String filename, MultipartFile file);
    MediaFile changeMediaFileAvailable(UUID id, Boolean available);
    void deleteMediaFile(UUID id);
}
