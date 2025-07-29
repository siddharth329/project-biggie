package com.club69.mediaconvert.pipeline.steps;

import com.club69.commons.mediaconvert.FFmpegCommandGeneratorService;
import com.club69.commons.mediaconvert.MediaConversionRequest;
import com.club69.commons.service.S3Service;
import com.club69.mediaconvert.config.FFmpegProcessingConfig;
import com.club69.mediaconvert.core.ffmpeg.FFmpegBuilder;
import com.club69.mediaconvert.exception.ProcessExecutorException;
import com.club69.mediaconvert.function.ProcessExecutor;
import com.club69.mediaconvert.function.ProcessExecutorResponse;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FFmpegProcessStep implements PipelineStep {
    private final S3Service s3Service;
    private final ProcessExecutor processExecutor;
    private final PipelineStepName stepName = PipelineStepName.PROCESS_FFMPEG;
    private final FFmpegCommandGeneratorService commandGeneratorService;
    private final FFmpegProcessingConfig ffmpegProcessingConfig;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) throws ProcessExecutorException {
        return processFFmpegJob(workingMemory);
    }

    private PipelineWorkingMemory processFFmpegJob(PipelineWorkingMemory workingMemory) throws ProcessExecutorException {
        ConversionQueue job = workingMemory.getJob();
        URL input = s3Service.generatePresignedUrl(job.getInputBucketName(), job.getObjectKey(), Duration.ofDays(1));

        List<List<String>> commands = new ArrayList<>();
        for (MediaConversionRequest.Profile profile: job.getMediaConversionRequest().getProfile()) {
            List<String> command = commandGeneratorService.generateCommand(
                    job.getMediaConversionRequest(),
                    profile,
                    ffmpegProcessingConfig.getAvailableHardwareAcceleration(),
                    input.toString(),
                    workingMemory.getTemporaryDirectoryPath());
            commands.add(command);
        }
        workingMemory.setCommandsGenerated(commands);

        for (int i = 0; i < commands.size(); i++) {
            List<String> command = commands.get(i);
            ProcessExecutorResponse processOutput =  processExecutor.run(command);
            if (!processOutput.getExitCode().equals(0)) {
                throw new ProcessExecutorException(
                        "Error while Processing Job: " + workingMemory.getJob().getId() + " " +
                        "Step Name: " + stepName + " " +
                        "Processing Profile Number: " + i + " " +
                        "Error message: " + processOutput.getErrorMessage());
            }
        }

        return workingMemory;
    }

    @Override
    public PipelineStepName getStepName() {
        return this.stepName;
    }
}
