package ir.aliap1376ir.source.microservices;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r ->
                        r.path("/api/categories/**")
                                .uri("lb://CATEGORIES").id("CATEGORIES"))
                .route(r ->
                        r.path("/api/books/**")
                                .uri("lb://BOOKS").id("BOOKS"))
                .build();
    }

}