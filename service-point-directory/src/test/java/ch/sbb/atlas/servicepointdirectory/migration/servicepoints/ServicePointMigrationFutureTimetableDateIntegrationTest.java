package ch.sbb.atlas.servicepointdirectory.migration.servicepoints;

import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class ServicePointMigrationFutureTimetableDateIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_FUTURE_TIMETABLE_V_2_20240112021759.csv";
  private static final String ATLAS_CSV_FILE = "future_timetable-world-service_point-2024-01-12.csv";
  private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2024, 12, 15);

  private static final List<ServicePointAtlasCsvModel> atlasCsvLines = new ArrayList<>();
  private static final Map<Integer, ServicePointAtlasCsvModel> atlasCsvLinesAsMap = new HashMap<>();
  private static final List<ServicePointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream =
        this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(CsvReader.parseCsv(csvStream, ServicePointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream =
        this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(CsvReader.parseCsv(csvStream, ServicePointAtlasCsvModel.class));
    }
    assertThat(atlasCsvLines).isNotEmpty();

    atlasCsvLines.forEach(line -> atlasCsvLinesAsMap.put(line.getNumber(), line));
  }

  @Test
  @Order(2)
  void shouldHaveSameDidokCodesInBothCsvs() {
    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointDidokCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ServicePointAtlasCsvModel::getNumber).collect(Collectors.toSet());

    Set<Integer> difference = atlasNumbers.stream().filter(e -> !didokCodes.contains(e)).collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Numbers, which are not in Didok: {}", difference);
    }
    Set<Integer> differenceDidok = didokCodes.stream().filter(e -> !atlasNumbers.contains(e)).collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Codes, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokCodes).containsExactlyInAnyOrderElementsOf(atlasNumbers);
  }

  @Test
  @Order(3)
  void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
    atlasCsvLines.forEach(atlasCsvLine -> assertThat(
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
    didokCsvLines.forEach(didokCsvLine -> {
      ServicePointAtlasCsvModel atlasCsvLine = atlasCsvLinesAsMap.get(didokCsvLine.getDidokCode());
      assertDoesNotThrow(() -> new ServicePointMappingEquality(didokCsvLine, atlasCsvLine, false).performCheck());
    });
  }

}
