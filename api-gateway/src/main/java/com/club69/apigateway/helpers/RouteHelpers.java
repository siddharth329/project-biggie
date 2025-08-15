package com.club69.apigateway.helpers;

import com.club69.apigateway.configs.CustomDiscoveryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RouteHelpers {
    private final FilterHelpers filterHelpers;
    private final PredicateHelpers predicateHelpers;
    private final CustomDiscoveryProperties customProperties;

    public Flux<RouteDefinition> createRoutesForService(ServiceInstance instance, Expression urlExpr,
                                                         SimpleEvaluationContext evalCtxt) {
        String serviceId = instance.getServiceId().toLowerCase();
        String uri = urlExpr.getValue(evalCtxt, instance, String.class);

        CustomDiscoveryProperties.RouteConfig config = customProperties.getServiceConfigs()
                .getOrDefault(serviceId, new CustomDiscoveryProperties.RouteConfig());

        List<RouteDefinition> routes = new ArrayList<>();

        routes.add(createMainServiceRoute(serviceId, uri, instance, config));

        if (config.isVersioningEnabled()) {
            for (String version : config.getSupportedVersions()) {
                routes.add(createVersionedRoute(serviceId, uri, instance, config, version));
            }
        }

        if (config.isAdminEnabled()) routes.add(createAdminRoute(serviceId, uri, instance, config));
        if (config.isHealthEnabled()) routes.add(createHealthRoute(serviceId, uri, instance, config));

        // Custom path route
        if (config.getCustomPath() != null && !config.getCustomPath().isEmpty()) {
            routes.add(createCustomPathRoute(serviceId, uri, instance, config));
        }

        return Flux.fromIterable(routes);
    }

    private RouteDefinition createMainServiceRoute(String serviceId, String uri,
                                                   ServiceInstance instance,
                                                   CustomDiscoveryProperties.RouteConfig config) {
        RouteDefinition route = new RouteDefinition();
        route.setId(customProperties.getRouteIdPrefix() + serviceId + "-main");
        route.setUri(URI.create(uri));

        // Get custom path mapping or use service ID
        String path = customProperties.getServicePathMapping().getOrDefault(serviceId, serviceId);

        // Predicates
        predicateHelpers.addPathPredicate(route, "/" + path + "/**");
        predicateHelpers.addMethodPredicate(route, "GET,POST,PUT,DELETE,OPTIONS");

        // Filters
        route.setFilters(filterHelpers.createFilters(serviceId, 1, instance, config));

        return route;
    }

    private RouteDefinition createVersionedRoute(String serviceId, String uri,
                                                 ServiceInstance instance,
                                                 CustomDiscoveryProperties.RouteConfig config,
                                                 String version) {
        RouteDefinition route = new RouteDefinition();
        route.setId(customProperties.getRouteIdPrefix() + serviceId + "-" + version);
        route.setUri(URI.create(uri));

        // Predicates
        predicateHelpers.addPathPredicate(route, "/api/" + version + "/" + serviceId + "/**");
        predicateHelpers.addHeaderPredicate(route, "Accept", ".*application/(vnd\\.api\\." + version + "\\+)?json.*");

        // Filters
        List<FilterDefinition> filters = filterHelpers.createFilters(serviceId, 3, instance, config);
        filterHelpers.addHeaderFilter(filters, "X-API-Version", version);
        route.setFilters(filters);

        return route;
    }

    private RouteDefinition createAdminRoute(String serviceId, String uri,
                                             ServiceInstance instance,
                                             CustomDiscoveryProperties.RouteConfig config) {
        RouteDefinition route = new RouteDefinition();
        route.setId(customProperties.getRouteIdPrefix() + serviceId + "-admin");
        route.setUri(URI.create(uri));

        // Predicates
        predicateHelpers.addPathPredicate(route, "/admin/" + serviceId + "/**");
        predicateHelpers.addHeaderPredicate(route, "X-User-Role", "(ADMIN|SUPER_ADMIN)");
        predicateHelpers.addMethodPredicate(route, "GET,POST,PUT,DELETE");

        // Filters
        List<FilterDefinition> filters = filterHelpers.createFilters(serviceId, 2, instance, config);
        filterHelpers.addHeaderFilter(filters, "X-Admin-Access", "true");
        route.setFilters(filters);

        return route;
    }

    private RouteDefinition createHealthRoute(String serviceId, String uri,
                                              ServiceInstance instance,
                                              CustomDiscoveryProperties.RouteConfig config) {
        RouteDefinition route = new RouteDefinition();
        route.setId(customProperties.getRouteIdPrefix() + serviceId + "-health");
        route.setUri(URI.create(uri));

        // Predicates
        predicateHelpers.addPathPredicate(route, "/health/" + serviceId);
        predicateHelpers.addMethodPredicate(route, "GET");

        // Filters
        List<FilterDefinition> filters = new ArrayList<>();
        filterHelpers.addSetPathFilter(filters, "/actuator/health");
        filterHelpers.addHeaderFilter(filters, "X-Health-Check", "gateway");
        route.setFilters(filters);

        return route;
    }

    private RouteDefinition createCustomPathRoute(String serviceId, String uri,
                                                  ServiceInstance instance,
                                                  CustomDiscoveryProperties.RouteConfig config) {
        RouteDefinition route = new RouteDefinition();
        route.setId(customProperties.getRouteIdPrefix() + serviceId + "-custom");
        route.setUri(URI.create(uri));

        // Predicates
        predicateHelpers.addPathPredicate(route, "/" + config.getCustomPath() + "/**");

        // Filters
        route.setFilters(filterHelpers.createFilters(serviceId, 1, instance, config));

        return route;
    }
}
