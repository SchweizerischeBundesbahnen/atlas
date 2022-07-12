package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.exception.AtlasScheduledJobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyImportScheduler {

  private final CompanyService companyService;

  // This is going to be moved to a seperate module in the near future
  @Scheduled(cron = "0 0 2 * * ?") // Every day at 02:00
  public void importCompaniesNightly() {
    log.info("Starting scheduled import of Companies");
    try {
      companyService.saveCompaniesFromCrd();
    } catch (Exception e) {
      throw new AtlasScheduledJobException("CompanyImport", e);
    }
    log.info("Completed scheduled import of Companies");
  }

}
