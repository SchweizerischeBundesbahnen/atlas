package ch.sbb.importservice.migration.platform;

import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.PlatformCsvService;
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
class PlatformMigrationFutureTimetableIntegrationTest {

  private static final String DIDOK_STOP_PLACE_CSV_FILE = "PRM_PLATFORMS_20240125013756.csv";
  private static final String ATLAS_STOP_POINT_CSV_FILE = "future-timetable-platform-2024-01-25.csv";
  private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2024, 12, 15);

  private static final List<PlatformCsvModel> didokPlatformCsvLines = new ArrayList<>();
  private static final List<PlatformVersionCsvModel> atlasPlatformCsvLines = new ArrayList<>();

  private final PlatformCsvService platformCsvService;

  @Autowired
  public PlatformMigrationFutureTimetableIntegrationTest(PlatformCsvService platformCsvService) {
    this.platformCsvService = platformCsvService;
  }

  @Test
  @Order(1)
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_STOP_PLACE_CSV_FILE)) {
      List<PlatformCsvModelContainer> platformCsvModelContainers = platformCsvService.mapToPlatformCsvModelContainers(
          CsvReader.parseCsv(csvStream, PlatformCsvModel.class));
      didokPlatformCsvLines.addAll(platformCsvModelContainers.stream()
          .map(PlatformCsvModelContainer::getCsvModels)
          .flatMap(Collection::stream)
          .toList());

    }
    assertThat(didokPlatformCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_STOP_POINT_CSV_FILE)) {
      atlasPlatformCsvLines.addAll(CsvReader.parseCsv(csvStream, PlatformVersionCsvModel.class));
    }
    assertThat(atlasPlatformCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
    atlasPlatformCsvLines.forEach(atlasCsvLine -> {
      DateRange dateRange = DateRange.builder()
              .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
              .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
              .build();

      if (!dateRange.contains(FUTURE_TIMETABLE_DATE)) {
        System.out.println("Nicht im Datumsbereich: " + atlasCsvLine);
      }

      assertThat(dateRange.contains(FUTURE_TIMETABLE_DATE)).isTrue();


    });
  }
}
