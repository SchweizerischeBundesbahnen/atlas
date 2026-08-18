package ch.sbb.atlas.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RouteConfig {

  @Bean
  public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder, GatewayConfig gatewayConfig,
      GatewayRequestLogging gatewayRequestLogging) {
    Builder routeBuilder = routeLocatorBuilder.routes();

    if (gatewayConfig.isTthModuleReroute()) {
      log.info("TTH Module (new) Reroute is enabled");
      String timetableHearingUri = gatewayConfig.getRoutes().get("timetable-hearing");
      routeBuilder.route("tth-cutover",
          p -> p.predicate(exchange -> {
                String requestPath = exchange.getRequest().getURI().getRawPath();
                return requestPath.startsWith("/line-directory") && requestPath.contains("/timetable-hearing/");
              })
              .filters(f -> f.rewritePath("/line-directory/(?<path>.*)", "/$\\{path}").filter(gatewayRequestLogging))
              .uri(timetableHearingUri));
      routeBuilder.route("tth-year-cutover",
          p -> p.path("/workflow/internal/tth/year/**")
              .filters(f -> f.rewritePath("/workflow/internal/tth/year/(?<path>.*)",
                      "/internal/timetable-hearing/years/$\\{path}")
                  .filter(gatewayRequestLogging))
              .uri(timetableHearingUri));
    }

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

}
