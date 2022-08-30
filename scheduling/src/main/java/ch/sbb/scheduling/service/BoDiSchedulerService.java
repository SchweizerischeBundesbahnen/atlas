package ch.sbb.scheduling.service;

import ch.sbb.scheduling.client.BoDiClient;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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

  @Scheduled(cron = "${scheduler.bodi.import.tu.crd.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "loadCompaniesFromCRD", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void postLoadCompaniesFromCRD() {
    executeRequest(boDiClient.postLoadCompaniesFromCRD(), "Import Companies from CRD");
  }

  @Scheduled(cron = "${scheduler.bodi.import.tu.bav.chron}", zone = "${scheduler.zone}")
  @SchedulerLock(name = "loadTransportCompaniesFromBav", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
  public void postLoadTransportCompaniesFromBav() {
    executeRequest(boDiClient.postLoadTransportCompaniesFromBav(), "Import Companies from BAV");
  }


}