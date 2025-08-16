package com.club69.mediaconvert.dto;

import com.club69.commons.mediaconvert.shaka.EncryptionMethod;
import com.club69.commons.mediaconvert.shaka.KeyRotationPeriod;
import com.club69.commons.mediaconvert.shaka.SegmentDuration;
import com.club69.commons.mediaconvert.shaka.StreamingProtocol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

// Shaka Packager Request Model
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShakaPackagerRequest {
    @NotBlank(message = "Output directory is required")
    private String outputDirectory;

    @NotNull(message = "Streaming protocol is required")
    private StreamingProtocol protocol;

    @NotBlank(message = "Master playlist name is required")
    private String masterPlaylistName;

    @NotNull(message = "At least one input stream is required")
    @Size(min = 1, max = 20, message = "Between 1 and 20 input streams allowed")
    private List<InputStream> inputStreams;

    // Segmentation settings
    private SegmentDuration segmentDuration = SegmentDuration.STANDARD;
    private Integer fragmentDuration; // In seconds, for DASH

    // Encryption settings
    private EncryptionMethod encryptionMethod = EncryptionMethod.NONE;
    private String keyServerUrl;
    private String contentId;
    private KeyRotationPeriod keyRotationPeriod = KeyRotationPeriod.DISABLED;

    // Advanced options
    private Boolean generateStaticLiveProfile = false;
    private Boolean preserveOrder = true;
    private Boolean allowCodecSwitching = false;
    private String baseUrls; // Comma-separated URLs
    private Integer minBufferTime; // In seconds
    private Integer timeShiftBufferDepth; // In seconds for live streaming

    // HLS specific options
    private String hlsMasterPlaylistOutput;
    private String hlsMediaPlaylistPrefix;
    private Boolean hlsIframePlaylistsOnly = false;

    // DASH specific options
    private String dashMpdOutput;
    private Boolean dashGenerateStaticLiveProfile = false;
    private String dashUtcTimings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InputStream {
        @NotBlank(message = "Input file path is required")
        private String inputFile;

        @NotNull(message = "Stream type is required")
        private StreamType streamType;

        private String streamSelector; // e.g., "0" for first stream, "video", "audio"
        private String outputFormat; // e.g., "mp4", "webm"
        private String initSegment; // Custom init segment name
        private String segmentTemplate; // Custom segment template
        private String playlistName; // Custom playlist name for this stream

        // Quality/bitrate info for adaptive streaming
        private Integer bandwidth; // In bps
        private Integer width;
        private Integer height;
        private String frameRate;
        private String codecs;
        private String language; // For audio streams
        private String role; // e.g., "main", "alternate", "subtitle"

        @Getter
        public enum StreamType {
            VIDEO("video"),
            AUDIO("audio"),
            TEXT("text"),
            TRICK_PLAY("trick_play");

            private final String value;
            StreamType(String value) {
                this.value = value;
            }
        }
    }
}
