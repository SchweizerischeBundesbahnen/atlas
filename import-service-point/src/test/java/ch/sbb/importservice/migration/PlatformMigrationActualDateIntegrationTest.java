package ch.sbb.importservice.migration;

import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.PlatformCsvService;
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
class PlatformMigrationActualDateIntegrationTest {

  private static final String DIDOK_PLATFORM_CSV_FILE = "PRM_PLATFORMS_20240117013658.csv";
  private static final String ATLAS_PLATFORM_CSV_FILE = "actual-date-platform-2024-01-17.csv";
  private static final LocalDate ACTUAL_DATE = LocalDate.of(2024, 01, 17);

  private static final List<PlatformCsvModel> didokPlatformCsvLines = new ArrayList<>();
  private static final List<PlatformVersionCsvModel> atlasPlatformCsvLines = new ArrayList<>();

  private final PlatformCsvService platformCsvService;

  @Autowired
  public PlatformMigrationActualDateIntegrationTest(PlatformCsvService platformCsvService) {
    this.platformCsvService = platformCsvService;
  }

  @Test
  @Order(1)
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_PLATFORM_CSV_FILE)) {
      List<PlatformCsvModelContainer> platformCsvModelContainers = platformCsvService.mapToPlatformCsvModelContainers(
          CsvReader.parseCsv(csvStream, PlatformCsvModel.class));
      didokPlatformCsvLines.addAll(platformCsvModelContainers.stream()
          .map(PlatformCsvModelContainer::getPlatformCsvModels)
          .flatMap(Collection::stream)
          .toList());

    }
    assertThat(didokPlatformCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_PLATFORM_CSV_FILE)) {
      atlasPlatformCsvLines.addAll(CsvReader.parseCsv(csvStream, PlatformVersionCsvModel.class));
    }
    assertThat(atlasPlatformCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveOnlyVersionsValidOnActualDate() {
    atlasPlatformCsvLines.forEach(atlasCsvLine -> assertThat(
            DateRange.builder()
                .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(ACTUAL_DATE)
        ).isTrue()
    );
  }
}
