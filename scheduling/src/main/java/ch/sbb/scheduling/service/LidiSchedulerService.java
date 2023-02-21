package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
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

  @SpanTracing
  @Retryable(label = "exportFullLineVersions", value = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullLineVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportFullLineVersions() {
    return executeRequest(liDiClient::putLiDiLineExportFull, "Full Line Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportActualLineVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportActualLineVersions() {
    return executeRequest(liDiClient::putLiDiLineExportActual, "Actual Line Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportFutureTimetableLineVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4,
      backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportNextTimetableLineVersions() {
    return executeRequest(liDiClient::putLiDiLineExportNextTimetableVersions,
        "Future Timetable Line Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportFullSublineVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.subline.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullSublineVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportFullSublineVersions() {
    return executeRequest(liDiClient::putLiDiSublineExportFull, "Full Subline Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportActualSublineVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.subline.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualSublineLineVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportActualSublineVersions() {
    return executeRequest(liDiClient::putLiDiSublineExportActual,
        "Actual Subline Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportFutureTimetableSublineVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4,
      backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.subline.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableSublineVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportNextTimetableSublineVersions() {
    return executeRequest(liDiClient::putLiDiSublineExportNextTimetableVersions,
        "Future Timetable Subline Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportFullTimetableFieldNumberVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4,
      backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.timetable.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullTimetableFieldNumberVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportFullTimetableFieldNumberVersions() {
    return executeRequest(liDiClient::putLiDiTimetableFieldNumberExportFull,
        "Full TimetableFieldNumber Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportActualTimetableFieldNumberVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4
      , backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.timetable.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualTimetableFieldNumberVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportActualTimetableFieldNumberVersions() {
    return executeRequest(liDiClient::putLiDiTimetableFieldNumberExportActual,
        "Actual TimetableFieldNumber Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportFutureTimetableTimetableFieldNumberVersions", retryFor = SchedulingExecutionException.class,
      maxAttempts = 4, backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.lidi.export.timetable.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableTimetableFieldNumberSublineVersions", lockAtMostFor = "PT1M", lockAtLeastFor =
      "PT1M")
  public Response exportNextTimetableTimetableFieldNumberVersions() {
    return executeRequest(liDiClient::putLiDiTimetableFieldNumberExportNextTimetableVersions,
        "Future Timetable TimetableFieldNumber Versions CSV/ZIP");
  }

}