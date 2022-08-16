package ch.sbb.line.directory.scheduler;

import ch.sbb.line.directory.service.export.ExportService;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportScheduler {

  private final ExportService exportService;

  @Scheduled(cron = "${scheduler.export.line.full.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFullLineVersions", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportFullLineVersions() {
    logStartJob("FullLineVersions - CSV");
    URL urlCsv = exportService.exportFullLineVersionsCsv();
    logEndJob("FullLineVersions - CSV", urlCsv);
    logStartJob("FullLineVersions - ZIP");
    URL urlZip = exportService.exportFullLineVersionsCsvZip();
    logEndJob("FullLineVersions - ZIP", urlZip);
  }

  @Scheduled(cron = "${scheduler.export.line.actual.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportActualLineVersions", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportActualLineVersions() {
    logStartJob("ActualLineVersions - CSV");
    URL urlCsv = exportService.exportActualLineVersionsCsv();
    logEndJob("ActualLineVersions - CSV", urlCsv);
    logStartJob(" ActualLineVersions - ZIP");
    URL urlZip = exportService.exportActualLineVersionsCsvZip();
    logEndJob("ActualLineVersions - ZIP", urlZip);
  }

  @Scheduled(cron = "${scheduler.export.line.future.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersions", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportFutureTimetableLineVersions() {
    logStartJob("FutureTimetableLineVersions - CSV");
    URL urlCsv = exportService.exportFutureTimetableLineVersionsCsv();
    logEndJob("FutureTimetableLineVersions - CSV", urlCsv);
    logStartJob("FutureTimetableLineVersions - ZIP");
    URL urlZip = exportService.exportFutureTimetableLineVersionsCsvZip();
    logEndJob("FutureTimetableLineVersions - ZIP", urlZip);
  }

  private void logStartJob(String jobName) {
    log.info("[SCHEDULER-LINE-EXPORT]: starting export {}", jobName);
  }

  private void logEndJob(String jobName, URL url) {
    log.info("[SCHEDULER-LINE-EXPORT]: ended export {} - [{}]", jobName, url);
  }

}
