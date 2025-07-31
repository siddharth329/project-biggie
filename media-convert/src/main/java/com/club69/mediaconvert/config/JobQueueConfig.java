package com.club69.mediaconvert.config;

import com.club69.mediaconvert.function.JobExecutor;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.service.JobQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class JobQueueConfig {
    private final JobExecutor jobExecutor;
    private final JobQueueService jobQueueService;

    @Value("${app.processor.concurrent-jobs:1}")
    private int concurrentJobs;

    private final AtomicInteger activeTasks = new AtomicInteger(0);

    public TaskExecutor jobProcessorExecutor() {
        ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();

        // Custom rejection policy
        executor.setRejectedExecutionHandler((runnable, executor1) -> {
            log.warn("Job execution rejected, queue full. Consider increasing pool size.");
            // Could implement custom logic like storing back to DB
        });

        executor.initialize();
        return executor;
    }

    private ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);           // Minimum threads
        executor.setMaxPoolSize(concurrentJobs);           // Maximum threads
        executor.setQueueCapacity(1);        // Queue size before creating new threads
        executor.setKeepAliveSeconds(60);      // Thread idle time before termination
        executor.setThreadNamePrefix("job-exec-ffmpeg-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    @Scheduled(fixedDelay = 5000)
    public void processJobsAdaptively() {
        log.debug("Processing jobs Scheduler called for job with already active tasks count: {}", activeTasks.get());
        int currentActive = activeTasks.get();
        int maxConcurrent = Runtime.getRuntime().availableProcessors();

        if (currentActive < maxConcurrent) {
            Optional<ConversionQueue> jobOpt = jobQueueService.claimNextJob();

            if (jobOpt.isPresent()) {
                activeTasks.incrementAndGet();
                jobProcessorExecutor().execute(() -> {
                    try {
                        jobExecutor.processJobs(jobOpt.get());
                    } finally {
                        activeTasks.decrementAndGet();
                    }
                });
            }
        }
    }
}

