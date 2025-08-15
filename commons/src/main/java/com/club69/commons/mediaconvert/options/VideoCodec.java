package com.club69.commons.mediaconvert.options;

// Enhanced Enums for Video Encoding
public enum VideoCodec {
    // Software Codecs
    H264("libx264", "h264_nvenc", "h264_qsv", "h264_amf", "h264_videotoolbox"),
    H265("libx265", "hevc_nvenc", "hevc_qsv", "hevc_amf", "hevc_videotoolbox"),
    VP8("libvpx", null, null, null, null),
    VP9("libvpx-vp9", null, "vp9_qsv", null, null),
    AV1("libaom-av1", "av1_nvenc", "av1_qsv", "av1_amf", null),
    PRORES("prores", null, null, null, "prores_videotoolbox");

    private final String softwareCodec;
    private final String nvencCodec;
    private final String qsvCodec;
    private final String amfCodec;
    private final String videotoolboxCodec;

    VideoCodec(String softwareCodec, String nvencCodec, String qsvCodec, String amfCodec, String videotoolboxCodec) {
        this.softwareCodec = softwareCodec;
        this.nvencCodec = nvencCodec;
        this.qsvCodec = qsvCodec;
        this.amfCodec = amfCodec;
        this.videotoolboxCodec = videotoolboxCodec;
    }

    public String getCodec(HardwareAcceleration acceleration) {
        switch (acceleration) {
            case NVENC:
                return nvencCodec != null ? nvencCodec : softwareCodec;
            case QSV:
                return qsvCodec != null ? qsvCodec : softwareCodec;
            case AMF:
                return amfCodec != null ? amfCodec : softwareCodec;
            case VIDEOTOOLBOX:
                return videotoolboxCodec != null ? videotoolboxCodec : softwareCodec;
            case SOFTWARE:
            default:
                return softwareCodec;
        }
    }

    public boolean supportsHardwareAcceleration(HardwareAcceleration acceleration) {
        switch (acceleration) {
            case NVENC:
                return nvencCodec != null;
            case QSV:
                return qsvCodec != null;
            case AMF:
                return amfCodec != null;
            case VIDEOTOOLBOX:
                return videotoolboxCodec != null;
            case SOFTWARE:
                return true;
            default:
                return false;
        }
    }
}
