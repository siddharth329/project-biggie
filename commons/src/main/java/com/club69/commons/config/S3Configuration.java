package com.club69.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuration class for AWS S3.
 * This class provides beans for S3Client and S3Presigner that can be used by services
 * to interact with AWS S3 buckets.
 */
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3Configuration {
    
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
    private int signedUrlExpirationMinutes = 60;
    private String endpointOverride;
    
    /**
     * Creates an S3Client bean for interacting with AWS S3.
     * 
     * @return the S3Client
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build();
    }
    
    /**
     * Creates an S3Presigner bean for generating pre-signed URLs for S3 objects.
     * 
     * @return the S3Presigner
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder()
                        .pathStyleAccessEnabled(true).build())
                .build();
    }
}