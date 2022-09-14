package ch.sbb.atlas.base.service.model.configuration;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
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
        resp.addHeader(CORRELATION_ID, currentSpan.context().traceId());
      }
      chain.doFilter(request, response);
    };
  }

}