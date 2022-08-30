package ch.sbb.scheduling.service;

import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SchedulerService {

  private final AtlasClient atlasClient;

  @Scheduled(cron = "${scheduler.export.line.full.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFullLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportFullLineVersionsCsv() {
    executeRequest(atlasClient.putLiDiExportFullCsv(), "Full Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.export.line.full.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFullLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportFullLineVersionsZip() {
    executeRequest(atlasClient.putLiDiExportFullZip(), "Full Line Versions ZIP");
  }

  @Scheduled(cron = "${scheduler.export.line.actual.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportActualLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportActualLineVersionsCsv() {
    executeRequest(atlasClient.putLiDiExportActualCsv(), "Actual Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.export.line.actual.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportActualLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportActualLineVersionsZip() {
    executeRequest(atlasClient.putLiDiExportActualZip(), "Actual Line Versions ZIP");
  }

  @Scheduled(cron = "${scheduler.export.line.future.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsCsv", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportNextTimetableLineVersionsCsv() {
    executeRequest(atlasClient.putLiDiExportFutureTimetableVersionsCsv(),
        "Future Timetable Line Versions CSV");
  }

  @Scheduled(cron = "${scheduler.export.line.future.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersionsZip", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportNextTimetableLineVersionsZip() {
    executeRequest(atlasClient.putLiDiExportFutureTimetableVersionsZip(),
        "Future Timetable Line Versions ZIP");
  }

  private void executeRequest(Response clientCall, String jobName) {
    log.info("Starting Export {}...", jobName);
    try (Response response = clientCall) {
      if (HttpStatus.OK.value() == response.status()) {
        log.info("Export {} Successfully completed", jobName);
      } else {
        log.error(
            "Export {} Unsuccessful completed. Response Status: {} \nResponse: \n{}", jobName,
            response.status(), response);
      }
    }
  }


}