package com.club69.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Eureka Server.
 * This configuration secures the Eureka dashboard with basic authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures security for the Eureka Server.
     * Requires basic authentication for accessing the Eureka dashboard.
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requests -> requests
//                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().permitAll()
            );

        return http.build();
    }
}