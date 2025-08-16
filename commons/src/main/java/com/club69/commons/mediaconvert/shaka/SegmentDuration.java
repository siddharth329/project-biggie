package com.club69.commons.mediaconvert.shaka;

import lombok.Getter;

@Getter
public enum SegmentDuration {
    VERY_SHORT(2, "2 seconds - Ultra low latency"),
    SHORT(4, "4 seconds - Low latency"),
    STANDARD(6, "6 seconds - Standard"),
    MEDIUM(8, "8 seconds - Medium"),
    LONG(10, "10 seconds - Long segments");

    private final int seconds;
    private final String description;

    SegmentDuration(int seconds, String description) {
        this.seconds = seconds;
        this.description = description;
    }

}
