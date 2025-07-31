package com.club69.mediaconvert.service.impl;

import com.club69.mediaconvert.mediaconvert.FFmpegCommandGeneratorService;
import com.club69.mediaconvert.dto.MediaConversionRequest;
import com.club69.commons.exception.ApiException;
import com.club69.mediaconvert.mediaconvert.options.HardwareAcceleration;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.repository.ConversionQueueRepository;
import com.club69.mediaconvert.service.MediaConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaConversionServiceImpl implements MediaConversionService {
    private final ConversionQueueRepository conversionQueueRepository;
    private final FFmpegCommandGeneratorService commandGeneratorService;

    @Override
    public ConversionQueue convertMediaFile(MediaConversionRequest request) {
        ConversionQueue conversionQueue = ConversionQueue.builder()
                .mediaFileId(request.getMediaFileId())
                .inputBucketName(request.getInputBucketName())
                .objectKey(request.getObjectKey())
                .outputBucketName(request.getOutputBucketName())
                .outPrefix(request.getOutputPrefix())
                .status(ConversionQueue.ConversionStatus.PENDING)
                .retryCount(0).maxRetries(1)
                .mediaConversionRequest(request).build();

        return conversionQueueRepository.save(conversionQueue);
    }

    public List<List<String>> generateCommand(MediaConversionRequest request) {
        List<List<String>> commands = new ArrayList<>();
        for (MediaConversionRequest.Profile profile: request.getProfile()) {
            commands.add(commandGeneratorService.generateCommand(request, profile, HardwareAcceleration.SOFTWARE,
                    request.getObjectKey(), "tempDirectory"));
        }
        return commands;
    }

    @Override
    public List<ConversionQueue> getConversionsForMediaFile(UUID mediaFileId) {
        return conversionQueueRepository.findAllByMediaFileId(mediaFileId);
    }

    @Override
    public boolean cancelConversion(UUID conversionId) {
        ConversionQueue conversionQueue = getConversionById(conversionId);
        if (conversionQueue.getStatus().equals(ConversionQueue.ConversionStatus.PENDING) ||
            conversionQueue.getStatus().equals(ConversionQueue.ConversionStatus.FAILED)) {

            conversionQueue.setStatus(ConversionQueue.ConversionStatus.CANCELLED);
            conversionQueueRepository.save(conversionQueue);
            return true;

        } else if (conversionQueue.getStatus().equals(ConversionQueue.ConversionStatus.CANCELLED)) {
            throw new ApiException("Conversion request is already cancelled");
        }

        throw new ApiException("Conversion request cannot be cancelled as it is " + conversionQueue.getStatus().toString().toLowerCase());
    }

    @Override
    public ConversionQueue getConversionById(UUID conversionId) {
        return conversionQueueRepository
                .findById(conversionId)
                .orElseThrow(() -> new ApiException("Conversion not found with the given Id"));
    }
}
