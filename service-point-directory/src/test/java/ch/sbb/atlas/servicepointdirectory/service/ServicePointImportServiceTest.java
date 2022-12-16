package ch.sbb.atlas.servicepointdirectory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ServicePointImportServiceTest {

  private final ServicePointImportService servicePointImportService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  public ServicePointImportServiceTest(ServicePointImportService servicePointImportService,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointImportService = servicePointImportService;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  // ----------------------------------
  // Dienststellen All V3 Csv Import Tests
  // ----------------------------------
  @Test
  void parseFirst10LinesFromDienststellenAllV3CsvAndImport() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DIDOK3_DIENSTSTELLEN_ALL_V_3_20221216020403.csv");
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream, 10);

    assertThat(servicePointCsvModels).hasSize(10);

    servicePointImportService.importSPCsvModel(servicePointCsvModels);

    List<ServicePointVersion> saved = servicePointVersionRepository.findAll();
    assertThat(saved).hasSize(10);
    for (ServicePointVersion item : saved) {
      assertThat(item.getId()).isNotNull();
      assertThat(item.getServicePointGeolocation().getId()).isNotNull();
    }
  }
  // ----------------------------------
  // Dienststellen All V3 Csv Import Tests End
  // ----------------------------------

}