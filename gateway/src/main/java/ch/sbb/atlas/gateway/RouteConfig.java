package ch.sbb.atlas.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Slf4j
@Configuration
public class RouteConfig {

  @Bean
  public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder, GatewayConfig gatewayConfig,
      GatewayRequestLogging gatewayRequestLogging) {
    Builder routeBuilder = routeLocatorBuilder.routes();

    routeBuilder.route("tth-legacy-v2",
        p -> p.path("/line-directory/v2/timetable-hearing/**")
            .filters(f -> f.rewritePath("/line-directory/(?<path>.*)", "/$\\{path}")
                .filter(legacyTthV2LoggingFilter())
                .filter(gatewayRequestLogging))
            .uri(gatewayConfig.getRoutes().get("timetable-hearing")));

    gatewayConfig.getRoutes().forEach((application, uri) ->
        routeBuilder
            .route(application, p -> p
                .path("/" + application + "/**")
                .filters(f -> f.rewritePath("/" + application + "/(?<path>.*)", "/$\\{path}")
                    .filter(gatewayRequestLogging))
                .uri(uri)
            ));
    return routeBuilder.build();
  }

  private GatewayFilter legacyTthV2LoggingFilter() {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String clientId = HeaderUtil.getClientCredentialId(request.getHeaders());
      log.warn("Legacy TTH V2 path used: path=[{} {}] for clientId={}", request.getMethod(), request.getPath(), clientId);
      return chain.filter(exchange);
    };
  }

}
