package com.club69.mediaconvert.function;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessExecutorResponse {
    private Integer exitCode;
    private Boolean success;
    private String output;
    private String errorMessage;
}
