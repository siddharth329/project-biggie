package com.club69.apigateway.configs;

import com.club69.apigateway.helpers.RouteHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import reactor.core.publisher.Flux;


@Configuration
@Slf4j
public class EnhancedRouteDefinitionLocator extends DiscoveryClientRouteDefinitionLocator {
    private final ReactiveDiscoveryClient discoveryClient;
    private final DiscoveryLocatorProperties properties;
    private final CustomDiscoveryProperties customProperties;
    private final SimpleEvaluationContext evalCtxt;
    private final RouteHelpers routeHelpers;

    public EnhancedRouteDefinitionLocator(ReactiveDiscoveryClient discoveryClient,
                                          RouteHelpers routeHelpers,
                                          DiscoveryLocatorProperties properties,
                                          CustomDiscoveryProperties customProperties) {
        super(discoveryClient, properties);
        this.discoveryClient = discoveryClient;
        this.routeHelpers = routeHelpers;
        this.properties = properties;
        this.customProperties = customProperties;
        this.evalCtxt = SimpleEvaluationContext.forReadOnlyDataBinding().build();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        if (!customProperties.isEnabled()) return super.getRouteDefinitions();

        System.out.println("=======================================================================");
        System.out.println("Custom Properties: ");
        System.out.println(customProperties);

        SpelExpressionParser parser = new SpelExpressionParser();
        Expression includeExpr = parser.parseExpression(properties.getIncludeExpression());
        Expression urlExpr = parser.parseExpression(properties.getUrlExpression());

        return discoveryClient.getServices()
                .filter(service -> {
                    System.out.println("Service: " + service);
                    return !customProperties.getExcludedServices().contains(service.toLowerCase());
                })
                .flatMap(discoveryClient::getInstances)
                .filter(instance -> {
                    System.out.println("Instance: " + instance);
                    Boolean include = includeExpr.getValue(evalCtxt, Boolean.class);
                    return include != null && include;
                }).flatMap(instance -> routeHelpers.createRoutesForService(instance, urlExpr, evalCtxt))
                .doOnNext(route -> log.info("Created route: {} -> {}", route.getId(), route.getUri()))
                .onErrorContinue((error, obj) -> {
                    log.error("Error creating route for: {} Error: {}", obj, error.getMessage());
                });
    }
}
