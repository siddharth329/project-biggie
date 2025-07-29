package com.club69.commons.service;

import com.club69.commons.model.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

/**
 * Service interface for S3 operations.
 * This interface defines methods for interacting with AWS S3.
 */
public interface S3Service {
    
    /**
     * Uploads a file to S3.
     * 
     * @param file the file to upload
     * @param key the object key
     * @return the MediaFile object representing the uploaded file
     * @throws IOException if an I/O error occurs
     */
    MediaFile uploadFile(MultipartFile file, String filename, String key) throws IOException;
    
    /**
     * Uploads a file to S3 with a specific content type.
     * 
     * @param inputStream the input stream of the file
     * @param key the object key
     * @param contentType the content type
     * @param size the file size
     * @return the MediaFile object representing the uploaded file
     * @throws IOException if an I/O error occurs
     */
    MediaFile uploadFile(InputStream inputStream, String filename, String key, String contentType, long size) throws IOException;
    
    /**
     * Generates a pre-signed URL for an S3 object.
     * 
     * @param bucketName the bucket name
     * @param objectKey the object key
     * @param duration the duration for which the URL is valid
     * @return the pre-signed URL
     */
    URL generatePresignedUrl(String bucketName, String objectKey, Duration duration);
    
    /**
     * Generates a pre-signed URL for an S3 object using the default expiration time.
     * 
     * @param bucketName the bucket name
     * @param objectKey the object key
     * @return the pre-signed URL
     */
    URL generatePresignedUrl(String bucketName, String objectKey);
    
    /**
     * Deletes an object from S3.
     * 
     * @param bucketName the bucket name
     * @param objectKey the object key
     */
    void deleteObject(String bucketName, String objectKey);

    void deleteObjectByPrefix(String bucketName, String prefix);
    
    /**
     * Copies an object within S3.
     * 
     * @param sourceBucketName the source bucket name
     * @param sourceObjectKey the source object key
     * @param destinationBucketName the destination bucket name
     * @param destinationObjectKey the destination object key
     * @return the MediaFile object representing the copied file
     */
    MediaFile copyObject(String sourceBucketName, String sourceObjectKey, String destinationBucketName, String destinationObjectKey);
    
    /**
     * Checks if an object exists in S3.
     * 
     * @param bucketName the bucket name
     * @param objectKey the object key
     * @return true if the object exists, false otherwise
     */
    boolean objectExists(String bucketName, String objectKey);
}