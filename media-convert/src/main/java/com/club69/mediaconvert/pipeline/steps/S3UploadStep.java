package com.club69.mediaconvert.pipeline.steps;

import com.club69.commons.config.S3Configuration;
import com.club69.commons.service.S3Service;
import com.club69.mediaconvert.pipeline.PipelineStep;
import com.club69.mediaconvert.pipeline.PipelineStepName;
import com.club69.mediaconvert.pipeline.PipelineWorkingMemory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class S3UploadStep implements PipelineStep {
    private final S3Service s3Service;
    private final S3Configuration s3Configuration;
    private final PipelineStepName stepName = PipelineStepName.UPLOAD_FILES_S3;

    @Override
    public PipelineWorkingMemory execute(PipelineWorkingMemory workingMemory) {
        return uploadToS3(workingMemory);
    }

    private PipelineWorkingMemory uploadToS3(PipelineWorkingMemory workingMemory) {
        try {
            List<String> uploadedFiles = s3Service.uploadDirectoryToS3(
                    s3Configuration.getConvertedMediaFileBucket(),
                    Path.of(workingMemory.getTemporaryDirectoryPath(), "shaka").toString(),
                    workingMemory.getJob().getMediaFileId().toString());
            workingMemory.setGeneratedFiles(uploadedFiles);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading files to S3", e);
        }

        return workingMemory;
    }
}
