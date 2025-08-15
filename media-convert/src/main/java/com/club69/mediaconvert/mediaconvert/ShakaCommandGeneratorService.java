package com.club69.mediaconvert.mediaconvert;

import com.club69.commons.dto.MediaConversionRequest;
import com.club69.mediaconvert.dto.ShakaPackagerRequest;
import com.club69.mediaconvert.mediaconvert.shaka.EncryptionMethod;
import com.club69.mediaconvert.mediaconvert.shaka.KeyRotationPeriod;
import com.club69.mediaconvert.mediaconvert.shaka.SegmentDuration;
import com.club69.mediaconvert.mediaconvert.shaka.StreamingProtocol;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Shaka Packager Command Generator Service
@Component
public class ShakaCommandGeneratorService {
    @Value("${processor.shaka-packager.packager.path}")
    private String shakaPackagerPath;

    public List<String> generateCommand(ShakaPackagerRequest request) {
        List<String> command = new ArrayList<>();

        // Base command
        command.add(shakaPackagerPath);

        // Add input streams
        addInputStreams(command, request);

        // Add output options
        addOutputOptions(command, request);

        // Add segmentation options
        addSegmentationOptions(command, request);

        // Add encryption options
        addEncryptionOptions(command, request);

        // Add protocol-specific options
        addProtocolSpecificOptions(command, request);

        // Add advanced options
        addAdvancedOptions(command, request);

        return command;
    }

    private void addInputStreams(List<String> command, ShakaPackagerRequest request) {
        AtomicInteger index = new AtomicInteger(0);
        for (ShakaPackagerRequest.InputStream stream : request.getInputStreams()) {
            StringBuilder streamSpec = new StringBuilder();

            // Input file
            streamSpec.append("in=").append(stream.getInputFile());

            // Stream selector
            if (StringUtils.isNotBlank(stream.getStreamSelector())) {
                streamSpec.append(",stream=").append(stream.getStreamSelector());
            }

            // Output format and path
            String outputPath = buildOutputPath(request, stream, index.get());
            streamSpec.append(",out=").append(outputPath);

            // Init segment
            if (StringUtils.isNotBlank(stream.getInitSegment())) {
                streamSpec.append(",init_segment=").append(stream.getInitSegment());
            }

            // Segment template
            if (StringUtils.isNotBlank(stream.getSegmentTemplate())) {
                streamSpec.append(",segment_template=").append(stream.getSegmentTemplate());
            }

            // Playlist name
            if (StringUtils.isNotBlank(stream.getPlaylistName())) {
                streamSpec.append(",playlist_name=").append(stream.getPlaylistName());
            }

            // Stream metadata
            addStreamMetadata(streamSpec, stream);

            command.add(streamSpec.toString());
            index.incrementAndGet();
        }
    }

    private void addStreamMetadata(StringBuilder streamSpec, ShakaPackagerRequest.InputStream stream) {
        if (stream.getBandwidth() != null) {
            streamSpec.append(",bandwidth=").append(stream.getBandwidth());
        }

//        if (stream.getWidth() != null && stream.getHeight() != null) {
//            streamSpec.append(",resolution=").append(stream.getWidth()).append("x").append(stream.getHeight());
//        }
//
//        if (StringUtils.isNotBlank(stream.getFrameRate())) {
//            streamSpec.append(",frame_rate=").append(stream.getFrameRate());
//        }

        if (StringUtils.isNotBlank(stream.getCodecs())) {
            streamSpec.append(",codecs=").append(stream.getCodecs());
        }

        if (StringUtils.isNotBlank(stream.getLanguage())) {
            streamSpec.append(",language=").append(stream.getLanguage());
        }

        if (StringUtils.isNotBlank(stream.getRole())) {
            streamSpec.append(",role=").append(stream.getRole());
        }
    }

    private String buildOutputPath(ShakaPackagerRequest request, ShakaPackagerRequest.InputStream stream, int index) {
        StringBuilder path = new StringBuilder();
        path.append(request.getOutputDirectory());

        if (!request.getOutputDirectory().endsWith("/")) {
            path.append("/");
        }

        // Generate output filename based on stream type and properties
        switch (stream.getStreamType()) {
            case VIDEO:
                path.append("video");
                path.append("_").append(index);
                if (stream.getHeight() != null) {
                    path.append("_").append(stream.getHeight()).append("p");
                }
                if (stream.getBandwidth() != null) {
                    path.append("_").append(stream.getBandwidth() / 1000).append("k");
                }
                break;
            case AUDIO:
                path.append("audio");
                path.append("_").append(index);
                if (StringUtils.isNotBlank(stream.getLanguage())) {
                    path.append("_").append(stream.getLanguage());
                }
                if (stream.getBandwidth() != null) {
                    path.append("_").append(stream.getBandwidth() / 1000).append("k");
                }
                break;
            case TEXT:
                path.append("text");
                if (StringUtils.isNotBlank(stream.getLanguage())) {
                    path.append("_").append(stream.getLanguage());
                }
                break;
        }

        // Add format extension
        if (StringUtils.isNotBlank(stream.getOutputFormat())) {
            path.append(".").append(stream.getOutputFormat());
        } else {
            path.append(".mp4"); // Default format
        }

        return path.toString();
    }

