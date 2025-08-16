package com.club69.commons.mediaconvert.shaka;

import lombok.Getter;

@Getter
public enum KeyRotationPeriod {
    DISABLED(0, "No key rotation"),
    VERY_FREQUENT(30, "30 seconds"),
    FREQUENT(60, "1 minute"),
    STANDARD(300, "5 minutes"),
    LONG(600, "10 minutes"),
    VERY_LONG(1800, "30 minutes");

    private final int seconds;
    private final String description;

    KeyRotationPeriod(int seconds, String description) {
        this.seconds = seconds;
        this.description = description;
    }

}
