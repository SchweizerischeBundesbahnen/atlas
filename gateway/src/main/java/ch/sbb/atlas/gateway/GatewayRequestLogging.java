package ch.sbb.atlas.gateway;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GatewayRequestLogging {

  private static final String FEIGN_TRACING_HEADER = "traceparent";
  private static final String CORRELATION_ID_HEADER_NAME = "Correlation-Id";

  private static final Map<String, String> TRACING_HEADERS = Map.of(FEIGN_TRACING_HEADER, FEIGN_TRACING_HEADER,
      "correlationId", CORRELATION_ID_HEADER_NAME);

  public GatewayFilter log() {
    return new OrderedGatewayFilter((exchange, chain) -> chain.filter(exchange)
        .then(Mono.fromRunnable(() -> TRACING_HEADERS.forEach((loggingKey, headerValue) -> {
          ServerHttpRequest request = exchange.getRequest();
          if (request.getHeaders().containsKey(headerValue)) {
            log.info("Routing [{}] request with {}=[{}] to path [{}]", loggingKey, request.getMethod(),
                request.getHeaders().get(headerValue), request.getPath());
          }
          ServerHttpResponse response = exchange.getResponse();
          if (response.getHeaders().containsKey(headerValue)) {
            log.info("Routing [{}] response with {}=[{}] from path [{}] with statusCode=[{}]", request.getMethod(), loggingKey,
                response.getHeaders().get(headerValue), request.getPath(), response.getStatusCode());
          }
        }))), -1);
  }
}