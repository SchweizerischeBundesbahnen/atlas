package ch.sbb.business.organisation.directory.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransportCompanyImportSchedulerTest {

  @Mock
  private TransportCompanyService transportCompanyService;
  private TransportCompanyImportScheduler transportCompanyImportScheduler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transportCompanyImportScheduler = new TransportCompanyImportScheduler(
        transportCompanyService);
  }

  @Test
  void shouldSaveTransportCompaniesFromBavViaService() {
    // When
    transportCompanyImportScheduler.importTransportCompaniesNightly();
    // Then
    verify(transportCompanyService).saveTransportCompaniesFromBav();
  }
}