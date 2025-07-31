package com.club69.mediaconvert.mediaconvert.options;

import lombok.Getter;

@Getter
public enum HardwareAcceleration {
    SOFTWARE("Software encoding"),
    NVENC("NVIDIA NVENC"),
    QSV("Intel Quick Sync Video"),
    AMF("AMD AMF"),
    VIDEOTOOLBOX("Apple VideoToolbox");

    private final String description;

    HardwareAcceleration(String description) {
        this.description = description;
    }

}
