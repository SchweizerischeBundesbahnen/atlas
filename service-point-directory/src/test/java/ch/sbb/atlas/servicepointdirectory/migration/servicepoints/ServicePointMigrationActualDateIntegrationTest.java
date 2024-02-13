package ch.sbb.atlas.servicepointdirectory.migration.servicepoints;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicePointMigrationActualDateIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_ACTUALDATE_V_2_20240213013246.csv";
  private static final String ATLAS_CSV_FILE = "actual_date-world-service_point-2024-02-13.csv";
  private static final LocalDate ACTUAL_DATE = LocalDate.of(2024, 2, 13);

  private static final List<ServicePointAtlasCsvModel> atlasCsvLines = new ArrayList<>();
  private static final Map<Integer, ServicePointAtlasCsvModel> atlasCsvLinesAsMap = new HashMap<>();
  private static final List<ServicePointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass()
        .getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(CsvReader.parseCsv(csvStream, ServicePointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass()
        .getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(CsvReader.parseCsv(csvStream, ServicePointAtlasCsvModel.class));
    }
    assertThat(atlasCsvLines).isNotEmpty();

    atlasCsvLines.forEach(line -> atlasCsvLinesAsMap.put(line.getNumber(), line));
  }

  @Test
  @Order(2)
  void shouldHaveSameDidokCodesInBothCsvs() {
    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointDidokCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream()
        // Remove this check as soon a new export with servicePointNumber without checkDigit is generated
        .map(servicePointVersionCsvModel -> ServicePointNumber.removeCheckDigit(servicePointVersionCsvModel.getNumber()))
        .collect(Collectors.toSet());

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
  void shouldHaveOnlyVersionsValidOnActualDate() {
    atlasCsvLines.forEach(atlasCsvLine -> assertThat(
            DateRange.builder()
                .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(ACTUAL_DATE)
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
