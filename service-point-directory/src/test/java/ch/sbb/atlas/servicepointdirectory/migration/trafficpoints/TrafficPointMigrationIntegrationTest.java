package ch.sbb.atlas.servicepointdirectory.migration.trafficpoints;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.migration.Validity;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.io.IOException;
import java.io.InputStream;
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
 class TrafficPointMigrationIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20230906011933.csv";
  private static final String ATLAS_CSV_FILE = "full-world-traffic_point-2023-09-06.csv";

  private static final List<TrafficPointAtlasCsvModel> trafficPointElementCsvModels = new ArrayList<>();
  private static final List<TrafficPointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(CsvReader.parseCsv(csvStream, TrafficPointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
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
  void shouldHaveSameValidityOnEachDidokCode() {
    Map<String, Validity> groupedDidokSloids = didokCsvLines.stream().collect(
        Collectors.groupingBy(TrafficPointDidokCsvModel::getSloid, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> DateRange.builder()
                    .from(i.getValidFrom())
                    .to(i.getValidTo())
                    .build()
                ).collect(Collectors.toList())
            ).minify())
        )
    );

    Map<String, Validity> groupedAtlasSloids = trafficPointElementCsvModels.stream().collect(
        Collectors.groupingBy(TrafficPointAtlasCsvModel::getSloid, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> DateRange.builder()
                    .from(CsvReader.dateFromString(i.getValidFrom()))
                    .to(CsvReader.dateFromString(i.getValidTo()))
                    .build()
                ).collect(Collectors.toList())
            ).minify())
        )
    );

    List<String> validityErrors = new ArrayList<>();
    groupedDidokSloids.forEach((sloid, didokValidity) -> {
      Validity atlasValidity = groupedAtlasSloids.get(sloid);
      if (atlasValidity == null) {
        System.out.println("Didok SLOID [" + sloid + "] not found in ATLAS");
      } else if (!atlasValidity.equals(didokValidity)) {
        validityErrors.add(
            "ValidityError on didokCode: " + sloid + " didokValidity=" + didokValidity + ", atlasValidity=" + atlasValidity);
      }
    });

    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }
    assertThat(validityErrors).isEmpty();
  }

  /**
   * For each Version in didok we will look at the GUELTIG_VON, look up the corresponding Atlas Traffic Point Version (valid on
   * GUELTIG_VON) and do a comparison
   */
  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    Map<String, List<TrafficPointAtlasCsvModel>> groupedAtlasNumbers = trafficPointElementCsvModels.stream()
        .collect(Collectors.groupingBy(TrafficPointAtlasCsvModel::getSloid));

    didokCsvLines.forEach(didokCsvLine -> {
      TrafficPointAtlasCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getSloid()));
      new TrafficPointMappingEquality(didokCsvLine, atlasCsvLine, false).performCheck();
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
