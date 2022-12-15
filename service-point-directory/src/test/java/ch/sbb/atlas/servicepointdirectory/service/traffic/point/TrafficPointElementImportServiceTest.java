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

  @Autowired
  private TrafficPointElementImportService trafficPointElementImportService;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Test
  void shouldParseCsvSuccessfully() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221214011657.csv");
    List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElementss(csvStream);

    assertThat(trafficPointElementCsvModels).isNotEmpty();
  }

  @Test
  void shouldSaveParsedCsvToDb() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221214011657.csv");
    List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElementss(csvStream);

    trafficPointElementImportService.importTrafficPointElements(trafficPointElementCsvModels);

    assertThat(trafficPointElementVersionRepository.count()).isEqualTo(58995);
  }

}