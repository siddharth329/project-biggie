package com.club69.mediaconvert.service;

import com.club69.mediaconvert.dto.MediaConversionRequest;
import com.club69.mediaconvert.model.ConversionQueue;

import java.util.Optional;
import java.util.UUID;

public interface JobQueueService {
    void addJob(MediaConversionRequest request);
    Optional<ConversionQueue> claimNextJob();
    boolean completeJob(UUID jobId);
    boolean failJob(UUID jobId, String errorMessage);
}
