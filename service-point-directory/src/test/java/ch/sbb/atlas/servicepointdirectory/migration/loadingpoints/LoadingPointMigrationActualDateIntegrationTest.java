package ch.sbb.atlas.servicepointdirectory.migration.loadingpoints;

import static ch.sbb.atlas.servicepointdirectory.migration.CsvReader.*;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.migration.DateRange;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoadingPointMigrationActualDateIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_LADESTELLEN_20230906011320.csv";
  private static final String ATLAS_CSV_FILE = "actual_date-world-loading_point-2023-09-06.csv";
  private static final LocalDate ACTUAL_DATE = LocalDate.of(2023, 9, 6);

  private static final List<LoadingPointAtlasCsvModel> atlasCsvLines = new ArrayList<>();
  private static final List<LoadingPointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream =
        this.getClass().getResourceAsStream(BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(parseCsv(csvStream, LoadingPointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream =
        this.getClass().getResourceAsStream(BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(parseCsv(csvStream, LoadingPointAtlasCsvModel.class));
    }
    assertThat(atlasCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveOnlyVersionsValidOnActualDate() {
    atlasCsvLines.forEach(atlasCsvLine -> assertThat(
            DateRange.builder()
                .from(dateFromString(atlasCsvLine.getValidFrom()))
                .to(dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(ACTUAL_DATE)
        ).isTrue()
    );
  }

}
