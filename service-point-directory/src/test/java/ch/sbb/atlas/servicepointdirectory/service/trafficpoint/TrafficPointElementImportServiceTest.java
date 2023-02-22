package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
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

  private static final String CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221222011816.csv";

  private final TrafficPointElementImportService trafficPointElementImportService;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  public TrafficPointElementImportServiceTest(TrafficPointElementImportService trafficPointElementImportService,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.trafficPointElementImportService = trafficPointElementImportService;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @Test
  void shouldParseCsvSuccessfully() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE)) {
      List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElements(
          csvStream);

      TrafficPointElementCsvModel csvModel = trafficPointElementCsvModels.get(0);
      assertThat(csvModel.getTrafficPointElementType()).isNotNull();
      assertThat(csvModel.getCreatedAt()).isNotNull();
      assertThat(csvModel.getCreatedBy()).isNotNull();

      assertThat(trafficPointElementCsvModels).isNotEmpty();
    }
  }

  @Test
  void shouldSaveParsedCsvToDb() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE)) {
      List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElements(
          csvStream);

      trafficPointElementImportService.importTrafficPointElements(trafficPointElementCsvModels);

      assertThat(trafficPointElementVersionRepository.count()).isEqualTo(59088);
    }
  }
}
