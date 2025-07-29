package com.club69.mediaconvert;

import com.club69.commons.config.EurekaClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Media Convert Application.
 * This service is responsible for handling media file conversion.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.club69"})
// @EntityScan(basePackages = {"com.club69"})
// @Import(EurekaClientConfig.class)
public class MediaConvertApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaConvertApplication.class, args);
    }
}