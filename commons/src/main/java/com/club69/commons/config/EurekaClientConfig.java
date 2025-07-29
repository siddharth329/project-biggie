package com.club69.commons.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Common configuration for Eureka client.
 * This configuration enables service discovery and Feign clients for inter-service communication.
 * It can be imported by all microservices to enable registration with Eureka server.
 */
//@Configuration
//@EnableDiscoveryClient
//@EnableFeignClients(basePackages = "com.club69")
public class EurekaClientConfig {
    // This is a marker class that enables Eureka client and Feign clients
    // The actual configuration is done through application.properties/yml in each service
}