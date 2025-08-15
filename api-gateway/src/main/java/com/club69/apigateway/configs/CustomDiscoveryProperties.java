package com.club69.apigateway.configs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "gateway.discovery.custom")
@ToString
public class CustomDiscoveryProperties {
    private boolean enabled = true;
    private String routeIdPrefix = "custom-";
    private Map<String, String> servicePathMapping = new HashMap<>();
    private Set<String> excludedServices = new HashSet<>();
    private Map<String, RouteConfig> serviceConfigs = new HashMap<>();
    private GlobalConfig global = new GlobalConfig();

    @Setter
    @Getter
    @ToString
    public static class RouteConfig {
        private boolean adminEnabled = false;
        private boolean healthEnabled = true;
        private boolean versioningEnabled = true;
        private List<String> supportedVersions = Arrays.asList("v1", "v2");
        private boolean circuitBreakerEnabled = false;
        private boolean rateLimitEnabled = false;
        private String rateLimitRpm;
        private Map<String, String> customHeaders = new HashMap<>();
        private List<String> customFilters = new ArrayList<>();
        private String customPath;
    }

    @Setter
    @Getter
    @ToString
    public static class GlobalConfig {
        private boolean corsEnabled = true;
        private boolean loggingEnabled = true;
        private boolean metricsEnabled = true;
        private String fallbackUri = "/fallback";
        private int defaultTimeout = 30000;
    }
}
