package com.club69.mediaconvert.config;

import com.club69.commons.mediaconvert.options.HardwareAcceleration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FFmpegProcessingConfig {
    public HardwareAcceleration hardwareAcceleration = HardwareAcceleration.SOFTWARE;

    public HardwareAcceleration getAvailableHardwareAcceleration() {
        return hardwareAcceleration;
    }
}
