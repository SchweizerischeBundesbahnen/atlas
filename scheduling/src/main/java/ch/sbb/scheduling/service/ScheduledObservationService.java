package ch.sbb.scheduling.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledObservationService {

  private final ObservationRegistry observationRegistry;

  public void startObservationWithScope() {
    Observation observation = Observation.createNotStarted("schedulerObservation", observationRegistry).start();
    observation.openScope();
  }

  public void stopObservation() {
    observationRegistry.getCurrentObservation().stop();
  }
}
