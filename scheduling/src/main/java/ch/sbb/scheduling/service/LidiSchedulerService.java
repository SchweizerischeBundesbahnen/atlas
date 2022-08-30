package ch.sbb.scheduling.service;

import ch.sbb.scheduling.client.LiDiClient;
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
  public void exportFullLineVersionsCsv() {
    executeRequest(liDiClient.putLiDiExportFullCsv(), "Full Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportFullLineVersionsZip() {
    executeRequest(liDiClient.putLiDiExportFullZip(), "Full Line Versions ZIP");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportActualLineVersionsCsv() {
    executeRequest(liDiClient.putLiDiExportActualCsv(), "Actual Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportActualLineVersionsZip() {
    executeRequest(liDiClient.putLiDiExportActualZip(), "Actual Line Versions ZIP");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportNextTimetableLineVersionsCsv() {
    executeRequest(liDiClient.putLiDiExportFutureTimetableVersionsCsv(),
        "Future Timetable Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.lidi.export.line.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportNextTimetableLineVersionsZip() {
    executeRequest(liDiClient.putLiDiExportFutureTimetableVersionsZip(),
        "Future Timetable Line Versions ZIP");
  }

}