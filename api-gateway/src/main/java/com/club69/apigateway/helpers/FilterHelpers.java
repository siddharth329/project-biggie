package com.club69.apigateway.helpers;

import com.club69.apigateway.configs.CustomDiscoveryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilterHelpers {
    private final CustomDiscoveryProperties customProperties;

    public List<FilterDefinition> createFilters(String serviceId, int stripPrefix,
                                                 ServiceInstance instance,
                                                 CustomDiscoveryProperties.RouteConfig config) {
        List<FilterDefinition> filters = new ArrayList<>();

        // Strip prefix
        addStripPrefixFilter(filters, stripPrefix);

        // Service identification headers
        addHeaderFilter(filters, "X-Service-Name", serviceId);
        addHeaderFilter(filters, "X-Instance-ID", instance.getInstanceId());
        addHeaderFilter(filters, "X-Gateway-Timestamp", String.valueOf(System.currentTimeMillis()));

        // Custom headers from config
        config.getCustomHeaders().forEach((name, value) ->
                addHeaderFilter(filters, name, value));

        // Custom filters from config
        config.getCustomFilters().forEach(filterName -> {
            FilterDefinition filter = new FilterDefinition();
            filter.setName(filterName);
            filters.add(filter);
        });

        // Circuit breaker
        if (config.isCircuitBreakerEnabled()) {
            addCircuitBreakerFilter(filters, serviceId, customProperties);
        }

        // Rate limiting
        if (config.isRateLimitEnabled() && config.getRateLimitRpm() != null) {
            addRateLimitFilter(filters, config.getRateLimitRpm());
        }

        // Global logging filter
//        if (customProperties.getGlobal().isLoggingEnabled()) {
//            addCustomFilter(filters, "RequestLogging");
//        }

        // Timeout filter
        // addTimeoutFilter(filters, customProperties.getGlobal().getDefaultTimeout());

        return filters;
    }

    // Helper methods for adding filters
    private void addStripPrefixFilter(List<FilterDefinition> filters, int parts) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName("StripPrefix");
        filter.addArg("parts", String.valueOf(parts));
        filters.add(filter);
    }

    public void addHeaderFilter(List<FilterDefinition> filters, String name, String value) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName("AddRequestHeader");
        filter.addArg("name", name);
        filter.addArg("value", value);
        filters.add(filter);
    }

    public void addSetPathFilter(List<FilterDefinition> filters, String template) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName("SetPath");
        filter.addArg("template", template);
        filters.add(filter);
    }

    private void addCircuitBreakerFilter(List<FilterDefinition> filters, String serviceId,
                                         CustomDiscoveryProperties customProperties) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName("CircuitBreaker");
        filter.addArg("name", serviceId + "-cb");
        filter.addArg("fallbackUri", "forward:" + customProperties.getGlobal().getFallbackUri() + "/" + serviceId);
        filters.add(filter);
    }

    private void addRateLimitFilter(List<FilterDefinition> filters, String rpm) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName("RequestRateLimiter");
        filter.addArg("redis-rate-limiter.replenishRate", "10");
        filter.addArg("redis-rate-limiter.burstCapacity", rpm);
        filter.addArg("key-resolver", "#{@userKeyResolver}");
        filters.add(filter);
    }

    private void addTimeoutFilter(List<FilterDefinition> filters, int timeoutMs) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName("Timeout");
        filter.addArg("timeout", timeoutMs + "ms");
        filters.add(filter);
    }

    private void addCustomFilter(List<FilterDefinition> filters, String filterName) {
        FilterDefinition filter = new FilterDefinition();
        filter.setName(filterName);
        filters.add(filter);
    }
}
