package ch.sbb.atlas.gateway;

import java.util.Map;
import java.util.Objects;
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
          logRequest(request, loggingKey, headerValue);

          ServerHttpResponse response = exchange.getResponse();
          logResponse(loggingKey, headerValue, response, request);
        }))), -1);
  }

  private static void logRequest(ServerHttpRequest request, String loggingKey, String headerValue) {
    String correlationIdInfo = "";
    if (request.getHeaders().containsKey(headerValue)) {
      correlationIdInfo = " with " + loggingKey + "=" + request.getHeaders().getFirst(headerValue);
    }
    String credentialId = HeaderUtil.getClientCredentialId(request.getHeaders());
    log.info("Routing request{} to path=[{} {}] for clientId={}", correlationIdInfo, request.getMethod(),
        request.getPath(), credentialId);
  }

  private static void logResponse(String loggingKey, String headerValue, ServerHttpResponse response, ServerHttpRequest request) {
    if (response.getHeaders().containsKey(headerValue)) {
      log.info("Routing response with {}={} from path=[{} {}] with statusCode={}", loggingKey,
          Objects.requireNonNull(response.getHeaders().get(headerValue)).getFirst(), request.getMethod(), request.getPath(),
          response.getStatusCode());
    }
  }
}