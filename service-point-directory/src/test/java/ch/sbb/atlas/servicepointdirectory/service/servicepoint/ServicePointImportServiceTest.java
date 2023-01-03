package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class ServicePointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";

  private final ServicePointImportService servicePointImportService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  public ServicePointImportServiceTest(ServicePointImportService servicePointImportService,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointImportService = servicePointImportService;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @Test
  void shouldParseCsvCorrectly() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

    assertThat(servicePointCsvModels).isNotEmpty();
    ServicePointCsvModel firstServicePointCsvModel = servicePointCsvModels.get(0);
    assertThat(firstServicePointCsvModel.getNummer()).isNotNull();
    assertThat(firstServicePointCsvModel.getLaendercode()).isNotNull();
    assertThat(firstServicePointCsvModel.getDidokCode()).isNotNull();
    assertThat(firstServicePointCsvModel.getCreatedAt()).isNotNull();
    assertThat(firstServicePointCsvModel.getCreatedBy()).isNotNull();
  }

  @Test
  void shouldParseCsvAndSaveToDB() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

    servicePointImportService.importServicePoints(servicePointCsvModels);

    List<ServicePointVersion> savedServicePoints = servicePointVersionRepository.findAll();
    assertThat(savedServicePoints).isNotEmpty();
    for (ServicePointVersion savedServicePointVersion : savedServicePoints) {
      assertThat(savedServicePointVersion.getId()).isNotNull();
      if (savedServicePointVersion.hasGeolocation()) {
        assertThat(savedServicePointVersion.getServicePointGeolocation().getId()).isNotNull();
      }
    }
  }
}
