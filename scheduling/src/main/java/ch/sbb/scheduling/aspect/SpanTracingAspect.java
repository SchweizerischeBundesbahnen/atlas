package ch.sbb.scheduling.aspect;

import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Scope;
import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class SpanTracingAspect {

  private final ObservationRegistry observationRegistry;

  @Around("@annotation(ch.sbb.scheduling.aspect.annotation.SpanTracing)")
  public Object executeSpanTracing(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("Start observation...");
    Observation observation = Observation.start("schedulerObservation", observationRegistry).start();
    try (Scope ignored = observation.openScope()) {
      Object proceed = joinPoint.proceed();
      Observation currentObservation = observationRegistry.getCurrentObservation();
      if (currentObservation != null) {
        currentObservation.stop();
        log.info("Stop observation...");
      } else {
        throw new IllegalStateException("observationRegistry is null");
      }
      return proceed;
    }
  }

}
