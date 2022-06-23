package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.exception.AtlasScheduledJobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransportCompanyImportScheduler {

  private final TransportCompanyService transportCompanyService;

  // This is going to be moved to a seperate module in the near future
  @Scheduled(cron = "0 0 1 * * ?") // Every day at 01:00
  public void importTransportCompaniesNightly() {
    log.info("Starting scheduled import of TransportCompanies");
    try {
      transportCompanyService.saveTransportCompaniesFromBav();
    } catch (Exception e) {
      throw new AtlasScheduledJobException("TransportCompanyImport", e);
    }
    log.info("Completed scheduled import of TransportCompanies");
  }

}