    private void addOutputOptions(List<String> command, ShakaPackagerRequest request) {
        // Master playlist output
        if (request.getProtocol() == StreamingProtocol.HLS || request.getProtocol() == StreamingProtocol.BOTH) {
            command.add("--hls_master_playlist_output");
            String hlsOutput = StringUtils.isNotBlank(request.getHlsMasterPlaylistOutput()) ?
                    request.getHlsMasterPlaylistOutput() :
                    request.getOutputDirectory() + "/" + request.getMasterPlaylistName() + ".m3u8";
            command.add(hlsOutput);
        }

        if (request.getProtocol() == StreamingProtocol.DASH || request.getProtocol() == StreamingProtocol.BOTH) {
            command.add("--mpd_output");
            String dashOutput = StringUtils.isNotBlank(request.getDashMpdOutput()) ?
                    request.getDashMpdOutput() :
                    request.getOutputDirectory() + "/" + request.getMasterPlaylistName() + ".mpd";
            command.add(dashOutput);
        }
    }

    private void addSegmentationOptions(List<String> command, ShakaPackagerRequest request) {
        // Segment duration
        command.add("--segment_duration");
        command.add(String.valueOf(request.getSegmentDuration().getSeconds()));

        // Fragment duration (primarily for DASH)
        if (request.getFragmentDuration() != null) {
            command.add("--fragment_duration");
            command.add(String.valueOf(request.getFragmentDuration()));
        }
    }

    private void addEncryptionOptions(List<String> command, ShakaPackagerRequest request) {
        if (request.getEncryptionMethod() == EncryptionMethod.NONE) {
            return;
        }

        // Protection scheme
        command.add("--protection_scheme");
        command.add(request.getEncryptionMethod().getValue());

        // Key server URL
        if (StringUtils.isNotBlank(request.getKeyServerUrl())) {
            command.add("--key_server_url");
            command.add(request.getKeyServerUrl());
        }

        // Content ID
        if (StringUtils.isNotBlank(request.getContentId())) {
            command.add("--content_id");
            command.add(request.getContentId());
        }

        // Key rotation
        if (request.getKeyRotationPeriod() != KeyRotationPeriod.DISABLED) {
            command.add("--crypto_period_duration");
            command.add(String.valueOf(request.getKeyRotationPeriod().getSeconds()));
        }
    }

    private void addProtocolSpecificOptions(List<String> command, ShakaPackagerRequest request) {
        // HLS specific options
        if (request.getProtocol() == StreamingProtocol.HLS || request.getProtocol() == StreamingProtocol.BOTH) {
            if (StringUtils.isNotBlank(request.getHlsMediaPlaylistPrefix())) {
                command.add("--hls_media_playlist_prefix");
                command.add(request.getHlsMediaPlaylistPrefix());
            }

            if (Boolean.TRUE.equals(request.getHlsIframePlaylistsOnly())) {
                command.add("--hls_iframe_playlists_only");
            }
        }

        // DASH specific options
        if (request.getProtocol() == StreamingProtocol.DASH || request.getProtocol() == StreamingProtocol.BOTH) {
            if (Boolean.TRUE.equals(request.getDashGenerateStaticLiveProfile())) {
                command.add("--generate_static_live_mpd");
            }

            if (StringUtils.isNotBlank(request.getDashUtcTimings())) {
                command.add("--utc_timings");
                command.add(request.getDashUtcTimings());
            }

            if (request.getMinBufferTime() != null) {
                command.add("--min_buffer_time");
                command.add(String.valueOf(request.getMinBufferTime()));
            }

            if (request.getTimeShiftBufferDepth() != null) {
                command.add("--time_shift_buffer_depth");
                command.add(String.valueOf(request.getTimeShiftBufferDepth()));
            }
        }
    }

    private void addAdvancedOptions(List<String> command, ShakaPackagerRequest request) {
        if (Boolean.TRUE.equals(request.getGenerateStaticLiveProfile())) {
            command.add("--generate_static_live_mpd");
        }

        if (Boolean.FALSE.equals(request.getPreserveOrder())) {
            command.add("--no_preserve_order");
        }

        if (Boolean.TRUE.equals(request.getAllowCodecSwitching())) {
            command.add("--allow_codec_switching");
        }

        if (StringUtils.isNotBlank(request.getBaseUrls())) {
            command.add("--base_urls");
            command.add(request.getBaseUrls());
        }
    }

