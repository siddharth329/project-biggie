package com.club69.mediaconvert.pipeline;

public interface PipelineStep {
    PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory);
    PipelineStepName getStepName();
}
