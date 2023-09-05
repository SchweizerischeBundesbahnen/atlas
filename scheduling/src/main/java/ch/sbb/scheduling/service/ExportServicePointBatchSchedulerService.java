package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.ExportServicePointBatchClient;
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
public class ExportServicePointBatchSchedulerService extends BaseSchedulerService {

  private final ExportServicePointBatchClient importServicePointBatchClient;

  public ExportServicePointBatchSchedulerService(ExportServicePointBatchClient importServicePointBatchClient) {
    this.importServicePointBatchClient = importServicePointBatchClient;
    this.clientName = "ExportServicePointBatch-Client";
  }

  @SpanTracing
  @Retryable(label = "triggerExportServicePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service-point.service-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportServicePointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportServicePointBatch() {
    return executeRequest(importServicePointBatchClient::postTriggerExportServicePointBatch,
        "Trigger Export Service Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportTrafficPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service-point.traffic-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportTrafficPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportTrafficPointBatch() {
    return executeRequest(importServicePointBatchClient::postTriggerExportTrafficPointBatch,
        "Trigger Export Traffic Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportLoadingPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service-point.loading-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportLoadingPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportLoadingPointBatch() {
    return executeRequest(importServicePointBatchClient::postTriggerExportLoadingPointBatch,
        "Trigger Export Loading Point Batch");
  }

}
