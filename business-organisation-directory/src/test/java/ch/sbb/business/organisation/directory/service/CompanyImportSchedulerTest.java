package ch.sbb.business.organisation.directory.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CompanyImportSchedulerTest {

  @Mock
  private CompanyService companyService;
  private CompanyImportScheduler companyImportScheduler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    companyImportScheduler = new CompanyImportScheduler(companyService);
  }

  @Test
  void shouldSaveCompaniesFromCrdViaService() {
    // When
    companyImportScheduler.importCompaniesNightly();
    // Then
    verify(companyService).saveCompaniesFromCrd();
  }
}