package com.club69.mediaconvert.mediaconvert.shaka;

import lombok.Getter;

// Enums for Shaka Packager Configuration
@Getter
public enum StreamingProtocol {
    HLS("hls", ".m3u8"),
    DASH("dash", ".mpd"),
    BOTH("both", ".m3u8,.mpd");

    private final String value;
    private final String extension;

    StreamingProtocol(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

}
