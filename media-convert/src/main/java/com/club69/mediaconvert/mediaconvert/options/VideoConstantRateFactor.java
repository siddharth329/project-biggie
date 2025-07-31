package com.club69.mediaconvert.mediaconvert.options;

import lombok.Getter;

@Getter
public enum VideoConstantRateFactor {
    LOSSLESS(0, "Lossless quality"),
    VISUALLY_LOSSLESS(18, "Visually lossless"),
    HIGH_QUALITY(20, "High quality"),
    GOOD_QUALITY(23, "Good quality (default)"),
    MEDIUM_QUALITY(26, "Medium quality"),
    LOW_QUALITY(30, "Low quality"),
    POOR_QUALITY(35, "Poor quality");

    private final int value;
    private final String description;

    VideoConstantRateFactor(int value, String description) {
        this.value = value;
        this.description = description;
    }

}
