package com.club69.mediaconvert.pipeline;

import com.club69.mediaconvert.utils.FileUtils;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaConversionPipeline {
    private final FileUtils fileUtils;
    private final List<PipelineStep> pipelineSteps;
    /*
    STEPS:
        - Processing Media with FFmpeg
        - Processing Media with Shaka Packager
        - Uploading the Media Files to s3
        - Updating the status of the jobs in the database
     */


    // Executing all the steps in the pipelines with the necessary dependencies
    private PipelineWorkingMemory executeWithDependency(PipelineWorkingMemory workingMemory) {

        for (PipelineStep step : pipelineSteps) {
            try {
                log.info("Executing step: {}", step.getStepName());
                workingMemory.setCurrentStep(step.getStepName());
                workingMemory.updateStartTimeForStep();

                workingMemory = step.execute(workingMemory);

                workingMemory.updateEndTimeForStep();
                log.info("Completed step: {}", step.getStepName());

            } catch (Exception e) {
                workingMemory.updateEndTimeForStep();
                workingMemory.markAsFailed("Error in " + step.getStepName() + ": " + e.getMessage());
                log.error("Error in step: {}", step.getStepName(), e);
            }

            if (!workingMemory.getStatus()) {
                log.error("Pipeline stopped at step: {} due to error: {}", workingMemory.getCurrentStep(), workingMemory.getErrorMessage());
                break;
            }
        }

        return workingMemory;
    }

    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) {
        // Creating temporary folder and handling other dependencies
        Preconditions.checkNotNull(workingMemory.getJob(), "Job to be executed cannot be null");

        String temporaryDirectoryPath = null;
        try {
            temporaryDirectoryPath = fileUtils.createTempDirectory(workingMemory.getJob().getId().toString());
            workingMemory.setTemporaryDirectoryPath(temporaryDirectoryPath);

            // Running Created Pipeline
            return this.executeWithDependency(workingMemory);

        } catch (IOException e) {
            log.error("Error creating temporary directory: {}", e.getMessage());
            workingMemory.markAsFailed("Error creating temporary directory: " + e.getMessage());
            return workingMemory;

        } finally {
            if (temporaryDirectoryPath != null) {
                fileUtils.cleanupTempDirectory(temporaryDirectoryPath);
            }
        }
    }
}
