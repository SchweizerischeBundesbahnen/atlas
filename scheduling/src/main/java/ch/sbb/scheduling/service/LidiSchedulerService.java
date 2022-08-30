package ch.sbb.scheduling.service;

import ch.sbb.scheduling.client.LiDiClient;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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

  @Scheduled(cron = "${scheduler.lidi.export.line.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public Response exportFullLineVersionsCsv() {
    return executeRequest(liDiClient.putLiDiExportFullCsv(), "Full Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public Response exportFullLineVersionsZip() {
    return executeRequest(liDiClient.putLiDiExportFullZip(), "Full Line Versions ZIP");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public Response exportActualLineVersionsCsv() {
    return executeRequest(liDiClient.putLiDiExportActualCsv(), "Actual Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public Response exportActualLineVersionsZip() {
    return executeRequest(liDiClient.putLiDiExportActualZip(), "Actual Line Versions ZIP");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public Response exportNextTimetableLineVersionsCsv() {
    return executeRequest(liDiClient.putLiDiExportNextTimetableVersionsCsv(),
        "Future Timetable Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public Response exportNextTimetableLineVersionsZip() {
    return executeRequest(liDiClient.putLiDiExportNextTimetableVersionsZip(),
        "Future Timetable Line Versions ZIP");
  }

}