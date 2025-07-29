package com.club69.mediaconvert.pipeline;

import com.club69.mediaconvert.pipeline.steps.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class PipelineConfiguration {
    @Bean
    public List<PipelineStep> pipelineSteps(
            FFmpegProcessStep ffmpegProcessStep,
            ShakaPackagerStep shakaPackagerStep,
            S3UploadStep s3UploadStep,
            DatabaseUpdateStep databaseUpdateStep) {

        return Arrays.asList(
                ffmpegProcessStep,
                shakaPackagerStep,
                s3UploadStep,
                databaseUpdateStep
        );
    }
}
