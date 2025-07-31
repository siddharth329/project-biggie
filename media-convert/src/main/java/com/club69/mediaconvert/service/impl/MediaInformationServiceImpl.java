package com.club69.mediaconvert.service.impl;

import com.club69.mediaconvert.dto.MediaInformationRequest;
import com.club69.commons.exception.ApiException;
import com.club69.commons.service.S3Service;
import com.club69.mediaconvert.core.ffprobe.FFProbeBuilder;
import com.club69.mediaconvert.core.ffprobe.parser.FFProbeOutputParser;
import com.club69.mediaconvert.mediaconvert.ffprobe.serialize.FFmpegProbeResult;
import com.club69.mediaconvert.function.ProcessExecutor;
import com.club69.mediaconvert.function.ProcessExecutorResponse;
import com.club69.mediaconvert.service.MediaInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaInformationServiceImpl implements MediaInformationService {
    private final S3Service s3Service;
    private final FFProbeOutputParser ffProbeOutputParser;
    private final ProcessExecutor processExecutor;

    @Override
    public FFmpegProbeResult getMediaInformation(MediaInformationRequest request) {
        URL input = s3Service.generatePresignedUrl(request.getInputBucketName(), request.getObjectKey(), Duration.ofDays(1));
        List<String> commands = FFProbeBuilder.builder()
                .ffProbePath("C:\\\\Users\\\\MSUSERSL123\\\\Desktop\\\\project\\\\ffmpeg-master-latest-win64-gpl-shared\\\\bin\\\\ffprobe.exe")
                .input(input.toString())
                .showFormats(request.getInformationRequest().isShowFormats())
                .showStreams(request.getInformationRequest().isShowStreams())
                .build().buildCommand();

        ProcessExecutorResponse processOutput = processExecutor.run(commands);
        if (processOutput.getExitCode().equals(0)) {
            FFmpegProbeResult probeResult = ffProbeOutputParser.parseOutput(processOutput.getOutput());
            probeResult.format.setFilename(request.getObjectKey()); // Replacing the Presigned URL with Filename
            return probeResult;
        } else {
            throw new ApiException("Something went wrong while getting media information: " + processOutput.getErrorMessage());
        }
    }
}
