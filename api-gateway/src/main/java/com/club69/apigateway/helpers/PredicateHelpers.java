package com.club69.apigateway.helpers;

import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

@Component
public class PredicateHelpers {

    // Helper methods for adding predicates
    public void addPathPredicate(RouteDefinition route, String pattern) {
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Path");
        predicate.addArg("pattern", pattern);
        route.getPredicates().add(predicate);
    }

    public void addMethodPredicate(RouteDefinition route, String methods) {
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Method");
        predicate.addArg("methods", methods);
        route.getPredicates().add(predicate);
    }

    public void addHeaderPredicate(RouteDefinition route, String header, String regexp) {
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Header");
        predicate.addArg("header", header);
        predicate.addArg("regexp", regexp);
        route.getPredicates().add(predicate);
    }
}
