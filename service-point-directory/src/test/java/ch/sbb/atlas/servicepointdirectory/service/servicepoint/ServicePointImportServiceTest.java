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

@IntegrationTest
public class ServicePointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";

  @Autowired
  private ServicePointImportService servicePointImportService;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Test
  void shouldParseCsvCorrectly() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(
        csvStream, 50);

    assertThat(servicePointCsvModels).hasSize(50);
    ServicePointCsvModel firstServicePointCsvModel = servicePointCsvModels.get(0);
    assertThat(firstServicePointCsvModel.getNummer()).isNotNull();
    assertThat(firstServicePointCsvModel.getLaendercode()).isNotNull();
    assertThat(firstServicePointCsvModel.getDidokCode()).isNotNull();
    assertThat(firstServicePointCsvModel.getErstelltAm()).isNotNull();
    assertThat(firstServicePointCsvModel.getErstelltVon()).isNotNull();
  }

  @Test
  void shouldParseCsvAndSaveToDB() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(
        csvStream, 50);

    servicePointImportService.importServicePoints(servicePointCsvModels);

    List<ServicePointVersion> savedServicePoints = servicePointVersionRepository.findAll();
    assertThat(savedServicePoints).hasSize(50);
    for (ServicePointVersion savedServicePointVersion : savedServicePoints) {
      assertThat(savedServicePointVersion.getId()).isNotNull();
      assertThat(savedServicePointVersion.getServicePointGeolocation().getId()).isNotNull();
    }
  }

}
