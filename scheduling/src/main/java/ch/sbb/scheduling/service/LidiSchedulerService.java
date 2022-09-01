package ch.sbb.scheduling.service;

import ch.sbb.scheduling.client.LiDiClient;
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
public class LidiSchedulerService extends BaseSchedulerService {

  private final LiDiClient liDiClient;

  public LidiSchedulerService(LiDiClient liDiClient) {
    this.liDiClient = liDiClient;
    this.clientName = "LiDi-Client";
  }

  @Retryable(label = "exportFullLineVersionsCsv", value = SchedulingExecutionException.class, maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullLineVersionsCsv", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportFullLineVersionsCsv() {
    return executeRequest(liDiClient.putLiDiExportFullCsv(), "Full Line Versions CSV");
  }

  @Retryable(label = "exportFullLineVersionsZip", value = SchedulingExecutionException.class, maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullLineVersionsZip", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportFullLineVersionsZip() {
    return executeRequest(liDiClient.putLiDiExportFullZip(), "Full Line Versions ZIP");
  }

  @Retryable(label = "exportActualLineVersionsCsv", value = SchedulingExecutionException.class, maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersionsCsv", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportActualLineVersionsCsv() {
    return executeRequest(liDiClient.putLiDiExportActualCsv(), "Actual Line Versions CSV");
  }

  @Retryable(label = "exportActualLineVersionsZip", value = SchedulingExecutionException.class, maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersionsZip", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportActualLineVersionsZip() {
    return executeRequest(liDiClient.putLiDiExportActualZip(), "Actual Line Versions ZIP");
  }

  @Retryable(label = "exportFutureTimetableLineVersionsCsv", value = SchedulingExecutionException.class, maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsCsv", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportNextTimetableLineVersionsCsv() {
    return executeRequest(liDiClient.putLiDiExportNextTimetableVersionsCsv(),
        "Future Timetable Line Versions CSV");
  }

  @Retryable(label = "exportFutureTimetableLineVersionsZip", value = SchedulingExecutionException.class, maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsZip", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportNextTimetableLineVersionsZip() {
    return executeRequest(liDiClient.putLiDiExportNextTimetableVersionsZip(),
        "Future Timetable Line Versions ZIP");
  }


}