package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

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

    // parse csv all
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(
        csvStream, Integer.MAX_VALUE);

    assertThat(servicePointCsvModels).hasSize(359616);
    ServicePointCsvModel firstServicePointCsvModel = servicePointCsvModels.get(0);
    assertThat(firstServicePointCsvModel.getNummer()).isNotNull();
    assertThat(firstServicePointCsvModel.getLaendercode()).isNotNull();
    assertThat(firstServicePointCsvModel.getDidokCode()).isNotNull();
    assertThat(firstServicePointCsvModel.getErstelltAm()).isNotNull();
    assertThat(firstServicePointCsvModel.getErstelltVon()).isNotNull();

    // delete all
    servicePointVersionRepository.deleteAll();

    // import all
    servicePointImportService.importServicePoints(servicePointCsvModels);

    // get
    assertThat(servicePointVersionRepository.count()).isEqualTo(servicePointCsvModels.size());

    final List<ServicePointVersion> savedServicePoints =
        servicePointVersionRepository.findAllByNumber(7000);
    assertThat(savedServicePoints).hasSize(1);
    ServicePointVersion savedServicePointVersion = savedServicePoints.get(0);
    assertThat(savedServicePointVersion.getId()).isNotNull();
    assertThat(savedServicePointVersion.getServicePointGeolocation().getId()).isNotNull();

  }
}
