package com.club69.commons.util;

import com.club69.commons.mediaconvert.serialize.FFmpegProbeResult;
import com.club69.commons.mediaconvert.serialize.FFmpegStream;
import org.springframework.stereotype.Component;

@Component
public class MediaUtils {
    public Integer getMediaHeight(FFmpegProbeResult probeResult) throws Exception {
        FFmpegStream fFmpegStream = probeResult.getStreams()
                .stream()
                .filter(stream -> stream.getCodecType().equalsIgnoreCase("video"))
                .findFirst()
                .orElse(null);

        if (fFmpegStream != null) {
            return fFmpegStream.getHeight();
        } else {
            throw new Exception("Given media is not a video file");
        }
    }
}
