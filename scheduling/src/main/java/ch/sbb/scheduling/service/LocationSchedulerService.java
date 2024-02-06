package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.LocationClient;
import ch.sbb.scheduling.exception.SchedulingExecutionException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationSchedulerService extends BaseSchedulerService {

  private final LocationClient locationClient;

  public LocationSchedulerService(LocationClient locationClient) {
    this.locationClient = locationClient;
    this.clientName = "Location-Client";
  }

  @SpanTracing
  @Retryable(label = "syncSloid", retryFor = SchedulingExecutionException.class, maxAttempts = 4,
      backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.location.sync.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "syncSloid", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response syncSloid() {
    return executeRequest(locationClient::syncSloid, "Sync sloid");
  }

}
