package com.club69.mediaconvert.validation;

import com.club69.mediaconvert.dto.ShakaPackagerRequest;
import com.club69.mediaconvert.mediaconvert.ValidationResult;
import com.club69.commons.mediaconvert.shaka.EncryptionMethod;
import com.club69.commons.mediaconvert.shaka.KeyRotationPeriod;
import com.club69.commons.mediaconvert.shaka.StreamingProtocol;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

// Validation Service for Shaka Packager
@Component
public class ShakaPackagerValidationService {

    public ValidationResult validateRequest(ShakaPackagerRequest request) {
        ValidationResult result = new ValidationResult();

        validateBasicFields(request, result);
        validateStreams(request, result);
        validateEncryption(request, result);
        validateProtocolSpecific(request, result);

        return result;
    }

    private void validateBasicFields(ShakaPackagerRequest request, ValidationResult result) {
        if (StringUtils.isBlank(request.getOutputDirectory())) {
            result.addError("outputDirectory", "Output directory is required");
        }

        if (request.getProtocol() == null) {
            result.addError("protocol", "Streaming protocol is required");
        }

        if (StringUtils.isBlank(request.getMasterPlaylistName())) {
            result.addError("masterPlaylistName", "Master playlist name is required");
        }

        if (request.getInputStreams() == null || request.getInputStreams().isEmpty()) {
            result.addError("inputStreams", "At least one input stream is required");
        } else if (request.getInputStreams().size() > 20) {
            result.addError("inputStreams", "Maximum 20 input streams allowed");
        }
    }

    private void validateStreams(ShakaPackagerRequest request, ValidationResult result) {
        if (request.getInputStreams() == null) return;

        boolean hasVideo = false;
        boolean hasAudio = false;

        for (int i = 0; i < request.getInputStreams().size(); i++) {
            ShakaPackagerRequest.InputStream stream = request.getInputStreams().get(i);
            String prefix = "inputStreams[" + i + "]";

            if (StringUtils.isBlank(stream.getInputFile())) {
                result.addError(prefix + ".inputFile", "Input file path is required");
            }

            if (stream.getStreamType() == null) {
                result.addError(prefix + ".streamType", "Stream type is required");
            } else {
                switch (stream.getStreamType()) {
                    case VIDEO:
                        hasVideo = true;
                        validateVideoStream(stream, prefix, result);
                        break;
                    case AUDIO:
                        hasAudio = true;
                        validateAudioStream(stream, prefix, result);
                        break;
                    case TEXT:
                        validateTextStream(stream, prefix, result);
                        break;
                }
            }
        }

        if (!hasVideo && !hasAudio) {
            result.addWarning("inputStreams", "No video or audio streams found. Ensure you have media streams for adaptive streaming.");
        }
    }

    private void validateVideoStream(ShakaPackagerRequest.InputStream stream, String prefix, ValidationResult result) {
        if (stream.getBandwidth() != null && stream.getBandwidth() <= 0) {
            result.addError(prefix + ".bandwidth", "Bandwidth must be positive");
        }

        if (stream.getWidth() != null && stream.getWidth() <= 0) {
            result.addError(prefix + ".width", "Width must be positive");
        }

        if (stream.getHeight() != null && stream.getHeight() <= 0) {
            result.addError(prefix + ".height", "Height must be positive");
        }

        if (StringUtils.isNotBlank(stream.getFrameRate())) {
            try {
                double fps = Double.parseDouble(stream.getFrameRate());
                if (fps <= 0 || fps > 120) {
                    result.addWarning(prefix + ".frameRate", "Frame rate should be between 0 and 120 fps");
                }
            } catch (NumberFormatException e) {
                result.addError(prefix + ".frameRate", "Invalid frame rate format");
            }
        }
    }

    private void validateAudioStream(ShakaPackagerRequest.InputStream stream, String prefix, ValidationResult result) {
        if (stream.getBandwidth() != null && (stream.getBandwidth() < 32000 || stream.getBandwidth() > 512000)) {
            result.addWarning(prefix + ".bandwidth", "Audio bandwidth typically ranges from 32kbps to 512kbps");
        }

        if (StringUtils.isNotBlank(stream.getLanguage()) && stream.getLanguage().length() != 3) {
            result.addWarning(prefix + ".language", "Language should be a 3-letter ISO code (e.g., 'eng', 'spa')");
        }
    }

    private void validateTextStream(ShakaPackagerRequest.InputStream stream, String prefix, ValidationResult result) {
        if (StringUtils.isBlank(stream.getLanguage())) {
            result.addWarning(prefix + ".language", "Language is recommended for text streams");
        }
    }

    private void validateEncryption(ShakaPackagerRequest request, ValidationResult result) {
        if (request.getEncryptionMethod() != EncryptionMethod.NONE) {
            if (StringUtils.isBlank(request.getKeyServerUrl())) {
                result.addError("keyServerUrl", "Key server URL is required for encryption");
            }

            if (StringUtils.isBlank(request.getContentId())) {
                result.addError("contentId", "Content ID is required for encryption");
            }
        }

        if (request.getKeyRotationPeriod() != KeyRotationPeriod.DISABLED &&
                request.getEncryptionMethod() == EncryptionMethod.NONE) {
            result.addWarning("keyRotationPeriod", "Key rotation is ignored when encryption is disabled");
        }
    }

    private void validateProtocolSpecific(ShakaPackagerRequest request, ValidationResult result) {
        if (request.getProtocol() == StreamingProtocol.HLS || request.getProtocol() == StreamingProtocol.BOTH) {
            // HLS validations
            if (request.getFragmentDuration() != null) {
                result.addWarning("fragmentDuration", "Fragment duration is primarily for DASH, not HLS");
            }
        }

        if (request.getProtocol() == StreamingProtocol.DASH || request.getProtocol() == StreamingProtocol.BOTH) {
            // DASH validations
            if (request.getMinBufferTime() != null && request.getMinBufferTime() < 2) {
                result.addWarning("minBufferTime", "Minimum buffer time should be at least 2 seconds for DASH");
            }
        }
    }
}
