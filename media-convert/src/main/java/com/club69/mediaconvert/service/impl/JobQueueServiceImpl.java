package com.club69.mediaconvert.service.impl;

import com.club69.commons.dto.MediaConversionRequest;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.repository.ConversionQueueRepository;
import com.club69.mediaconvert.service.JobQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class JobQueueServiceImpl implements JobQueueService {
    private final ConversionQueueRepository conversionQueueRepository;

    @Value("${app.worker.instance-id:#{T(java.util.UUID).randomUUID().toString()}}")
    private String workerId;

    @Value("${app.worker.lock-timeout-minutes:30}")
    private Integer lockTimeoutMinutes;


    @Override
    public void addJob(MediaConversionRequest request) {

    }

    @Override
    public Optional<ConversionQueue> claimNextJob() {
        return conversionQueueRepository.claimNextJob(workerId, lockTimeoutMinutes);
    }

    @Override
    public boolean completeJob(UUID jobId) {
        return conversionQueueRepository.completeJob(jobId, workerId);
    }

    @Override
    public boolean failJob(UUID jobId, String errorMessage) {
        return conversionQueueRepository.failJob(jobId, workerId, errorMessage);
    }
}
