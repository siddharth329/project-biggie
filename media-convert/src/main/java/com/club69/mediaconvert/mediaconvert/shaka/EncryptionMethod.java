package com.club69.mediaconvert.mediaconvert.shaka;

import lombok.Getter;

@Getter
public enum EncryptionMethod {
    NONE("none", "No encryption"),
    AES_128("aes-128", "AES-128 encryption"),
    SAMPLE_AES("sample-aes", "Sample AES encryption"),
    CBCS("cbcs", "CBCS encryption (for FairPlay)"),
    CENC("cenc", "Common Encryption (for Widevine/PlayReady)");

    private final String value;
    private final String description;

    EncryptionMethod(String value, String description) {
        this.value = value;
        this.description = description;
    }

}
