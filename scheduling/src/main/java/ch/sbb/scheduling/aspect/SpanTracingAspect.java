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
    Scope scope = observation.openScope();
    Object proceed = joinPoint.proceed();
    if (observationRegistry.getCurrentObservation() != null) {
      observationRegistry.getCurrentObservation().stop();
    } else {
      throw new IllegalStateException("observationRegistry is null");
    }
    scope.close();
    log.info("Stop observation...");
    return proceed;
  }

}
