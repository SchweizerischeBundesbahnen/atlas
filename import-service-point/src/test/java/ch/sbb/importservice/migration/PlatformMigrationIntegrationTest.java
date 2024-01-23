package ch.sbb.importservice.migration;

import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlatformMigrationIntegrationTest {

  private static final String DIDOK_PLATFORMS_CSV_FILE = "PRM_PLATFORMS_20240123013648.csv";
  private static final String ATLAS_PLATFORMS_CSV_FILE = "full-platform-2024-01-23.csv";
  private static final List<PlatformCsvModel> didokPlatformCsvLines = new ArrayList<>();
  private static final List<PlatformVersionCsvModel> atlasPlatformCsvLines = new ArrayList<>();

  private final PlatformCsvService platformCsvService;

  @Autowired
  public PlatformMigrationIntegrationTest(PlatformCsvService platformCsvService) {
    this.platformCsvService = platformCsvService;
  }

  @Test
  @Order(1)
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_PLATFORMS_CSV_FILE)) {
      List<PlatformCsvModelContainer> platformCsvModelContainers = platformCsvService.mapToPlatformCsvModelContainers(
          CsvReader.parseCsv(csvStream, PlatformCsvModel.class));
      didokPlatformCsvLines.addAll(platformCsvModelContainers.stream()
          .map(PlatformCsvModelContainer::getCsvModels)
          .flatMap(Collection::stream)
          .toList());

    }
    assertThat(didokPlatformCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_PLATFORMS_CSV_FILE)) {
      atlasPlatformCsvLines.addAll(CsvReader.parseCsv(csvStream, PlatformVersionCsvModel.class));
    }
    assertThat(atlasPlatformCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSamePlatformNumbersInBothCsvs() {

    Set<Integer> didokPlatformNumbers =
        didokPlatformCsvLines.stream().filter(platformCsvModel -> platformCsvModel.getStatus().equals(1))
            .map(MigrationUtil::removeCheckDigit).collect(Collectors.toSet());
    Set<Integer> atlasPlatformNumbers = atlasPlatformCsvLines.stream().map(PlatformVersionCsvModel::getParentNumberServicePoint)
        .collect(Collectors.toSet());

    Set<Integer> difference = atlasPlatformNumbers.stream().filter(e -> !didokPlatformNumbers.contains(e))
        .collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Platform Numbers, which are not in Didok: {}", difference);
    }
    Set<Integer> differenceDidok = didokPlatformNumbers.stream().filter(e -> !atlasPlatformNumbers.contains(e))
        .collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Platform Numbers, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokPlatformNumbers).containsExactlyInAnyOrderElementsOf(atlasPlatformNumbers);
  }

  @Test
  @Order(3)
  void shouldHaveSameValidityOnEachSloid() {

    Map<String, Validity> groupedSloidsDidok = didokPlatformCsvLines.stream().collect(
            Collectors.groupingBy(PlatformCsvModel::getSloid,
                    Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new Validity(
                                    list.stream().map(item -> DateRange.builder()
                                            .from(item.getValidFrom())
                                            .to(item.getValidTo())
                                            .build()
                                    ).collect(Collectors.toList())
                            ).minify())));

    Map<String, Validity> groupedSloidsAtlas = atlasPlatformCsvLines.stream().collect(
            Collectors.groupingBy(PlatformVersionCsvModel::getSloid,
                    Collectors.collectingAndThen(Collectors.toList(),
                            list -> new Validity(
                                    list.stream().map(
                                                    i -> DateRange.builder()
                                                            .from(CsvReader.dateFromString(i.getValidFrom()))
                                                            .to(CsvReader.dateFromString(i.getValidTo())).build())
                                            .collect(Collectors.toList())).minify())));

    List<String> validityErrors = new ArrayList<>();
    groupedSloidsDidok.forEach((sloid, didokValidity) -> {
      Validity atlasValidity = groupedSloidsAtlas.get(sloid);
      if (atlasValidity == null || !atlasValidity.equals(didokValidity)) {
        log.error("error: ", didokValidity.getDateRanges());
        validityErrors.add(
                "ValidityError on sloid: " + sloid + " didokValidity=" + didokValidity.getDateRanges() + ", atlasValidity=" + atlasValidity.getDateRanges());
      }
    });

    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }
    assertThat(validityErrors).isEmpty();
  }

  @Test
  @Order(5)
  void shouldHaveMappedFieldsToAtlasUsingSloidCorrectly() {
    assertThat(atlasPlatformCsvLines).isNotEmpty();
    Map<String, List<PlatformVersionCsvModel>> groupedAtlasPlatforms = atlasPlatformCsvLines.stream()
            .collect(Collectors.groupingBy(PlatformVersionCsvModel::getSloid));

    assertThat(didokPlatformCsvLines).isNotEmpty();
    didokPlatformCsvLines.forEach(didokCsvLine -> {
      PlatformVersionCsvModel atlasCsvLine = findCorrespondingAtlasPlatformVersion(didokCsvLine,
              groupedAtlasPlatforms.get(didokCsvLine.getSloid()));
      new PlatformMappingEquality(didokCsvLine, atlasCsvLine).performCheck();
    });
  }

  private PlatformVersionCsvModel findCorrespondingAtlasPlatformVersion(PlatformCsvModel didokCsvLine,
                                                                          List<PlatformVersionCsvModel> atlasCsvLines) {
    List<PlatformVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
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
