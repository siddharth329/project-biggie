package com.club69.mediaconvert.pipeline.steps;

import com.club69.mediaconvert.dto.ShakaPackagerRequest;
import com.club69.mediaconvert.exception.ProcessExecutorException;
import com.club69.mediaconvert.function.ProcessExecutor;
import com.club69.mediaconvert.function.ProcessExecutorResponse;
import com.club69.mediaconvert.mediaconvert.ShakaCommandGeneratorService;
import com.club69.mediaconvert.mediaconvert.ValidationResult;
import com.club69.mediaconvert.mediaconvert.shaka.StreamingProtocol;
import com.club69.mediaconvert.model.ConversionQueue;
import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import com.club69.mediaconvert.validation.ShakaPackagerValidationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Component
@Getter
@Slf4j
@RequiredArgsConstructor
public class ShakaPackagerStep implements PipelineStep {
    private final PipelineStepName stepName = PipelineStepName.PROCESS_SHAKA_PACKAGER;
    private final ProcessExecutor processExecutor;
    private final ShakaCommandGeneratorService commandGeneratorService;
    private final ShakaPackagerValidationService shakaPackagerValidationService;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) {
        return processShakaPackager(workingMemory);
    }

    public PipelineWorkingMemory processShakaPackager(PipelineWorkingMemory workingMemory) {
        ConversionQueue job = workingMemory.getJob();
        Path shakaOutputPath = Path.of("shaka");
        ShakaPackagerRequest request = commandGeneratorService.createFromMediaConversion(
                job.getMediaConversionRequest(),
                StreamingProtocol.BOTH,
                workingMemory.getTemporaryDirectoryPath(),
                shakaOutputPath.toString());

        // Validating the media conversion request
        ValidationResult result = shakaPackagerValidationService.validateRequest(request);
        if (!result.isValid()) {
            log.error("Media Conversion Request Validation Failed");
            throw new RuntimeException(
                    "Media Conversion Request Validation Failed: " +
                            "Warning: " + result.getWarnings() + " " +
                            "Error: " + result.getErrors()
            );
        }

        try {
            Files.createDirectories(Path.of(Paths.get(workingMemory.getTemporaryDirectoryPath(), "shaka").toString()));
        } catch (IOException e) {
            workingMemory.markAsFailed("Error in " + stepName + ": " + e.getMessage());
            return workingMemory;
        }

        List<String> command = commandGeneratorService.generateCommand(request);
        ProcessExecutorResponse processOutput =  processExecutor.run(command, workingMemory.getTemporaryDirectoryPath());
        if (!processOutput.getExitCode().equals(0)) {
            log.error("ProcessExecutorResponse: {}", processOutput);
            log.error("ShakaCommandWithError: {}", String.join(" ", command));
            throw new ProcessExecutorException(
                    "Error while Processing Job: " + workingMemory.getJob().getId() + " " +
                            "Step Name: " + stepName + " " +
                            "Error message: " + processOutput.getErrorMessage());
        }

        return workingMemory;
    }
}
