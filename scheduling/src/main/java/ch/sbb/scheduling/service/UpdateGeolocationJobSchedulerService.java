package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.BulkImportServiceBatchClient;
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
public class UpdateGeolocationJobSchedulerService extends BaseSchedulerService {

  private final BulkImportServiceBatchClient bulkImportServiceBatchClient;

  public UpdateGeolocationJobSchedulerService(BulkImportServiceBatchClient bulkImportServiceBatchClient) {
    this.bulkImportServiceBatchClient = bulkImportServiceBatchClient;
    this.clientName = "ImportServicePointBatch-Client";
  }

  @SpanTracing
  @Retryable(label = "updateGeolocationServicePointJob", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.geo-location-update-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "updateGeolocationServicePointJob", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response updateGeoLocations() {
    return executeRequest(bulkImportServiceBatchClient::triggerUpdateGeolocationServicePointJob, "Update GeoLocation");
  }

}