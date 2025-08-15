package com.club69.apigateway.controller;

import com.club69.apigateway.configs.CustomDiscoveryProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway/management")
public class RouteManagementController {
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final RouteLocator routeLocator;
    private final ReactiveDiscoveryClient discoveryClient;
    private final CustomDiscoveryProperties customProperties;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Get all active routes
     */
    @GetMapping("/routes")
    public Flux<RouteInfo> getAllRoutes() {
        return routeLocator.getRoutes()
                .map(this::convertToRouteInfo);
    }

    /**
     * Get route definitions
     */
    @GetMapping("/route-definitions")
    public Flux<RouteDefinition> getRouteDefinitions() {
        return routeDefinitionLocator.getRouteDefinitions();
    }

    /**
     * Get routes for a specific service
     */
    @GetMapping("/routes/service/{serviceId}")
    public Flux<RouteInfo> getRoutesByService(@PathVariable String serviceId) {
        return routeLocator.getRoutes()
                .filter(route -> route.getId().contains(serviceId.toLowerCase()))
                .map(this::convertToRouteInfo);
    }

    /**
     * Get all discovered services
     */
    @GetMapping("/services")
    public Flux<ServiceInfo> getAllServices() {
        return discoveryClient.getServices()
                .filter(serviceId -> !customProperties.getExcludedServices().contains(serviceId))
                .flatMap(serviceId ->
                        discoveryClient.getInstances(serviceId)
                                .collectList()
                                .map(instances -> new ServiceInfo(serviceId, instances))
                );
    }

    /**
     * Get service instances for a specific service
     */
    @GetMapping("/services/{serviceId}/instances")
    public Flux<ServiceInstance> getServiceInstances(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    /**
     * Refresh all routes
     */
    @PostMapping("/routes/refresh")
    public Mono<ResponseEntity<Map<String, Object>>> refreshRoutes() {
        return Mono.fromRunnable(() -> eventPublisher.publishEvent(new RefreshRoutesEvent(this)))
                .then(Mono.just(ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Routes refresh event published",
                        "timestamp", System.currentTimeMillis()
                ))));
    }

    /**
     * Get gateway configuration
     */
    @GetMapping("/config")
    public Mono<ResponseEntity<CustomDiscoveryProperties>> getConfiguration() {
        return Mono.just(ResponseEntity.ok(customProperties));
    }

    /**
     * Update service configuration (runtime)
     */
    @PutMapping("/config/service/{serviceId}")
    public Mono<ResponseEntity<Map<String, Object>>> updateServiceConfig(
            @PathVariable String serviceId,
            @RequestBody CustomDiscoveryProperties.RouteConfig newConfig) {

        customProperties.getServiceConfigs().put(serviceId, newConfig);
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));

        return Mono.just(ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Service configuration updated: " + serviceId,
                "config", newConfig,
                "timestamp", System.currentTimeMillis()
        )));
    }

    /**
     * Get route statistics
     */
    @GetMapping("/stats")
    public Mono<ResponseEntity<RouteStats>> getRouteStatistics() {
        return routeLocator.getRoutes()
                .collectList()
                .zipWith(discoveryClient.getServices().collectList())
                .map(tuple -> {
                    List<Route> routes = tuple.getT1();
                    List<String> services = tuple.getT2();

                    Map<String, Long> routesByType = routes.stream()
                            .collect(Collectors.groupingBy(
                                    route -> extractRouteType(route.getId()),
                                    Collectors.counting()
                            ));

                    return new RouteStats(
                            routes.size(),
                            services.size(),
                            services.size() - customProperties.getExcludedServices().size(),
                            routesByType
                    );
                })
                .map(ResponseEntity::ok);
    }

    /**
     * Health check for gateway
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> healthCheck() {
        return routeLocator.getRoutes()
                .collectList()
                .zipWith(discoveryClient.getServices().collectList())
                .map(tuple -> {
                    Map<String, Object> health = new HashMap<>();
                    health.put("status", "UP");
                    health.put("gateway", "operational");
                    health.put("activeRoutes", tuple.getT1().size());
                    health.put("discoveredServices", tuple.getT2().size());
                    health.put("timestamp", System.currentTimeMillis());
                    return ResponseEntity.ok(health);
                });
    }

    // Helper methods and classes
    private RouteInfo convertToRouteInfo(Route route) {
        return new RouteInfo(
                route.getId(),
                route.getUri().toString(),
                route.getPredicate().toString(),
                route.getFilters().stream()
                        .map(filter -> filter.getClass().getSimpleName())
                        .collect(Collectors.toList()),
                extractServiceName(route.getId())
        );
    }

    private String extractRouteType(String routeId) {
        if (routeId.contains("-main")) return "main";
        if (routeId.contains("-admin")) return "admin";
        if (routeId.contains("-health")) return "health";
        if (routeId.contains("-v1") || routeId.contains("-v2")) return "versioned";
        if (routeId.contains("-custom")) return "custom";
        return "other";
    }

    private String extractServiceName(String routeId) {
        String[] parts = routeId.split("-");
        return parts.length > 1 ? parts[1] : "unknown";
    }

    // Data classes
    @Getter
    public static class RouteInfo {
        // Getters
        private final String id;
        private final String uri;
        private final String predicate;
        private final List<String> filters;
        private final String serviceName;

        public RouteInfo(String id, String uri, String predicate, List<String> filters, String serviceName) {
            this.id = id;
            this.uri = uri;
            this.predicate = predicate;
            this.filters = filters;
            this.serviceName = serviceName;
        }

    }

    @Getter
    public static class ServiceInfo {
        // Getters
        private final String serviceId;
        private final List<ServiceInstance> instances;

        public ServiceInfo(String serviceId, List<ServiceInstance> instances) {
            this.serviceId = serviceId;
            this.instances = instances;
        }

        public int getInstanceCount() { return instances.size(); }
    }

    @Getter
    public static class RouteStats {
        // Getters
        private final int totalRoutes;
        private final int totalServices;
        private final int activeServices;
        private final Map<String, Long> routesByType;

        public RouteStats(int totalRoutes, int totalServices, int activeServices, Map<String, Long> routesByType) {
            this.totalRoutes = totalRoutes;
            this.totalServices = totalServices;
            this.activeServices = activeServices;
            this.routesByType = routesByType;
        }

    }
}
