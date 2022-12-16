package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TrafficPointElementImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221216011554.csv";

  @Autowired
  private TrafficPointElementImportService trafficPointElementImportService;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Test
  void shouldParseCsvSuccessfully() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElements(csvStream);

    assertThat(trafficPointElementCsvModels).isNotEmpty();
  }

  @Test
  void shouldSaveParsedCsvToDb() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElements(csvStream);

    trafficPointElementImportService.importTrafficPointElements(trafficPointElementCsvModels);

    assertThat(trafficPointElementVersionRepository.count()).isEqualTo(59055);
  }

}
