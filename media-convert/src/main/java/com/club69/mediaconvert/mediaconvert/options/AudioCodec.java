package com.club69.mediaconvert.mediaconvert.options;

import lombok.Getter;

@Getter
public enum AudioCodec {
    AAC("aac", "Advanced Audio Coding"),
    MP3("libmp3lame", "MPEG Audio Layer III"),
    OPUS("libopus", "Opus"),
    VORBIS("libvorbis", "Ogg Vorbis"),
    FLAC("flac", "Free Lossless Audio Codec"),
    AC3("ac3", "Dolby Digital"),
    EAC3("eac3", "Dolby Digital Plus"),
    DTS("dts", "DTS"),
    PCM_S16LE("pcm_s16le", "PCM 16-bit"),
    PCM_S24LE("pcm_s24le", "PCM 24-bit");

    private final String value;
    private final String description;

    AudioCodec(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
