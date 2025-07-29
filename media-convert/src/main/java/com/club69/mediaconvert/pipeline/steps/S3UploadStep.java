package com.club69.mediaconvert.pipeline.steps;

import com.club69.commons.service.S3Service;
import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class S3UploadStep implements PipelineStep {
    private final S3Service s3Service;
    private final PipelineStepName stepName = PipelineStepName.UPLOAD_FILES_S3;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) {
        return null;
    }

}
