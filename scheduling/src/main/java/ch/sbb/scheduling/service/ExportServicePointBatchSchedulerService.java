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

  private final ExportServicePointBatchClient exportServicePointBatchClient;

  public ExportServicePointBatchSchedulerService(ExportServicePointBatchClient exportServicePointBatchClient) {
    this.exportServicePointBatchClient = exportServicePointBatchClient;
    this.clientName = "ExportServicePointBatch-Client";
  }

  @SpanTracing
  @Retryable(label = "triggerExportServicePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.service-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportServicePointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportServicePointBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportServicePointBatch,
        "Trigger Export Service Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportTrafficPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.traffic-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportTrafficPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportTrafficPointBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportTrafficPointBatch,
        "Trigger Export Traffic Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportLoadingPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.loading-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportLoadingPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportLoadingPointBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportLoadingPointBatch,
        "Trigger Export Loading Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportStopPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.stop-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportStopPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportStopPointBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportStopPointBatch,
        "Trigger Export Stop Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportPlatformBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.platform-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportPlatformBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportPlatformBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportPlatformBatch,
            "Trigger Export Platform Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportReferencePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.reference-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportReferencePointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportReferencePointBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportReferencePointBatch,
        "Trigger Export Reference Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportContactPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.contact-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportContactPointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportContactPointBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportContactPointBatch,
        "Trigger Export Contact Point Batch");
  }
  @SpanTracing
  @Retryable(label = "triggerExportToiletBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.toilet-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportToiletBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportToiletBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportToiletBatch,
        "Trigger Export Toilet Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportParkingLotBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.parking-lot-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportParkingLotBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportParkingLotBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportParkingLotBatch,
        "Trigger Export ParkingLot Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportRelationBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.relation-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportRelationBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportRelationBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportRelationBatch,
        "Trigger Export Relation Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportTransportCompanyBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.export-service.transport-company-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportTransportCompanyBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerExportTransportCompanyBatch() {
    return executeRequest(exportServicePointBatchClient::postTriggerExportTransportCompanyBatch,
        "Trigger Export TransportCompany Batch");
  }

}
