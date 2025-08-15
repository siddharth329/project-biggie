package com.club69.mediaconvert.pipeline.steps;

import com.club69.mediaconvert.mediaconvert.FFmpegCommandGeneratorService;
import com.club69.commons.dto.MediaConversionRequest;
import com.club69.commons.service.S3Service;
import com.club69.mediaconvert.config.FFmpegProcessingConfig;
import com.club69.mediaconvert.exception.ProcessExecutorException;
import com.club69.mediaconvert.function.ProcessExecutor;
import com.club69.mediaconvert.function.ProcessExecutorResponse;
import com.club69.mediaconvert.mediaconvert.ValidationResult;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import com.club69.mediaconvert.validation.MediaConversionValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FFmpegProcessStep implements PipelineStep {
    private final S3Service s3Service;
    private final ProcessExecutor processExecutor;
    private final PipelineStepName stepName = PipelineStepName.PROCESS_FFMPEG;
    private final FFmpegCommandGeneratorService commandGeneratorService;
    private final FFmpegProcessingConfig ffmpegProcessingConfig;
    private final MediaConversionValidationService mediaConversionValidationService;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) throws ProcessExecutorException {
        return processFFmpegJob(workingMemory);
    }

    private PipelineWorkingMemory processFFmpegJob(PipelineWorkingMemory workingMemory) throws ProcessExecutorException {
        ConversionQueue job = workingMemory.getJob();
        URL input = s3Service.generatePresignedUrl(job.getInputBucketName(), job.getObjectKey(), Duration.ofDays(1));

        // Validating the media conversion request
        ValidationResult result = mediaConversionValidationService.validateRequest(
                job.getMediaConversionRequest(),
                ffmpegProcessingConfig.getAvailableHardwareAcceleration());
        if (!result.isValid()) {
            log.error("Media Conversion Request Validation Failed");
            throw new RuntimeException(
                    "Media Conversion Request Validation Failed: " +
                    "Warning: " + result.getWarnings() + " " +
                    "Error: " + result.getErrors()
            );
        }

        // Generating command for execution
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
        try {
            Files.createDirectories(Path.of(Paths.get(workingMemory.getTemporaryDirectoryPath(), "ffmpeg").toString()));
        } catch (IOException e) {
            workingMemory.markAsFailed("Error in " + stepName + ": " + e.getMessage());
            return workingMemory;
        }

        for (int i = 0; i < commands.size(); i++) {
            List<String> command = commands.get(i);
            ProcessExecutorResponse processOutput =  processExecutor.run(command, workingMemory.getTemporaryDirectoryPath());
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
