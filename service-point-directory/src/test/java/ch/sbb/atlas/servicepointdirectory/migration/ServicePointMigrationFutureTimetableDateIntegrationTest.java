package ch.sbb.atlas.servicepointdirectory.migration;

import static ch.sbb.atlas.servicepointdirectory.migration.AtlasCsvReader.dateFromString;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicePointMigrationFutureTimetableDateIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_FUTURE_TIMETABLE_V_2_20230713023455.csv";
  private static final String ATLAS_CSV_FILE = "future_timetable-world-service-point-2023-07-13.csv";
  private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2023, 12, 10);

  private static final String SEPARATOR = "/";

  private static final List<ServicePointVersionCsvModel> atlasCsvLines = new ArrayList<>();
  private static final Map<Integer, ServicePointVersionCsvModel> atlasCsvLinesAsMap = new HashMap<>();
  private static final List<ServicePointCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(ServicePointImportService.parseServicePoints(csvStream));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(AtlasCsvReader.parseAtlasServicePoints(csvStream));
    }
    assertThat(atlasCsvLines).isNotEmpty();

    atlasCsvLines.forEach(line -> atlasCsvLinesAsMap.put(line.getNumber(), line));
  }

  @Test
  @Order(2)
  void shouldHaveSameDidokCodesInBothCsvs() {
    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ServicePointVersionCsvModel::getNumber).collect(Collectors.toSet());

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
  void shouldHaveSameOrGreaterValidityInAtlasDueToMerges() {
    didokCsvLines.forEach(didokCsvLine -> {
      ServicePointVersionCsvModel atlasCsvLine = atlasCsvLinesAsMap.get(didokCsvLine.getDidokCode());

      assertThat(dateFromString(atlasCsvLine.getValidFrom())).isBeforeOrEqualTo(didokCsvLine.getValidFrom());
      assertThat(dateFromString(atlasCsvLine.getValidTo())).isAfterOrEqualTo(didokCsvLine.getValidTo());
    });
  }

  @Test
  @Order(4)
  void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
    atlasCsvLines.forEach(atlasCsvLine -> {
      assertThat(
          new DateRange(dateFromString(atlasCsvLine.getValidFrom()),
              dateFromString(atlasCsvLine.getValidTo()))
              .contains(FUTURE_TIMETABLE_DATE)).isTrue();
    });
  }

  @Test
  @Order(5)
  @Disabled("Does not work. Didok does not export sloid, WGS84WEB. Height gets rounded to 1 decimal point.")
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    didokCsvLines.forEach(didokCsvLine -> {
      ServicePointVersionCsvModel atlasCsvLine = atlasCsvLinesAsMap.get(didokCsvLine.getDidokCode());
      new ServicePointMappingEquality(didokCsvLine, atlasCsvLine).performCheck();
    });
  }

}
