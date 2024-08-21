package ch.sbb.atlas.servicepointdirectory.controller;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointBulkImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ServicePointBulkImportControllerTest {

  @Mock
  private ServicePointBulkImportService servicePointBulkImportService;

  private ServicePointBulkImportController servicePointBulkImportController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    servicePointBulkImportController = new ServicePointBulkImportController(servicePointBulkImportService);
  }

  @Test
  void shouldBulkUpdateViaService() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .sloid("ch:1:sloid:7000")
            .build())
        .build();
    servicePointBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(servicePointBulkImportService).updateServicePoint(updateContainer);
  }
}