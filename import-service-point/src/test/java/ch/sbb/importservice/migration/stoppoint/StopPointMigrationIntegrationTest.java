package ch.sbb.importservice.migration.stoppoint;

import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.migration.MigrationUtil;
import ch.sbb.importservice.service.csv.StopPointCsvService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StopPointMigrationIntegrationTest {

  private static final String DIDOK_STOP_PLACE_CSV_FILE = "PRM_STOP_PLACES_20240123013510.csv";
  private static final String ATLAS_STOP_POINT_CSV_FILE = "full-stop-point-2024-01-23.csv";
  private static final List<StopPointCsvModel> didokStopPointCsvLines = new ArrayList<>();
  private static final List<StopPointVersionCsvModel> atlasStopPointCsvLines = new ArrayList<>();

  private final StopPointCsvService stopPointCsvService;

  @Autowired
  public StopPointMigrationIntegrationTest(StopPointCsvService stopPointCsvService) {
    this.stopPointCsvService = stopPointCsvService;
  }

  @Test
  @Order(1)
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_STOP_PLACE_CSV_FILE)) {
      List<StopPointCsvModelContainer> stopPointCsvModelContainers = stopPointCsvService.mapToStopPointCsvModelContainers(
          CsvReader.parseCsv(csvStream, StopPointCsvModel.class));
      didokStopPointCsvLines.addAll(stopPointCsvModelContainers.stream()
          .map(StopPointCsvModelContainer::getStopPointCsvModels)
          .flatMap(Collection::stream)
          .toList());

    }
    assertThat(didokStopPointCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_STOP_POINT_CSV_FILE)) {
      atlasStopPointCsvLines.addAll(CsvReader.parseCsv(csvStream, StopPointVersionCsvModel.class));
    }
    assertThat(atlasStopPointCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameStopPointNumbersInBothCsvs() {

    Set<Integer> didokStopPointNumbers =
        didokStopPointCsvLines.stream().filter(stopPointCsvModel -> stopPointCsvModel.getStatus().equals(1))
            .map(MigrationUtil::removeCheckDigit).collect(Collectors.toSet());
    Set<Integer> atlasStopPointNumbers = atlasStopPointCsvLines.stream().map(StopPointVersionCsvModel::getNumber)
        .collect(Collectors.toSet());

    Set<Integer> difference = atlasStopPointNumbers.stream().filter(e -> !didokStopPointNumbers.contains(e))
        .collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Stop Point Numbers, which are not in Didok: {}", difference);
    }
    Set<Integer> differenceDidok = didokStopPointNumbers.stream().filter(e -> !atlasStopPointNumbers.contains(e))
        .collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Stop Point Numbers, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokStopPointNumbers).containsExactlyInAnyOrderElementsOf(atlasStopPointNumbers);
  }

  @Test
  @Order(3)
  void shouldHaveSameValidityOnEachDidokCode() {
    Map<Integer, Validity> groupedDidokCodes = didokStopPointCsvLines.stream().collect(
        Collectors.groupingBy(MigrationUtil::removeCheckDigit,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> new Validity(
                    list.stream().map(item -> DateRange.builder()
                        .from(item.getValidFrom())
                        .to(item.getValidTo())
                        .build()
                    ).collect(Collectors.toList())
                ).minify())));

    Map<Integer, Validity> groupedAtlasNumbers = atlasStopPointCsvLines.stream().collect(
        Collectors.groupingBy(StopPointVersionCsvModel::getNumber, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(
                        i -> DateRange.builder()
                            .from(CsvReader.dateFromString(i.getValidFrom()))
                            .to(CsvReader.dateFromString(i.getValidTo())).build())
                    .collect(Collectors.toList())).minify())));

    List<String> validityErrors = new ArrayList<>();
    groupedDidokCodes.forEach((didokCode, didokValidity) -> {
      Validity atlasValidity = groupedAtlasNumbers.get(didokCode);
      if (!atlasValidity.equals(didokValidity)) {
        validityErrors.add(
            "ValidityError on didokCode: " + didokCode + " didokValidity=" + didokValidity + ", atlasValidity=" + atlasValidity);
      }
    });

    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }
    assertThat(validityErrors).isEmpty();
  }

  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    assertThat(atlasStopPointCsvLines).isNotEmpty();
    Map<Integer, List<StopPointVersionCsvModel>> groupedAtlasStopPoints = atlasStopPointCsvLines.stream()
        .collect(Collectors.groupingBy(StopPointVersionCsvModel::getNumber));
    assertThat(didokStopPointCsvLines).isNotEmpty();
    didokStopPointCsvLines.forEach(didokCsvLine -> {
      StopPointVersionCsvModel atlasCsvLine = findCorrespondingAtlasStopPointVersion(didokCsvLine,
          groupedAtlasStopPoints.get(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode())));
      new StopPointMappingEquality(didokCsvLine, atlasCsvLine).performCheck();
    });
  }

  private StopPointVersionCsvModel findCorrespondingAtlasStopPointVersion(StopPointCsvModel didokCsvLine,
      List<StopPointVersionCsvModel> atlasCsvLines) {
    List<StopPointVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
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
