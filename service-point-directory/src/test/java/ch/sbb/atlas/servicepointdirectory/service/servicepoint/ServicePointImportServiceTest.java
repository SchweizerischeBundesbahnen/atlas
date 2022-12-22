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

  @Autowired
  private ServicePointImportService servicePointImportService;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Test
  void shouldParseServicePointCsvAndSaveInDbSuccessfully() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);

    // parse csv all
    System.out.println("parse all");
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(
        csvStream, Integer.MAX_VALUE);

    assertThat(servicePointCsvModels).hasSize(359616);
    ServicePointCsvModel csvModel = servicePointCsvModels.get(0);
    assertThat(csvModel.getNummer()).isNotNull();
    assertThat(csvModel.getLaendercode()).isNotNull();
    assertThat(csvModel.getDidokCode()).isNotNull();
    assertThat(csvModel.getCreatedAt()).isNotNull();
    assertThat(csvModel.getCreatedBy()).isNotNull();

    // import all
    System.out.println("save all");
    servicePointImportService.importServicePoints(servicePointCsvModels);

    // get
    System.out.println("get by number 7000");

    assertThat(servicePointVersionRepository.count()).isEqualTo(servicePointCsvModels.size());
    final List<ServicePointVersion> savedServicePoints =
        servicePointVersionRepository.findAllByNumber(7000);
    assertThat(savedServicePoints).hasSize(1);
    ServicePointVersion savedServicePointVersion = savedServicePoints.get(0);
    assertThat(savedServicePointVersion.getId()).isNotNull();
    assertThat(savedServicePointVersion.getServicePointGeolocation().getId()).isNotNull();

  }
}
