package com.club69.adminservices.service.impl;

import com.club69.adminservices.clients.MediaConvertFeignClient;
import com.club69.adminservices.dto.ConversionQueueRequestDto;
import com.club69.adminservices.helpers.VideoProfilePresets;
import com.club69.adminservices.repository.MediaFileRepository;
import com.club69.adminservices.service.MediaConvertService;
import com.club69.adminservices.service.MediaFileService;
import com.club69.commons.dto.MediaConversionRequest;
import com.club69.commons.exception.ApiException;
import com.club69.commons.mediaconvert.options.VideoCodec;
import com.club69.commons.mediaconvert.options.VideoConstantRateFactor;
import com.club69.commons.mediaconvert.options.VideoPixelFormat;
import com.club69.commons.mediaconvert.options.VideoTune;
import com.club69.commons.model.MediaFile;
import com.club69.commons.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaConvertServiceImpl implements MediaConvertService {
    private final MediaFileRepository mediaFileRepository;
    private final MediaConvertFeignClient mediaConvertFeignClient;
    private final MediaFileService mediaFileService;

    public ConversionQueueRequestDto convertMediaFile(UUID mediaFileId) {
        MediaFile mediaFile = mediaFileService.getMediaFileById(mediaFileId);
        List<VideoProfilePresets.Preset> presets = VideoProfilePresets.presets;

        List<MediaConversionRequest.Profile> profiles = presets.stream()
                .filter(preset -> preset.profileOption < 4000) // Replacing 4000 with the original media height
                .map(preset -> MediaConversionRequest.Profile.builder()
                        .filename("output_" + preset.profileOption + "p.mp4")
                        .movFlags("faststart")
                        .videoProfile(MediaConversionRequest.VideoProfile.builder()
                                .scaleOutput(true)
                                .scaleType(MediaConversionRequest.VideoProfile.VideoScaleType.HEIGHT)
                                .scaleValue(preset.profileOption)
                                .videoCodec(VideoCodec.H264)
                                .crf(VideoConstantRateFactor.GOOD_QUALITY)
                                .tune(VideoTune.FILM)
                                .profile(preset.PROFILE)
                                .level(preset.LEVEL)
                                .keyIntMin(preset.KEY_INT_MIN)
                                .groupOfPicturesSize(preset.GROUP_OF_PICTURES_SIZE)
                                .scThreshold(preset.SC_THRESHOLD)
                                .frameRate(30.0)
                                .pixelFormat(VideoPixelFormat.YUV420P).build())
                        .audioProfile(MediaConversionRequest.AudioProfile.builder()
                                .name("Main Audio")
                                .audioCodec("aac")
                                .bitrate(128L)
                                .sampleRate(48000)
                                .audioChannel(2).build()).build()
                ).toList();

        MediaConversionRequest request = MediaConversionRequest.builder()
                .mediaFileId(mediaFile.getId())
                .inputBucketName(mediaFile.getBucketName())
                .objectKey(mediaFile.getObjectKey())
                .outputPrefix(mediaFile.getObjectKey())
                .outputBucketName("original")
                .profile(profiles).build();

        ConversionQueueRequestDto conversionQueue;
        try {
            ApiResponse response = mediaConvertFeignClient.convert(request).getBody();
             conversionQueue = response != null ? (ConversionQueueRequestDto) response.getData() : null;
        } catch (Exception e) {
            log.error("Error while requesting conversion: {}", e.getMessage());
            throw new ApiException(e.getMessage());
        }

        mediaFile.setStatus(MediaFile.MediaStatus.PROCESSING);
        mediaFileRepository.save(mediaFile);
        return conversionQueue;
    }
}
