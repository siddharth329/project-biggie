package com.club69.adminservices.service;

import com.club69.adminservices.dto.ConversionQueueRequestDto;
import com.club69.commons.mediaconvert.serialize.FFmpegProbeResult;
import com.club69.commons.model.MediaFile;

import java.util.UUID;

public interface MediaConvertService {
    ConversionQueueRequestDto convertMediaFile(UUID mediaFileId);
    FFmpegProbeResult mediaFileInformation(UUID mediaFileId, boolean showFormats, boolean showStreams);
    FFmpegProbeResult mediaFileInformation(MediaFile mediaFile, boolean showFormats, boolean showStreams);
}
