package com.club69.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the status of a microservice.
 * This model is used by the admin-services to monitor the status of all microservices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {
    
    private String serviceId;
    private String serviceName;
    private String host;
    private int port;
    private String status;
    private LocalDateTime lastUpdated;
    private String version;
    private Map<String, Object> metadata;
    private Map<String, HealthIndicator> healthIndicators;
    
    /**
     * Enum representing the status of a service.
     */
    public enum Status {
        UP("UP"),
        DOWN("DOWN"),
        UNKNOWN("UNKNOWN"),
        OUT_OF_SERVICE("OUT_OF_SERVICE"),
        STARTING("STARTING");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
    
    /**
     * Represents a health indicator for a service.
     * Health indicators provide detailed information about specific aspects of a service's health.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthIndicator {
        private String name;
        private String status;
        private Map<String, Object> details;
    }
    
    /**
     * Checks if the service is up.
     * 
     * @return true if the service is up, false otherwise
     */
    public boolean isUp() {
        return Status.UP.getValue().equals(status);
    }
    
    /**
     * Checks if the service is down.
     * 
     * @return true if the service is down, false otherwise
     */
    public boolean isDown() {
        return Status.DOWN.getValue().equals(status);
    }
}