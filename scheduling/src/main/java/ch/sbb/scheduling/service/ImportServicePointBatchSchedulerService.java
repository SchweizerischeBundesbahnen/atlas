package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.ImportServicePointBatchClient;
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
public class ImportServicePointBatchSchedulerService extends BaseSchedulerService {

  private final ImportServicePointBatchClient importServicePointBatchClient;

  public ImportServicePointBatchSchedulerService(ImportServicePointBatchClient importServicePointBatchClient) {
    this.importServicePointBatchClient = importServicePointBatchClient;
    this.clientName = "ImportServicePoint-Client";
  }

  @SpanTracing
  @Retryable(label = "triggerImportServicePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.service-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportServicePointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response triggerImportServicePointBatch() {
    return executeRequest(importServicePointBatchClient::triggerImportServicePointBatch,
        "Trigger Import Service Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerImportTrafficPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.traffic-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportTrafficPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response triggerImportTrafficPointBatch() {
    return executeRequest(importServicePointBatchClient::triggerImportTrafficPointBatch,
        "Trigger Import Traffic Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerImportLoadingPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.loading-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportLoadingPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response triggerImportLoadingPointBatch() {
    return executeRequest(importServicePointBatchClient::triggerImportLoadingPointBatch,
        "Trigger Import Loading Point Batch");
  }


  @SpanTracing
  @Retryable(label = "triggerImportStopPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.stop-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportStopPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response triggerImportStopPointBatch() {
    return executeRequest(importServicePointBatchClient::triggerImportStopPointBatch,
        "Trigger Import Stop Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerImportPlatformBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.platform-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportPlatformBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response triggerImportPlatformBatch() {
    return executeRequest(importServicePointBatchClient::triggerImportPlatformBatch, "Trigger Import Platform Batch");
  }

}
