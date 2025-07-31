package com.club69.mediaconvert.mediaconvert;

import com.club69.mediaconvert.dto.MediaConversionRequest;
import com.club69.mediaconvert.mediaconvert.options.HardwareAcceleration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// FFmpeg Command Generator Service
@Component
public class FFmpegCommandGeneratorService {
    @Value("${processor.ffmpeg.ffmpeg.path}")
    private String ffmpegPath;

    public List<String> generateCommand(MediaConversionRequest request,
                                        MediaConversionRequest.Profile profile,
                                        HardwareAcceleration acceleration,
                                        String inputPathGenerated,
                                        String tempDirectoryPath) {
        List<String> command = new ArrayList<>();

        // Basic FFmpeg setup
        command.add(ffmpegPath);
        command.add("-y"); // Overwrite output files

        // Add hardware acceleration setup
        addHardwareAccelerationOptions(command, acceleration);

        // Input file
        command.add("-i");
        // command.add(getInputPath(request));
        command.add(inputPathGenerated);

        // Video encoding options
        addVideoEncodingOptions(command, profile.getVideoProfile(), acceleration);

        // Audio encoding options
        addAudioEncodingOptions(command, profile.getAudioProfile());

        // Container options
        addContainerOptions(command, profile);

        // Output file
        command.add(getOutputPath(request, profile, tempDirectoryPath));

        return command;
    }

    private void addHardwareAccelerationOptions(List<String> command, HardwareAcceleration acceleration) {
        switch (acceleration) {
            case NVENC:
                command.add("-hwaccel");
                command.add("cuda");
                command.add("-hwaccel_output_format");
                command.add("cuda");
                break;
            case QSV:
                command.add("-hwaccel");
                command.add("qsv");
                command.add("-hwaccel_output_format");
                command.add("qsv");
                break;
            case AMF:
                command.add("-hwaccel");
                command.add("d3d11va");
                command.add("-hwaccel_output_format");
                command.add("d3d11");
                break;
            case VIDEOTOOLBOX:
                command.add("-hwaccel");
                command.add("videotoolbox");
                break;
            case SOFTWARE:
            default:
                // No hardware acceleration
                break;
        }
    }

    private void addVideoEncodingOptions(List<String> command,
                                         MediaConversionRequest.VideoProfile videoProfile,
                                         HardwareAcceleration acceleration) {
        if (videoProfile == null) return;

        // Codec
        if (videoProfile.getVideoCodec() != null) {
            command.add("-c:v");
            command.add(videoProfile.getVideoCodec().getCodec(acceleration));
        }

        // Scaling
        if (Boolean.TRUE.equals(videoProfile.getScaleOutput()) &&
                videoProfile.getScaleType() != null && videoProfile.getScaleValue() != null) {
            command.add("-vf");
            String scaleFilter = videoProfile.getScaleType() == MediaConversionRequest.VideoProfile.VideoScaleType.WIDTH ?
                    "scale=" + videoProfile.getScaleValue() + ":-2" :
                    "scale=-2:" + videoProfile.getScaleValue();
            command.add(scaleFilter);
        }

        // Quality settings
        if (videoProfile.getCrf() != null) {
            String crfParam = getCrfParameter(acceleration);
            command.add(crfParam);
            command.add(String.valueOf(videoProfile.getCrf().getValue()));
        } else if (StringUtils.isNotBlank(videoProfile.getMaxRate())) {
            command.add("-b:v");
            command.add(videoProfile.getMaxRate());

            if (StringUtils.isNotBlank(videoProfile.getBufferSize())) {
                command.add("-maxrate");
                command.add(videoProfile.getMaxRate());
                command.add("-bufsize");
                command.add(videoProfile.getBufferSize());
            }
        }

        // Profile and level
        if (videoProfile.getProfile() != null) {
            command.add("-profile:v");
            command.add(videoProfile.getProfile().getValue());
        }

        if (StringUtils.isNotBlank(videoProfile.getLevel())) {
            command.add("-level:v");
            command.add(videoProfile.getLevel());
        }

        // Tune
        if (videoProfile.getTune() != null) {
            String tuneParam = getTuneParameter(acceleration);
            if (tuneParam != null) {
                command.add(tuneParam);
                command.add(videoProfile.getTune().getValue());
            }
        }

        // Pixel format
        if (videoProfile.getPixelFormat() != null) {
            command.add("-pix_fmt");
            command.add(videoProfile.getPixelFormat().getValue());
        }

        // GOP settings
        if (videoProfile.getGroupOfPicturesSize() != null) {
            command.add("-g");
            command.add(String.valueOf(videoProfile.getGroupOfPicturesSize()));
        }

        if (videoProfile.getKeyIntMin() != null) {
            command.add("-keyint_min");
            command.add(String.valueOf(videoProfile.getKeyIntMin()));
        }

        if (videoProfile.getScThreshold() != null) {
            command.add("-sc_threshold");
            command.add(String.valueOf(videoProfile.getScThreshold()));
        }

        // Frame rate
        if (videoProfile.getFrameRate() != null) {
            command.add("-r");
            command.add(String.valueOf(videoProfile.getFrameRate()));
        }
    }

    private void addAudioEncodingOptions(List<String> command,
                                         MediaConversionRequest.AudioProfile audioProfile) {
        if (audioProfile == null) return;

        // Audio codec
        if (StringUtils.isNotBlank(audioProfile.getAudioCodec())) {
            command.add("-c:a");
            command.add(audioProfile.getAudioCodec());
        }

        // Audio bitrate
        if (audioProfile.getBitrate() != null) {
            command.add("-b:a");
            command.add(audioProfile.getBitrate() + "k");
        }

        // Sample rate
        if (audioProfile.getSampleRate() != null) {
            command.add("-ar");
            command.add(String.valueOf(audioProfile.getSampleRate()));
        }

        // Audio channels
        if (audioProfile.getAudioChannel() != null) {
            command.add("-ac");
            command.add(String.valueOf(audioProfile.getAudioChannel()));
        }
    }

    private void addContainerOptions(List<String> command, MediaConversionRequest.Profile profile) {
        if (StringUtils.isNotBlank(profile.getMovFlags())) {
            command.add("-movflags");
            command.add(profile.getMovFlags());
        }
    }

    private String getCrfParameter(HardwareAcceleration acceleration) {
        switch (acceleration) {
            case NVENC:
                return "-cq";
            case QSV:
                return "-global_quality";
            case AMF:
                return "-qp_i"; // AMF uses separate QP parameters
            default:
                return "-crf";
        }
    }

    private String getTuneParameter(HardwareAcceleration acceleration) {
        switch (acceleration) {
            case NVENC, SOFTWARE:
                return "-tune";
            case QSV:
                return "-preset";
            case AMF:
                return "-quality";
            default:
                return null;
        }
    }

    private String getInputPath(MediaConversionRequest request) {
        return "s3://" + request.getInputBucketName() + "/" + request.getObjectKey();
    }

    private String getOutputPath(MediaConversionRequest request, MediaConversionRequest.Profile profile, String tempDirectoryPath) {
//        return "s3://" + request.getOutputBucketName() + "/" +
//                request.getOutputPrefix() + "/" + profile.getFilename();
        return Path.of(tempDirectoryPath, profile.getFilename()).toString();
    }
}
