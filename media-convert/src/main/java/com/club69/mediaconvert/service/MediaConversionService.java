package com.club69.mediaconvert.service;

import com.club69.mediaconvert.dto.ConversionQueueStatus;
import com.club69.commons.dto.MediaConversionRequest;
import com.club69.mediaconvert.dto.MediaConversionStatus;
import com.club69.mediaconvert.model.ConversionQueue;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for media conversion operations.
 * This interface defines methods for uploading, converting, and managing media files.
 */
public interface MediaConversionService {
    
    /**
     * Converts a media file based on the specified conversion request.
     * 
     * @param request the conversion request
     * @return the conversion response
     */
    ConversionQueue convertMediaFile(MediaConversionRequest request);


    List<List<String>> generateCommand(MediaConversionRequest request);

    /**
     * Gets all conversions for a media file.
     * 
     * @param mediaFileId the ID of the media file
     * @return the list of conversion responses
     */
    List<ConversionQueue> getConversionsForMediaFile(UUID mediaFileId);

    
    /**
     * Cancels a conversion operation.
     * 
     * @param conversionId the ID of the conversion operation
     * @return true if the conversion was cancelled, false otherwise
     */
    boolean cancelConversion(UUID conversionId);

    /**
     * Get a conversion operation.
     *
     * @param conversionId the ID of the conversion operation
     * @return ConversionQueue if the conversion is present, throws error otherwise
     */
    ConversionQueue getConversionById(UUID conversionId);

    List<MediaConversionStatus.Response> getConversionStatusByBatch(List<MediaConversionStatus.Request> conversionIds);

    ConversionQueueStatus getConversionQueueStatus();
}