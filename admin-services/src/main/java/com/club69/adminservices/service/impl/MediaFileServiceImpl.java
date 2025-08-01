package com.club69.adminservices.service.impl;

import com.club69.commons.config.S3Configuration;
import com.club69.commons.exception.ApiException;
import com.club69.adminservices.service.MediaFileService;
import com.club69.commons.model.MediaFile;
import com.club69.adminservices.repository.MediaFileRepository;
import com.club69.commons.service.S3Service;
import com.club69.commons.util.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {
    private final MediaFileRepository mediaFileRepository;
    private final S3Service s3Service;
    private final S3Configuration s3Configuration;

    @Override
    public MediaFile getMediaFileById(UUID id) {
        return mediaFileRepository
                .findById(id)
                .orElseThrow(() -> new ApiException("Media File not found"));
    }

    @Override
    public Page<MediaFile> getMediaFiles(Pageable pageable) {
        return mediaFileRepository.findAll(pageable);
    }

    @Override
    public MediaFile uploadMediaFile(String filename, MultipartFile file) {
        String objectKey = FileUtils.generateUniqueFilename(file.getOriginalFilename());
        String fileName = Optional
                .ofNullable(filename)
                .orElseThrow(() -> new ApiException("Invalid File Name"))
                .trim().replace(" ", "_");

        try {
            MediaFile mediaFile = s3Service.uploadFile(file, s3Configuration.getOriginalMediaFileBucket(), fileName, objectKey);
            return mediaFileRepository.save(mediaFile);
        } catch (IOException e) {
            throw new ApiException("Upload File Failed");
        }
    }

    @Override
    public MediaFile changeMediaFileAvailable(UUID id, Boolean available) {
        MediaFile mediaFile = this.getMediaFileById(id);
        mediaFile.setAvailable(available);
        return mediaFileRepository.save(mediaFile);
    }

    @Override
    @Transactional
    public void deleteMediaFile(UUID id) {
        MediaFile mediaFile = this.getMediaFileById(id);

        s3Service.deleteObject(mediaFile.getBucketName(), mediaFile.getObjectKey());
        s3Service.deleteObjectByPrefix(mediaFile.getBucketName(), mediaFile.getObjectKey());
        mediaFileRepository.deleteById(id);
    }
}
