package com.club69.mediaconvert.service;

import com.club69.commons.dto.MediaInformationRequest;
import com.club69.commons.mediaconvert.serialize.FFmpegProbeResult;


public interface MediaInformationService {
    /**
     * Converts a media file based on the specified conversion request.
     *
     * @param request the conversion request
     * @return the conversion response
     */
    FFmpegProbeResult getMediaInformation(MediaInformationRequest request);
}
