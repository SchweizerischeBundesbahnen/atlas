package ch.sbb.atlas.servicepointdirectory.migration.trafficpoints;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.migration.CsvReader;
import ch.sbb.atlas.servicepointdirectory.migration.DateRange;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class TrafficPointMigrationFutureTimetableIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_FUTURE_TIMETABLE_V_1_20230906012325.csv";
  private static final String ATLAS_CSV_FILE = "future_timetable-world-traffic_point-2023-09-06.csv";
  private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2023, 12, 10);

  private static final List<TrafficPointAtlasCsvModel> trafficPointElementCsvModels = new ArrayList<>();
  private static final List<TrafficPointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream =
        this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(CsvReader.parseCsv(csvStream, TrafficPointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream =
        this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
      trafficPointElementCsvModels.addAll(CsvReader.parseCsv(csvStream, TrafficPointAtlasCsvModel.class));
    }
    assertThat(trafficPointElementCsvModels).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameSloidInBothCsvs() {
    Set<String> didokSloids = didokCsvLines.stream().map(TrafficPointDidokCsvModel::getSloid).collect(Collectors.toSet());
    Set<String> atlasSloids = trafficPointElementCsvModels.stream().map(TrafficPointAtlasCsvModel::getSloid)
        .collect(Collectors.toSet());

    Set<String> difference = atlasSloids.stream().filter(e -> !didokSloids.contains(e)).collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Sloids, which are not in Didok: {}", difference);
    }
    Set<String> differenceDidok = didokSloids.stream().filter(e -> !atlasSloids.contains(e)).collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Sloids, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokSloids).containsExactlyInAnyOrderElementsOf(atlasSloids);
  }

  @Test
  @Order(3)
  void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
    trafficPointElementCsvModels.forEach(atlasCsvLine -> assertThat(
            DateRange.builder()
                .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(FUTURE_TIMETABLE_DATE)
        ).isTrue()
    );
  }

  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    Map<String, List<TrafficPointAtlasCsvModel>> groupedAtlasNumbers = trafficPointElementCsvModels.stream()
        .collect(Collectors.groupingBy(TrafficPointAtlasCsvModel::getSloid));

    didokCsvLines.forEach(didokCsvLine -> {
      TrafficPointAtlasCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getSloid()));
      new TrafficPointMappingEquality(didokCsvLine, atlasCsvLine, true).performCheck();
    });
  }

  private TrafficPointAtlasCsvModel findCorrespondingAtlasServicePointVersion(TrafficPointDidokCsvModel didokCsvLine,
      List<TrafficPointAtlasCsvModel> atlasCsvLines) {
    List<TrafficPointAtlasCsvModel> matchedVersions = atlasCsvLines.stream().filter(
            atlasCsvLine -> DateRange.builder()
                .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(didokCsvLine.getValidFrom()))
        .toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }

}
