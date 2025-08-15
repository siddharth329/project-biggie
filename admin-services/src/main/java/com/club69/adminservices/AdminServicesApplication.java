package com.club69.adminservices;

import com.club69.commons.config.EurekaClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.club69"})
@EntityScan(basePackages = {"com.club69"})
@Import(EurekaClientConfig.class)
public class AdminServicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServicesApplication.class, args);
    }

}
