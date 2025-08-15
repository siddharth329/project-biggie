package com.club69.mediaconvert;

import com.club69.commons.config.EurekaClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Media Convert Application.
 * This service is responsible for handling media file conversion.
 */
// @EntityScan(basePackages = {"com.club69"})
// @Import(EurekaClientConfig.class)

@SpringBootApplication
@ComponentScan(basePackages = {"com.club69"})
@EnableFeignClients
@EnableDiscoveryClient
public class MediaConvertApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaConvertApplication.class, args);
    }
}