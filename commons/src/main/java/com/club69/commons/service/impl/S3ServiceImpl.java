package com.club69.commons.service.impl;

import com.club69.commons.config.S3Configuration;
import com.club69.commons.model.MediaFile;
import com.club69.commons.service.S3Service;
import com.club69.commons.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the S3Service interface.
 * This class provides methods for interacting with AWS S3 using the AWS SDK.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Configuration s3Configuration;

    @Override
    public MediaFile uploadFile(MultipartFile file, String filename, String key) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            return uploadFile(inputStream, filename, key, file.getContentType(), file.getSize());
        }
    }

    public MediaFile uploadFile(InputStream inputStream, String filename, String key, String contentType, long size) throws IOException {
        return uploadFile(inputStream, filename, s3Configuration.getBucketName(), key, contentType, size);
    }

    @Override
    public MediaFile uploadFile(InputStream inputStream, String filename, String bucketName, String key, String contentType, long size) throws IOException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", contentType);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Configuration.getBucketName())
                .key(key)
                .contentType(contentType)
                .metadata(metadata)
                .build();
        
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));
            
            log.info("File uploaded successfully to S3. Bucket: {}, Key: {}", s3Configuration.getBucketName(), key);
            
            return MediaFile.builder()
                    .id(UUID.randomUUID())
                    .filename(key)
                    .originalFilename(key)
                    .contentType(contentType)
                    .size(size)
                    .bucketName(s3Configuration.getBucketName())
                    .objectKey(key)
                    .status(MediaFile.MediaStatus.UPLOADED)
                    .type(FileUtils.determineMediaType(contentType))
                    .available(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } catch (S3Exception e) {
            log.error("Error uploading file to S3. Bucket: {}, Key: {}", s3Configuration.getBucketName(), key, e);
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> uploadDirectoryToS3(String directory, String outputPrefix) throws IOException {
        List<String> uploadedFiles = new ArrayList<>();

        // Deleting all the existing files in the public id folder (outputPrefix) in the bucket

        Files
                .walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String fileName = file.getFileName().toString();
                    String s3UploadKey = outputPrefix + "/" + fileName;

                    s3Client.putObject(PutObjectRequest.builder().bucket("original").key(s3UploadKey).build(), RequestBody.fromFile(file));
                    uploadedFiles.add(fileName);
                });

        return uploadedFiles;
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String objectKey, Duration duration) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .build();
        
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        
        log.info("Generated pre-signed URL for S3 object. Bucket: {}, Key: {}, Expiration: {}",
                bucketName, objectKey, presignedRequest.expiration());
        
        return presignedRequest.url();
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String objectKey) {
        Duration duration = Duration.ofMinutes(s3Configuration.getSignedUrlExpirationMinutes());
        return generatePresignedUrl(bucketName, objectKey, duration);
    }

    @Override
    public void deleteObject(String bucketName, String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        
        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Deleted S3 object. Bucket: {}, Key: {}", bucketName, objectKey);
        } catch (S3Exception e) {
            log.error("Error deleting S3 object. Bucket: {}, Key: {}", bucketName, objectKey, e);
            throw e;
        }
    }

    @Override
    public void deleteObjectByPrefix(String bucketName, String prefix) {

        try {
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix.endsWith("/") ? prefix : prefix + "/")
                    .maxKeys(1000).build();

            ListObjectsV2Response listResponse;
            int totalObjectsDeleted = 0;

            do {
                listResponse = s3Client.listObjectsV2(listObjectsV2Request);

                if (!listResponse.contents().isEmpty()) {
                    // Prepare objects for batch deletion
                    List<ObjectIdentifier> objectsToDelete = listResponse
                            .contents()
                            .stream()
                            .map(object -> ObjectIdentifier.builder().key(object.key()).build())
                            .toList();

                    // Delete objects in batch
                    DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                            .bucket(bucketName)
                            .delete(Delete.builder().objects(objectsToDelete).build())
                            .build();

                    DeleteObjectsResponse deleteResponse = s3Client.deleteObjects(deleteRequest);

                    totalObjectsDeleted += deleteResponse.deleted().size();
                }

                // Continue with next batch if truncated
                listObjectsV2Request = listObjectsV2Request.toBuilder()
                        .continuationToken(listResponse.nextContinuationToken())
                        .build();

            } while (listResponse.isTruncated());
        } catch (S3Exception e) {
            log.error("Error deleting S3 sub-folder. Bucket: {}, Prefix: {}", bucketName, prefix, e);
            throw e;
        }
    }

    @Override
    public MediaFile copyObject(String sourceBucketName, String sourceObjectKey,
                               String destinationBucketName, String destinationObjectKey) {
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucketName)
                .sourceKey(sourceObjectKey)
                .destinationBucket(destinationBucketName)
                .destinationKey(destinationObjectKey)
                .build();
        
        try {
            CopyObjectResponse response = s3Client.copyObject(copyObjectRequest);
            
            log.info("Copied S3 object. Source: {}/{}, Destination: {}/{}",
                    sourceBucketName, sourceObjectKey, destinationBucketName, destinationObjectKey);
            
            // Get the metadata of the copied object
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(destinationBucketName)
                    .key(destinationObjectKey)
                    .build();
            
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            
            return MediaFile.builder()
                    .id(UUID.randomUUID())
                    .filename(destinationObjectKey)
                    .originalFilename(sourceObjectKey)
                    .contentType(headObjectResponse.contentType())
                    .size(headObjectResponse.contentLength())
                    .bucketName(destinationBucketName)
                    .objectKey(destinationObjectKey)
                    .status(MediaFile.MediaStatus.UPLOADED)
                    .type(FileUtils.determineMediaType(headObjectResponse.contentType()))
                    .available(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } catch (S3Exception e) {
            log.error("Error copying S3 object. Source: {}/{}, Destination: {}/{}",
                    sourceBucketName, sourceObjectKey, destinationBucketName, destinationObjectKey, e);
            throw e;
        }
    }

    @Override
    public boolean objectExists(String bucketName, String objectKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        
        try {
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking if S3 object exists. Bucket: {}, Key: {}", bucketName, objectKey, e);
            throw e;
        }
    }
}