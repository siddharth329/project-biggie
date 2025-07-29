package com.club69.commons.mediaconvert.options;

import lombok.Getter;

@Getter
public enum VideoTune {
    // H.264/H.265 tunes
    FILM("film", "High quality movie content"),
    ANIMATION("animation", "Cartoons and animation"),
    GRAIN("grain", "Grainy content"),
    STILLIMAGE("stillimage", "Slideshows"),
    PSNR("psnr", "PSNR optimization"),
    SSIM("ssim", "SSIM optimization"),
    FASTDECODE("fastdecode", "Fast decoding"),
    ZEROLATENCY("zerolatency", "Live streaming"),

    // Hardware-specific tunes
    HQ("hq", "High quality (NVENC)"),
    LL("ll", "Low latency (NVENC)"),
    ULL("ull", "Ultra low latency (NVENC)"),
    LOSSLESS_HW("lossless", "Lossless (NVENC)"),

    // QSV tunes
    QUALITY("quality", "Quality mode (QSV)"),
    BALANCED("balanced", "Balanced mode (QSV/AMF)"),
    SPEED("speed", "Speed mode (QSV/AMF)"),
    ULTRALOWLATENCY("ultralowlatency", "Ultra low latency (AMF)");

    private final String value;
    private final String description;

    VideoTune(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean isCompatibleWith(VideoCodec codec, HardwareAcceleration acceleration) {
        switch (acceleration) {
            case NVENC:
                return this == HQ || this == LL || this == ULL || this == LOSSLESS_HW;
            case QSV:
                return this == QUALITY || this == BALANCED || this == SPEED;
            case AMF:
                return this == QUALITY || this == BALANCED || this == SPEED || this == ULTRALOWLATENCY;
            case SOFTWARE:
                return this == FILM || this == ANIMATION || this == GRAIN || this == STILLIMAGE ||
                        this == PSNR || this == SSIM || this == FASTDECODE || this == ZEROLATENCY;
            default:
                return false;
        }
    }
}
