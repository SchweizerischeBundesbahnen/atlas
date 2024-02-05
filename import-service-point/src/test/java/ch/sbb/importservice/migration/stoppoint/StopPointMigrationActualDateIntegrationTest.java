package ch.sbb.importservice.migration.stoppoint;

import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.StopPointCsvService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StopPointMigrationActualDateIntegrationTest {

  private static final String DIDOK_STOP_PLACE_CSV_FILE = "PRM_STOP_PLACES_20231115022314.csv";
  private static final String ATLAS_STOP_POINT_CSV_FILE = "actual-date-stop-point-2023-11-15.csv";
  private static final LocalDate ACTUAL_DATE = LocalDate.of(2023, 11, 15);

  private static final List<StopPointCsvModel> didokStopPointCsvLines = new ArrayList<>();
  private static final List<StopPointVersionCsvModel> atlasStopPointCsvLines = new ArrayList<>();

  private final StopPointCsvService stopPointCsvService;

  @Autowired
  public StopPointMigrationActualDateIntegrationTest(StopPointCsvService stopPointCsvService) {
    this.stopPointCsvService = stopPointCsvService;
  }

  @Test
  @Order(1)
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_STOP_PLACE_CSV_FILE)) {
      List<StopPointCsvModelContainer> stopPointCsvModelContainers = stopPointCsvService.mapToStopPointCsvModelContainers(
          CsvReader.parseCsv(csvStream, StopPointCsvModel.class));
      didokStopPointCsvLines.addAll(stopPointCsvModelContainers.stream()
          .map(StopPointCsvModelContainer::getStopPointCsvModels)
          .flatMap(Collection::stream)
          .toList());

    }
    assertThat(didokStopPointCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_STOP_POINT_CSV_FILE)) {
      atlasStopPointCsvLines.addAll(CsvReader.parseCsv(csvStream, StopPointVersionCsvModel.class));
    }
    assertThat(atlasStopPointCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveOnlyVersionsValidOnActualDate() {
    atlasStopPointCsvLines.forEach(atlasCsvLine -> assertThat(
            DateRange.builder()
                .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(ACTUAL_DATE)
        ).isTrue()
    );
  }
}
