package com.club69.adminservices.helpers;

import com.club69.commons.mediaconvert.options.VideoConstantRateFactor;
import com.club69.commons.mediaconvert.options.VideoProfileOption;
import lombok.Builder;

import java.util.List;

public class VideoProfilePresets {
    public static List<Preset> presets = List.of(
            Preset.builder()
                    .profileOption(1080)
                    .CRF(VideoConstantRateFactor.HIGH_QUALITY)
                    .PROFILE(VideoProfileOption.HIGH)
                    .LEVEL("4.0")
                    .KEY_INT_MIN(30)
                    .GROUP_OF_PICTURES_SIZE(60)
                    .SC_THRESHOLD(40).build(),
            Preset.builder()
                    .profileOption(720)
                    .CRF(VideoConstantRateFactor.MEDIUM_QUALITY)
                    .PROFILE(VideoProfileOption.HIGH)
                    .LEVEL("3.1")
                    .KEY_INT_MIN(25)
                    .GROUP_OF_PICTURES_SIZE(50)
                    .SC_THRESHOLD(40).build(),
            Preset.builder()
                    .profileOption(480)
                    .CRF(VideoConstantRateFactor.GOOD_QUALITY)
                    .PROFILE(VideoProfileOption.HIGH)
                    .LEVEL("3.1")
                    .KEY_INT_MIN(25)
                    .GROUP_OF_PICTURES_SIZE(50)
                    .SC_THRESHOLD(40).build()
    );

    @Builder
    public static class Preset {
        public Integer profileOption;
        public VideoConstantRateFactor CRF;
        public VideoProfileOption PROFILE;
        public String LEVEL;
        public Integer KEY_INT_MIN;
        public Integer GROUP_OF_PICTURES_SIZE;
        public Integer SC_THRESHOLD;
    }
}
