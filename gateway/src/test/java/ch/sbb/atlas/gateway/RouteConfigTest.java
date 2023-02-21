package ch.sbb.atlas.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class RouteConfigTest {

  @Mock
  private GatewayRequestLogging gatewayRequestLogging;

  private final RouteConfig routeConfig = new RouteConfig();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldConfigureRoutesCorrectly() {
    // Given
    GatewayConfig gatewayConfig = new GatewayConfig();
    String key = "timetable-field-number";
    String value = "http://timetable-field-number-backend:8080";
    gatewayConfig.setRoutes(Map.of(key, value));

    // When
    RouteLocator routeLocator = routeConfig.routes(
        new RouteLocatorBuilder(createMockGatewayContext()), gatewayConfig, gatewayRequestLogging);

    // Then
    List<Route> routes = routeLocator.getRoutes().collectList().block();
    assertThat(routes).isNotNull().hasSize(1);
    assertThat(routes.get(0).getId()).isEqualTo(key);
    assertThat(routes.get(0).getUri()).hasToString(value);
  }

  private ConfigurableApplicationContext createMockGatewayContext() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.register(PathRoutePredicateFactory.class);
    context.register(RewritePathGatewayFilterFactory.class);
    context.refresh();
    return context;
  }
}