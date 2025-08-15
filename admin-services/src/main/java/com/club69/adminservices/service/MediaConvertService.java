package com.club69.adminservices.service;

import com.club69.adminservices.dto.ConversionQueueRequestDto;

import java.util.UUID;

public interface MediaConvertService {
    ConversionQueueRequestDto convertMediaFile(UUID mediaFileId);
}
