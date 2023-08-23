package ch.sbb.atlas.servicepointdirectory.migration;

import static ch.sbb.atlas.servicepointdirectory.migration.AtlasCsvReader.dateFromString;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
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
public class TrafficPointMigrationActualDateIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_STICHTAG_V_1_20230822011742.csv";
  private static final String ATLAS_CSV_FILE = "actual_date-world-traffic_point-2023-08-22.csv";
  private static final LocalDate ACTUAL_DATE = LocalDate.of(2023, 8, 22);

  private static final List<TrafficPointVersionCsvModel> trafficPointElementCsvModels = new ArrayList<>();
  private static final List<TrafficPointElementCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream =
        this.getClass().getResourceAsStream(TrafficPointMigrationIntegrationTest.BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(TrafficPointElementImportService.parseTrafficPointElements(csvStream));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream =
        this.getClass().getResourceAsStream(TrafficPointMigrationIntegrationTest.BASE_PATH + ATLAS_CSV_FILE)) {
      trafficPointElementCsvModels.addAll(AtlasCsvReader.parseAtlasTraffics(csvStream));
    }
    assertThat(trafficPointElementCsvModels).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameDidokCodesInBothCsvs() {
    Set<String> didokSloids = didokCsvLines.stream().map(TrafficPointElementCsvModel::getSloid).collect(Collectors.toSet());
    Set<String> atlasSloids = trafficPointElementCsvModels.stream().map(TrafficPointVersionCsvModel::getSloid)
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
  void shouldHaveOnlyVersionsValidOnActualDate() {
    trafficPointElementCsvModels.forEach(atlasCsvLine -> {
      assertThat(
          new DateRange(dateFromString(atlasCsvLine.getValidFrom()),
              dateFromString(atlasCsvLine.getValidTo()))
              .contains(ACTUAL_DATE)).isTrue();
    });
  }

  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    Map<String, List<TrafficPointVersionCsvModel>> groupedAtlasNumbers = trafficPointElementCsvModels.stream()
        .collect(Collectors.groupingBy(TrafficPointVersionCsvModel::getSloid));

    didokCsvLines.forEach(didokCsvLine -> {
      TrafficPointVersionCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getSloid()));
      new TrafficPointMappingEquality(didokCsvLine, atlasCsvLine, false).performCheck();
    });
  }

  private TrafficPointVersionCsvModel findCorrespondingAtlasServicePointVersion(TrafficPointElementCsvModel didokCsvLine,
      List<TrafficPointVersionCsvModel> atlasCsvLines) {
    List<TrafficPointVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
        atlasCsvLine -> new DateRange(AtlasCsvReader.dateFromString(atlasCsvLine.getValidFrom()),
            AtlasCsvReader.dateFromString(atlasCsvLine.getValidTo())).contains(
            didokCsvLine.getValidFrom())).toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }

}
