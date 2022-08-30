package ch.sbb.scheduling.service;

import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SchedulerService {

  private final AtlasClient atlasClient;

  @Scheduled(cron = "${scheduler.export.line.full.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFullLineVersions", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportFullLineVersions() {

    log.info("asda");
    try (Response response = atlasClient.putLiDiExportFullCsv()) {
      log.info(response.reason());
    }
  }

  @Scheduled(cron = "${scheduler.export.line.full.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "asd", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public void get() {

    log.info("asda");
    try (Response response = atlasClient.getSomething()) {
      log.info(response.reason());
    }
  }

  @Scheduled(cron = "${scheduler.export.line.actual.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportActualLineVersions", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportActualLineVersions() {
    log.info("asda");
  }

  @Scheduled(cron = "${scheduler.export.line.future.chron}", zone = "${scheduler.export.zone}")
  @SchedulerLock(name = "exportFutureTimetableLineVersions", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void exportNextTimetableLineVersions() {
    log.info("asda");
  }

}