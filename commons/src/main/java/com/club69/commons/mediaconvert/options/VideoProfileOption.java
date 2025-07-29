package com.club69.commons.mediaconvert.options;

import lombok.Getter;

@Getter
public enum VideoProfileOption {
    // H.264 Profiles
    BASELINE("baseline", "Basic features, mobile compatible"),
    MAIN("main", "Standard features"),
    HIGH("high", "Advanced features"),
    HIGH10("high10", "10-bit support"),
    HIGH422("high422", "4:2:2 chroma sampling"),
    HIGH444("high444", "4:4:4 chroma sampling"),

    // H.265 Profiles
    MAIN_HEVC("main", "8-bit 4:2:0 (HEVC)"),
    MAIN10_HEVC("main10", "10-bit 4:2:0 (HEVC)"),
    MAIN12_HEVC("main12", "12-bit 4:2:0 (HEVC)"),
    MAIN422_10("main422-10", "10-bit 4:2:2 (HEVC)"),
    MAIN444_8("main444-8", "8-bit 4:4:4 (HEVC)"),
    MAIN444_10("main444-10", "10-bit 4:4:4 (HEVC)");

    private final String value;
    private final String description;

    VideoProfileOption(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean isCompatibleWith(VideoCodec codec) {
        switch (codec) {
            case H264:
                return this == BASELINE || this == MAIN || this == HIGH ||
                        this == HIGH10 || this == HIGH422 || this == HIGH444;
            case H265:
                return this == MAIN_HEVC || this == MAIN10_HEVC || this == MAIN12_HEVC ||
                        this == MAIN422_10 || this == MAIN444_8 || this == MAIN444_10;
            default:
                return this == MAIN || this == HIGH; // Default fallback
        }
    }
}
