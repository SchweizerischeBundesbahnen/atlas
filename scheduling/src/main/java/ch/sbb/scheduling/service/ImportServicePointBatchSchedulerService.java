package ch.sbb.scheduling.service;

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
    this.clientName = "ImportServicePointBatch-Client";
  }

  @Retryable(label = "triggerImportServicePointBatch", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.import-service-point.service-point-trigger-batch.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "triggerImportServicePointBatch", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postTriggerImportServicePointBatch() {
    return executeRequest(importServicePointBatchClient.postTriggerImportServicePointBatch(),
        "Trigger Import Service Point Batch");
  }

}