    // Utility method to generate commands for multiple qualities from MediaConversionRequest
    public ShakaPackagerRequest createFromMediaConversion(MediaConversionRequest mediaRequest,
                                                          StreamingProtocol protocol,
                                                          String inputDirectory,
                                                          String outputDirectory) {
        List<ShakaPackagerRequest.InputStream> streams = new ArrayList<>();

        for (MediaConversionRequest.Profile profile : mediaRequest.getProfile()) {
            String inputFile = Path.of("ffmpeg", profile.getFilename()).toString();

            // Create video stream
            if (profile.getVideoProfile() != null) {
                ShakaPackagerRequest.InputStream videoStream = ShakaPackagerRequest.InputStream.builder()
                        .inputFile(inputFile)
                        .streamType(ShakaPackagerRequest.InputStream.StreamType.VIDEO)
                        .streamSelector("video")
                        .bandwidth(estimateBandwidth(profile.getVideoProfile()))
                        .width(profile.getVideoProfile().getScaleValue())
                        .height(profile.getVideoProfile().getScaleValue())
                        .frameRate(profile.getVideoProfile().getFrameRate() != null ?
                                String.valueOf(profile.getVideoProfile().getFrameRate()) : null)
                        .build();
                streams.add(videoStream);
            }

            // Create audio stream
            if (profile.getAudioProfile() != null) {
                ShakaPackagerRequest.InputStream audioStream = ShakaPackagerRequest.InputStream.builder()
                        .inputFile(inputFile)
                        .streamType(ShakaPackagerRequest.InputStream.StreamType.AUDIO)
                        .streamSelector("audio")
                        .bandwidth(profile.getAudioProfile().getBitrate() != null ?
                                (int)(profile.getAudioProfile().getBitrate() * 1000) : null)
                        .language("eng") // Default to English
                        .build();
                streams.add(audioStream);
            }
        }

        return ShakaPackagerRequest.builder()
                .outputDirectory(outputDirectory)
                .protocol(protocol)
                .masterPlaylistName("master")
                .inputStreams(streams)
                .segmentDuration(SegmentDuration.STANDARD)
                .encryptionMethod(EncryptionMethod.NONE)
                .preserveOrder(true)
                .build();
    }

    private Integer estimateBandwidth(MediaConversionRequest.VideoProfile videoProfile) {
        if (StringUtils.isNotBlank(videoProfile.getMaxRate())) {
            String maxRate = videoProfile.getMaxRate().toLowerCase();
            int bandwidth = Integer.parseInt(maxRate.substring(0, maxRate.length() - 1));
            if (maxRate.endsWith("k")) {
                return bandwidth * 1000;
            } else if (maxRate.endsWith("m")) {
                return bandwidth * 1000000;
            }
        }

        // Estimate based on resolution and CRF if available
        if (videoProfile.getScaleValue() != null) {
            int resolution = videoProfile.getScaleValue();
            if (resolution >= 2160) return 15000000; // 4K
            if (resolution >= 1080) return 5000000;  // 1080p
            if (resolution >= 720) return 2500000;   // 720p
            if (resolution >= 480) return 1000000;   // 480p
        }

        return 2000000; // Default 2 Mbps
    }
}

// Controller for Shaka Packager
//@RestController
//@RequestMapping("/api/shaka-packager")
//public class ShakaPackagerController {
//
//    @Autowired
//    private ShakaPackagerValidationService validationService;
//
//    @Autowired
//    private ShakaPackagerCommandService commandService;
//
//    @PostMapping("/validate")
//    public ResponseEntity<ValidationResult> validateRequest(@RequestBody ShakaPackagerRequest request) {
//        ValidationResult result = validationService.validateRequest(request);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/generate-command")
//    public ResponseEntity<?> generateCommand(@RequestBody ShakaPackagerRequest request) {
//        ValidationResult validation = validationService.validateRequest(request);
//        if (validation.hasErrors()) {
//            return ResponseEntity.badRequest().body(validation);
//        }
//
//        List<String> command = commandService.generateCommand(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("validation", validation);
//        response.put("command", command);
//        response.put("commandString", String.join(" ", command));
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/from-media-conversion")
//    public ResponseEntity<?> generateFromMediaConversion(
//            @RequestBody MediaConversionRequest mediaRequest,
//            @RequestParam StreamingProtocol protocol,
//            @RequestParam String outputDirectory) {
//
//        ShakaPackagerRequest shakaRequest = commandService.createFromMediaConversion(
//                mediaRequest, protocol, outputDirectory);
//
//        ValidationResult validation = validationService.validateRequest(shakaRequest);
//        if (validation.hasErrors()) {
//            return ResponseEntity.badRequest().body(validation);
//        }
//
//        List<String> command = commandService.generateCommand(shakaRequest);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("shakaRequest", shakaRequest);
//        response.put("validation", validation);
//        response.put("command", command);
//        response.put("commandString", String.join(" ", command));
//
//        return ResponseEntity.ok(response);
//    }
//}
