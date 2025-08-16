package com.club69.mediaconvert.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConversionQueueStatus {
    private String status;
    private Integer count;
}
