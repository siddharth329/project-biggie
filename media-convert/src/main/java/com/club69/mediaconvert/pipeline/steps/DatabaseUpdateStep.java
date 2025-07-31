package com.club69.mediaconvert.pipeline.steps;

import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DatabaseUpdateStep implements PipelineStep {
    private final PipelineStepName stepName = PipelineStepName.UPDATE_JOB_STATUS;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) {
        return workingMemory;
    }

}
