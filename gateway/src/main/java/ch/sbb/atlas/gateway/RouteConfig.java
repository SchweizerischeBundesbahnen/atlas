package ch.sbb.atlas.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

  @Bean
  public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder, GatewayConfig gatewayConfig,
      GatewayRequestLogging gatewayRequestLogging) {
    Builder routeBuilder = routeLocatorBuilder.routes();
    gatewayConfig.getRoutes().forEach((application, uri) ->
        routeBuilder
            .route(application, p -> p
                .path("/" + application + "/**")
                .filters(f -> f.rewritePath("/" + application + "/(?<path>.*)", "/$\\{path}")
                    .filter(gatewayRequestLogging.log()))
                .uri(uri)
            ));
    return routeBuilder.build();
  }

}
