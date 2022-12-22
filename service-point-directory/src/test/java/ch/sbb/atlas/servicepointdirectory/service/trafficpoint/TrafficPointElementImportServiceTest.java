package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointCsvModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TrafficPointElementImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221222011816.csv";

  @Autowired
  private TrafficPointElementImportService trafficPointElementImportService;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Test
  void shouldParseTrafficPointCsvAndSaveInDbSuccessfully() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    System.out.println("parse all");
    List<TrafficPointElementCsvModel> trafficPointElementCsvModels =
        TrafficPointElementImportService.parseTrafficPointElements(
            csvStream);

    assertThat(trafficPointElementCsvModels).hasSize(59088);
    TrafficPointElementCsvModel csvModel = trafficPointElementCsvModels.get(0);
    assertThat(csvModel.getTrafficPointElementType()).isNotNull();
    assertThat(csvModel.getCreatedAt()).isNotNull();
    assertThat(csvModel.getCreatedBy()).isNotNull();

    // import all
    System.out.println("save all");
    long start = System.currentTimeMillis();
    trafficPointElementImportService.importTrafficPointElements(trafficPointElementCsvModels);
    long end = System.currentTimeMillis();
    System.out.println("Elapsed Time in milli seconds: " + (end - start));

    // get
    assertThat(trafficPointElementVersionRepository.count()).isEqualTo(59088);

    System.out.println("get by service point number 85953646");
    final List<TrafficPointElementVersion> versions = trafficPointElementVersionRepository
        .findAllByServicePointNumber(85953646);
    assertThat(versions).hasSize(5);
    TrafficPointElementVersion version = versions.get(0);
    assertThat(version.getId()).isNotNull();
    assertThat(version.getTrafficPointElementGeolocation().getId()).isNotNull();
  }
}
