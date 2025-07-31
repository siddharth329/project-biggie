package com.club69.mediaconvert.validation;

import com.club69.mediaconvert.dto.MediaConversionRequest;
import com.club69.mediaconvert.mediaconvert.ValidationResult;
import com.club69.mediaconvert.mediaconvert.options.HardwareAcceleration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MediaConversionValidationService {

    public ValidationResult validateRequest(MediaConversionRequest request, HardwareAcceleration acceleration) {
        ValidationResult result = new ValidationResult();

        // Validate basic fields
        // validateBasicFields(request, result);

        // Validate each profile
        for (int i = 0; i < request.getProfile().size(); i++) {
            validateProfile(request.getProfile().get(i), i, acceleration, result);
        }

        return result;
    }


    private void validateProfile(MediaConversionRequest.Profile profile, int index,
                                 HardwareAcceleration acceleration, ValidationResult result) {
        String prefix = "profile[" + index + "]";

        if (StringUtils.isBlank(profile.getFilename())) {
            result.addError(prefix + ".filename", "Filename is required");
        }

        validateVideoProfile(profile.getVideoProfile(), prefix + ".videoProfile", acceleration, result);
        validateAudioProfile(profile.getAudioProfile(), prefix + ".audioProfile", result);
    }

    private void validateVideoProfile(MediaConversionRequest.VideoProfile videoProfile,
                                      String prefix, HardwareAcceleration acceleration, ValidationResult result) {
        if (videoProfile == null) {
            result.addError(prefix, "Video profile is required");
            return;
        }

        // Validate codec compatibility with hardware acceleration
        if (videoProfile.getVideoCodec() != null &&
                !videoProfile.getVideoCodec().supportsHardwareAcceleration(acceleration)) {
            result.addWarning(prefix + ".videoCodec",
                    "Codec " + videoProfile.getVideoCodec() + " doesn't support " +
                            acceleration.getDescription() + ". Falling back to software encoding.");
        }

        // Validate tune compatibility
        if (videoProfile.getTune() != null && videoProfile.getVideoCodec() != null &&
                !videoProfile.getTune().isCompatibleWith(videoProfile.getVideoCodec(), acceleration)) {
            result.addError(prefix + ".tune",
                    "Tune " + videoProfile.getTune() + " is not compatible with " +
                            videoProfile.getVideoCodec() + " using " + acceleration.getDescription());
        }

        // Validate profile compatibility
        if (videoProfile.getProfile() != null && videoProfile.getVideoCodec() != null &&
                !videoProfile.getProfile().isCompatibleWith(videoProfile.getVideoCodec())) {
            result.addError(prefix + ".profile",
                    "Profile " + videoProfile.getProfile() + " is not compatible with " +
                            videoProfile.getVideoCodec());
        }

        // Validate pixel format compatibility
        if (videoProfile.getPixelFormat() != null && videoProfile.getVideoCodec() != null &&
                !videoProfile.getPixelFormat().isCompatibleWith(
                        videoProfile.getVideoCodec(), videoProfile.getProfile(), acceleration)) {
            result.addError(prefix + ".pixelFormat",
                    "Pixel format " + videoProfile.getPixelFormat() + " is not compatible with the selected codec/profile/acceleration");
        }

        // Validate CRF and rate control mutual exclusivity
        if (videoProfile.getCrf() != null &&
                (StringUtils.isNotBlank(videoProfile.getMaxRate()) || StringUtils.isNotBlank(videoProfile.getBufferSize()))) {
            result.addError(prefix, "Cannot use both CRF and rate control (maxRate/bufferSize) simultaneously");
        }

        // Validate buffer size with max rate
        if (StringUtils.isNotBlank(videoProfile.getMaxRate()) && StringUtils.isBlank(videoProfile.getBufferSize())) {
            result.addWarning(prefix + ".bufferSize", "Buffer size should be specified when using max rate");
        }

        // Validate GOP settings
        if (videoProfile.getKeyIntMin() != null && videoProfile.getGroupOfPicturesSize() != null &&
                videoProfile.getKeyIntMin() > videoProfile.getGroupOfPicturesSize()) {
            result.addError(prefix + ".keyIntMin", "Key interval minimum cannot be greater than GOP size");
        }

        // Validate scale settings
        if (Boolean.TRUE.equals(videoProfile.getScaleOutput())) {
            if (videoProfile.getScaleType() == null) {
                result.addError(prefix + ".scaleType", "Scale type is required when scale output is enabled");
            }
            if (videoProfile.getScaleValue() == null || videoProfile.getScaleValue() <= 0) {
                result.addError(prefix + ".scaleValue", "Valid scale value is required when scale output is enabled");
            }
        }
    }

    private void validateAudioProfile(MediaConversionRequest.AudioProfile audioProfile,
                                      String prefix, ValidationResult result) {
        if (audioProfile == null) {
            result.addError(prefix, "Audio profile is required");
            return;
        }

        if (StringUtils.isBlank(audioProfile.getAudioCodec())) {
            result.addError(prefix + ".audioCodec", "Audio codec is required");
        }

        if (audioProfile.getBitrate() != null && audioProfile.getBitrate() <= 0) {
            result.addError(prefix + ".bitrate", "Audio bitrate must be positive");
        }

        if (audioProfile.getSampleRate() != null && audioProfile.getSampleRate() <= 0) {
            result.addError(prefix + ".sampleRate", "Sample rate must be positive");
        }

        if (audioProfile.getAudioChannel() != null &&
                (audioProfile.getAudioChannel() < 1 || audioProfile.getAudioChannel() > 8)) {
            result.addError(prefix + ".audioChannel", "Audio channels must be between 1 and 8");
        }
    }
}
