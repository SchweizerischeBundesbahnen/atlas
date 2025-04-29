package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.ExportServiceBatchClient;
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
public class ExportServiceBatchSchedulerService extends BaseSchedulerService {

  private static final int MAX_ATTEMPTS = 4;
  private static final int BACKOFF_DELAY = 65000;
  private static final String LOCK_FOR = "PT1M";

  private final ExportServiceBatchClient exportServiceBatchClient;

  public ExportServiceBatchSchedulerService(ExportServiceBatchClient exportServiceBatchClient) {
    this.exportServiceBatchClient = exportServiceBatchClient;
    this.clientName = "ExportServicePointBatch-Client";
  }

  @SpanTracing
  @Retryable(label = "triggerExportServicePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.service-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportServicePointBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportServicePointBatch() {
    return executeRequest(exportServiceBatchClient::exportServicePointBatch,
        "Trigger Export Service Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportTrafficPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.traffic-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportTrafficPointBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportTrafficPointBatch() {
    return executeRequest(exportServiceBatchClient::exportTrafficPointBatch,
        "Trigger Export Traffic Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportLoadingPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.loading-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportLoadingPointBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportLoadingPointBatch() {
    return executeRequest(exportServiceBatchClient::exportLoadingPointBatch,
        "Trigger Export Loading Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportStopPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.stop-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportStopPointBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportStopPointBatch() {
    return executeRequest(exportServiceBatchClient::exportStopPointBatch,
        "Trigger Export Stop Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportPlatformBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.platform-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportPlatformBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportPlatformBatch() {
    return executeRequest(exportServiceBatchClient::exportPlatformBatch,
            "Trigger Export Platform Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportReferencePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.reference-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportReferencePointBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportReferencePointBatch() {
    return executeRequest(exportServiceBatchClient::exportReferencePointBatch,
        "Trigger Export Reference Point Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportContactPointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.contact-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportContactPointBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportContactPointBatch() {
    return executeRequest(exportServiceBatchClient::exportContactPointBatch,
        "Trigger Export Contact Point Batch");
  }
  @SpanTracing
  @Retryable(label = "triggerExportToiletBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.toilet-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportToiletBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportToiletBatch() {
    return executeRequest(exportServiceBatchClient::exportToiletBatch,
        "Trigger Export Toilet Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportParkingLotBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.parking-lot-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportParkingLotBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportParkingLotBatch() {
    return executeRequest(exportServiceBatchClient::exportParkingLotBatch,
        "Trigger Export ParkingLot Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportRelationBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.relation-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportRelationBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportRelationBatch() {
    return executeRequest(exportServiceBatchClient::exportRelationBatch,
        "Trigger Export Relation Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportBusinessOrganisationBatch", retryFor = SchedulingExecutionException.class, maxAttempts =
      MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.business-organisation-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportBusinessOrganisationBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportBusinessOrganisationBatch() {
    return executeRequest(exportServiceBatchClient::exportBusinessOrganisationBatch,
        "Trigger Export Business Organisation Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportTransportCompanyBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS, backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.transport-company-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportTransportCompanyBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportTransportCompanyBatch() {
    return executeRequest(exportServiceBatchClient::exportTransportCompanyBatch,
        "Trigger Export TransportCompany Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportLineBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS,
      backoff =
  @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.line-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportLineBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportLineBatch() {
    return executeRequest(exportServiceBatchClient::exportLineBatch,
        "Trigger Export Line Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportSublineBatch", retryFor = SchedulingExecutionException.class, maxAttempts = MAX_ATTEMPTS,
      backoff =
      @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.subline-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportSublineBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportSublineBatch() {
    return executeRequest(exportServiceBatchClient::exportSublineBatch,
        "Trigger Export Subline Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportTimetableFieldNumberBatch", retryFor = SchedulingExecutionException.class, maxAttempts =
      MAX_ATTEMPTS,
      backoff =
      @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.timetable-field-number-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportTimetableFieldNumberBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportTimetableFieldNumberBatch() {
    return executeRequest(exportServiceBatchClient::exportTimetableFieldNumberBatch,
        "Trigger Export TimetableFieldNumber Batch");
  }

  @SpanTracing
  @Retryable(label = "triggerExportRecordingObligationBatch", retryFor = SchedulingExecutionException.class, maxAttempts =
      MAX_ATTEMPTS,
      backoff =
      @Backoff(delay = BACKOFF_DELAY))
  @Scheduled(cron = "${scheduler.export-service.recording-obligation-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerExportRecordingObligationBatch", lockAtMostFor = LOCK_FOR, lockAtLeastFor = LOCK_FOR)
  public Response postTriggerExportRecordingObligationBatch() {
    return executeRequest(exportServiceBatchClient::exportRecordingObligationBatch,
        "Trigger Export RecordingObligation Batch");
  }

}
