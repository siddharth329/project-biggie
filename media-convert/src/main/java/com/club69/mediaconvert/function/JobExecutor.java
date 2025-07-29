package com.club69.mediaconvert.function;

import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.pipeline.MediaConversionPipeline;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import com.club69.mediaconvert.service.JobQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JobExecutor {
    private final JobQueueService jobQueueService;
    private final MediaConversionPipeline mediaConversionPipeline;

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    public void processJobs() {
        log.debug("Processing jobs Scheduler called for job");

        Optional<ConversionQueue> jobOpt = jobQueueService.claimNextJob();

        if (jobOpt.isPresent()) {
            ConversionQueue job = jobOpt.get();
            UUID jobId = job.getId();
            log.info("Processing job: {}", jobId);

            PipelineWorkingMemory workingMemory = null;
            try {
                // Calling pipeline instead of above
                workingMemory = mediaConversionPipeline.execute(PipelineWorkingMemory.builder().job(job).build());
                log.info("Job {} completed gracefully", jobId);
                if (workingMemory.getStatus()) jobQueueService.completeJob(jobId);

            } catch (Exception e) {
                log.error("Job {} failed: {}", jobId, e.getMessage());
                jobQueueService.failJob(jobId, e.getMessage());
            }
        }
    }
}
