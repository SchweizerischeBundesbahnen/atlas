package ch.sbb.scheduling.service;

import ch.sbb.scheduling.aspect.annotation.SpanTracing;
import ch.sbb.scheduling.client.BoDiClient;
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
public class BoDiSchedulerService extends BaseSchedulerService {

  private final BoDiClient boDiClient;

  public BoDiSchedulerService(BoDiClient boDiClient) {
    this.boDiClient = boDiClient;
    this.clientName = "BoDi-Client";
  }

  @SpanTracing
  @Retryable(label = "loadCompaniesFromCRD", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay
      = 65000))
  @Scheduled(cron = "${scheduler.bodi.import.tu.crd.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "loadCompaniesFromCRD", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postLoadCompaniesFromCRD() {
    return executeRequest(boDiClient::postLoadCompaniesFromCRD, "Import Companies from CRD");
  }

  @SpanTracing
  @Retryable(label = "loadTransportCompaniesFromBav", retryFor = SchedulingExecutionException.class, maxAttempts = 4, backoff =
  @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.bodi.import.tu.bav.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "loadTransportCompaniesFromBav", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response postLoadTransportCompaniesFromBav() {
    return executeRequest(boDiClient::postLoadTransportCompaniesFromBav,
        "Import Companies from BAV");
  }

  @SpanTracing
  @Retryable(label = "exportFullBusinessOrganisationVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4,
      backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.bodi.export.business-organisation.full.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFullBusinessOrganisationVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportFullBusinessOrganisationVersions() {
    return executeRequest(boDiClient::putBoDiBusinessOrganisationExportFull,
        "Full BusinessOrganisation Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportActualBusinessOrganisationVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4
      , backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.bodi.export.business-organisation.actual.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportActualBusinessOrganisationVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportActualBusinessOrganisationVersions() {
    return executeRequest(boDiClient::putBoDiBusinessOrganisationExportActual,
        "Actual BusinessOrganisation Versions CSV/ZIP");
  }

  @SpanTracing
  @Retryable(label = "exportFutureBusinessOrganisationVersions", retryFor = SchedulingExecutionException.class, maxAttempts = 4
      , backoff = @Backoff(delay = 65000))
  @Scheduled(cron = "${scheduler.bodi.export.business-organisation.future.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "exportFutureBusinessOrganisationVersions", lockAtMostFor = "PT1M", lockAtLeastFor = "PT1M")
  public Response exportNextTimetableBusinessOrganisationVersions() {
    return executeRequest(boDiClient::putBoDiBusinessOrganisationExportNextTimetableVersions,
        "Future Timetable BusinessOrganisation Versions CSV/ZIP");
  }

}