package com.club69.mediaconvert.mediaconvert.options;

import lombok.Getter;

@Getter
public enum VideoPixelFormat {
    // 8-bit formats
    YUV420P("yuv420p", "4:2:0 8-bit (most common)", 8),
    YUV422P("yuv422p", "4:2:2 8-bit", 8),
    YUV444P("yuv444p", "4:4:4 8-bit", 8),
    RGB24("rgb24", "RGB 8-bit", 8),
    BGR24("bgr24", "BGR 8-bit", 8),
    GRAY("gray", "Grayscale 8-bit", 8),

    // 10-bit formats
    YUV420P10LE("yuv420p10le", "4:2:0 10-bit", 10),
    YUV422P10LE("yuv422p10le", "4:2:2 10-bit", 10),
    YUV444P10LE("yuv444p10le", "4:4:4 10-bit", 10),

    // 12-bit formats
    YUV420P12LE("yuv420p12le", "4:2:0 12-bit", 12),
    YUV422P12LE("yuv422p12le", "4:2:2 12-bit", 12),

    // Hardware-specific formats
    NV12("nv12", "4:2:0 NV12 (hardware)", 8),
    P010LE("p010le", "4:2:0 10-bit (hardware)", 10);

    private final String value;
    private final String description;
    private final int bitDepth;

    VideoPixelFormat(String value, String description, int bitDepth) {
        this.value = value;
        this.description = description;
        this.bitDepth = bitDepth;
    }

    public boolean isCompatibleWith(VideoCodec codec, VideoProfileOption profile, HardwareAcceleration acceleration) {
        // Hardware acceleration compatibility
        if (acceleration != HardwareAcceleration.SOFTWARE) {
            switch (acceleration) {
                case NVENC:
                case QSV:
                case AMF:
                    return this == NV12 || this == P010LE || this == YUV444P;
                case VIDEOTOOLBOX:
                    return this == NV12 || this == P010LE || this == YUV420P;
            }
        }

        // Profile compatibility
        if (profile != null) {
            switch (profile) {
                case HIGH10:
                case MAIN10_HEVC:
                    return bitDepth >= 10;
                case HIGH422:
                case MAIN422_10:
                    return this == YUV422P || this == YUV422P10LE || this == YUV422P12LE;
                case HIGH444:
                case MAIN444_8:
                case MAIN444_10:
                    return this == YUV444P || this == YUV444P10LE;
            }
        }

        return true; // Default compatibility
    }
}
