package com.club69.mediaconvert.pipeline.steps;

import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ShakaPackagerStep implements PipelineStep {
    private final PipelineStepName stepName = PipelineStepName.PROCESS_SHAKA_PACKAGER;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) {
        return null;
    }

}
