package ch.sbb.atlas.base.service.model.configuration;

import brave.Span;
import brave.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;

public class CorrelationIdFilterConfig {

  public static final String CORRELATION_ID = "Correlation-Id";

  @Bean
  Filter traceIdInResponseFilter(Tracer tracer) {
    return (request, response, chain) -> {
      Span currentSpan = tracer.currentSpan();
      if (currentSpan != null) {
        HttpServletResponse resp = (HttpServletResponse) response;
        // putting trace id value in [Correlation-Id] response header
        resp.addHeader(CORRELATION_ID, String.valueOf(currentSpan.context().traceId()));
      }
      chain.doFilter(request, response);
    };
  }

}
