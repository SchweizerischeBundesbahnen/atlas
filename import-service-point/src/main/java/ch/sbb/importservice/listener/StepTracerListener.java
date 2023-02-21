package ch.sbb.importservice.listener;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StepTracerListener implements StepExecutionListener {

  private final Tracer tracer;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    Span currentSpan = tracer.currentSpan();
    if (currentSpan != null) {
      String traceId = currentSpan.context().traceId();
      stepExecution.getExecutionContext().put("traceId", traceId);
      log.info("Putting TraceId to stepExecution: {}", traceId);
    }
  }

}